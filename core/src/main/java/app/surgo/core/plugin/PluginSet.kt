package app.surgo.core.plugin

data class PluginSet(
    val allPlugins: List<PluginDescriptor>,
    val enabledPlugins: List<PluginDescriptor>
)
