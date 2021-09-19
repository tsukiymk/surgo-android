package app.surgo.core.plugin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class PluginInstallReceiver(
    private val listener: Listener
) : BroadcastReceiver() {
    private val filter: IntentFilter
        get() = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

    fun register(context: Context) {
        context.registerReceiver(this, filter)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.encodedSchemeSpecificPart
                if (packageName != null) {
                    val plugin = PluginLoader.loadPlugin(context, packageName)
                    if (plugin != null) {
                        listener.onPluginInstalled(plugin)
                    }
                }
            }
        }
    }

    interface Listener {
        fun onPluginInstalled(plugin: PluginDescriptor)
    }
}
