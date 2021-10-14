package app.surgo.ui.settings.plugin

internal sealed class PluginSettingsAction {
    data class OpenPluginDetails(val pluginId: Long) : PluginSettingsAction()
    data class RequestUninstall(val packageName: String) : PluginSettingsAction()
    data class ChangeDataSource(val dataSource: String) : PluginSettingsAction()
}
