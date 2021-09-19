package app.surgo.ui.settings.plugin

import androidx.compose.runtime.Immutable
import app.surgo.core.plugin.PluginDescriptor

@Immutable
internal data class PluginSettingsViewState(
    val selectedDataSource: String = "",
    val installedPlugins: List<PluginDescriptor> = emptyList()
)
