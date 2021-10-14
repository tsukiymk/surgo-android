package app.surgo.ui.plugindetails

import androidx.compose.runtime.Immutable
import app.surgo.core.plugin.PluginDescriptor

@Immutable
internal class PluginDetailsViewState(
    val plugin: PluginDescriptor? = null,
    val isDefaultDataSource: Boolean = false
)
