package app.surgo.core.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

object PluginLoader {
    private const val PLUGIN_KEY = "com.tsukiymk.surgo.plugin"

    fun loadPlugin(context: Context, packageName: String): PluginDescriptor? {
        val packageManager = context.packageManager
        val applicationInfo = try {
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (cause: PackageManager.NameNotFoundException) {
            throw cause
        }
        val isPlugin = applicationInfo.metaData?.keySet().orEmpty().contains(PLUGIN_KEY)
        if (!isPlugin) {
            return null
        }

        return runBlocking {
            loadPluginInfo(context, packageName)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun loadPlugins(context: Context): List<PluginDescriptor> {
        val packageManager = context.packageManager
        val filteredPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .filter { packageInfo ->
                packageInfo.applicationInfo.metaData?.keySet()
                    .orEmpty()
                    .any { it.equals(PLUGIN_KEY) }
            }

        if (filteredPackages.isEmpty()) return emptyList()

        return runBlocking {
            val deferred = filteredPackages.map {
                async {
                    loadPluginInfo(context, it.packageName)
                }
            }
            deferred.map { it.await() }
        }
    }

    private fun loadPluginInfo(context: Context, packageName: String): PluginDescriptor {
        val packageManager = context.packageManager
        val resources = packageManager.getResourcesForApplication(packageName)
        val applicationInfo = try {
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (cause: PackageManager.NameNotFoundException) {
            throw cause
        }
        val packageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (cause: PackageManager.NameNotFoundException) {
            throw cause
        }
        val parser = PluginXmlParser(resources, applicationInfo.metaData.getInt(PLUGIN_KEY))

        return PluginDescriptor(
            packageName = applicationInfo.packageName,
            version = packageInfo.versionName,
            name = parser.displayName,
            vendor = parser.vendor
        ).apply {
            actions = parser.actions
            extensions = parser.extensions
        }
    }
}
