package app.surgo.core.plugin.extensions

import android.content.Context
import android.content.pm.PackageManager
import app.surgo.core.datastore.PreferenceStorage
import app.surgo.core.plugin.PluginManager
import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.DataSourceFactory
import dalvik.system.PathClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SimpleDataSourceManager(
    private val context: Context,
    private val pluginManager: PluginManager,
    private val preferenceStorage: PreferenceStorage
): DataSourceManager {
    private val coroutineJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    private val factoryMap = HashMap<Long, DataSourceFactory>()

    init {
        coroutineScope.launch {
            combine(
                pluginManager.observableInstalled,
                preferenceStorage.selectedDataSource
            ) { plugins, selectedDataSource ->
                plugins.find { it.packageName == selectedDataSource }
                    ?.let { plugin ->
                        val beanClass = plugin.extensions.orEmpty()
                            .find { it.name == "datasource" }
                            ?.beanClass
                        if (beanClass != null) {
                            loadClass(plugin.packageName, beanClass)
                            pluginManager.messageBus.subscribeDataSource.emit(key)
                        }
                    }
            }.collect()
        }
    }

    private fun loadClass(
        packageName: String,
        beanClass: String
    ) {
        val packageManager = context.packageManager
        val applicationInfo = try {
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
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
                    val bytes = MessageDigest.getInstance("MD5")
                        .digest(factory.name.toByteArray())

                    key = (0..7).map {
                        bytes[it].toLong() and 0xff shl 8 * (7 - it)
                    }.reduce(Long::or) and Long.MAX_VALUE
                    factory = instance
                    factoryMap[key] = instance
                }
                else -> throw Exception("Unknown source class type! ${instance.javaClass}")
            }
        } catch (error: Throwable) {
            throw error
        }
    }

    override var key: Long = 0

    override var factory: DataSourceFactory = InternalDataSourceFactory()
        private set

    override val map: Map<Long, DataSourceFactory>
        get() = factoryMap
}
