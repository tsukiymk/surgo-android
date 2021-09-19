package app.surgo.core.plugin

import android.content.Context
import app.surgo.core.plugin.messages.MessageBus
import kotlinx.coroutines.flow.MutableStateFlow

class PluginManager(context: Context) {
    val observableInstalled = MutableStateFlow(emptyList<PluginDescriptor>())

    val messageBus = MessageBus()

    private val receiver = PluginInstallReceiver(object : PluginInstallReceiver.Listener {
        override fun onPluginInstalled(plugin: PluginDescriptor) {
            observableInstalled.value = observableInstalled.value
                .plus(plugin)
                .toList()
        }
    })

    init {
        val results = PluginLoader.loadPlugins(context)

        observableInstalled.value = results

        receiver.register(context)
    }

    companion object {
        @Volatile
        private var instance: PluginManager? = null

        @Volatile
        private var pluginSet: PluginSet? = null

        fun getInstance(context: Context): PluginManager {
            return instance ?: PluginManager(context).also { instance = it }
        }
    }
}
