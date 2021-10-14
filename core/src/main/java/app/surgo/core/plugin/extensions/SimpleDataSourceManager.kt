package app.surgo.core.plugin.extensions

import android.content.Context
import android.content.pm.PackageManager
import app.surgo.core.datastore.PreferenceStorage
import app.surgo.core.plugin.PluginDescriptor
import app.surgo.core.plugin.PluginManager
import app.surgo.data.DataSourceManager
import com.tsukiymk.surgo.openapi.DataSourceFactory
import dalvik.system.PathClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SimpleDataSourceManager(
    private val context: Context,
    private val pluginManager: PluginManager,
    private val preferenceStorage: PreferenceStorage
): DataSourceManager {
    private val coroutineJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    private val loadedDataSources = HashMap<Long, DataSourceFactory>()

    private var _selectedSource = 0L
    private val internalDataSourceFactory by lazy {
        InternalDataSourceFactory()
    }

    init {
        coroutineScope.launch {
            combine(
                pluginManager.observableInstalled,
                preferenceStorage.selectedDataSource
            ) { plugins, selectedDataSource ->
                plugins.filter { !loadedDataSources.containsKey(it.id) }
                    .map { descriptor ->
                        val beanClass = descriptor.extensions.orEmpty()
                            .find { it.name == "datasource" }?.beanClass
                        if (beanClass != null) {
                            loadClass(descriptor, beanClass)
                        }
                    }

                plugins.find { selectedDataSource == it.packageName }
                    ?.let { plugin ->
                        _selectedSource = plugin.id
                        pluginManager.messageBus.subscribeDataSource.emit(plugin.id)
                    }
            }.collect()
        }
    }

    private fun loadClass(
        descriptor: PluginDescriptor,
        beanClass: String
    ) {
        val packageManager = context.packageManager
        val applicationInfo = try {
            packageManager.getApplicationInfo(descriptor.packageName, PackageManager.GET_META_DATA)
        } catch (error: PackageManager.NameNotFoundException) {
            throw error
        }
        val classLoader = PathClassLoader(
            applicationInfo.sourceDir,
            null,
            context.classLoader
        )

        try {
            when (
                val instance = Class.forName(
                    beanClass,
                    false,
                    classLoader
                ).newInstance()
            ) {
                is DataSourceFactory -> {
                    loadedDataSources[descriptor.id] = instance
                }
                else -> throw Exception("Unknown source class type! ${instance.javaClass}")
            }
        } catch (error: Throwable) {
            throw error
        }
    }

    override val selectedSource: Long
        get() = _selectedSource

    override operator fun get(key: Long): DataSourceFactory {
        return loadedDataSources[key] ?: internalDataSourceFactory
    }
}
