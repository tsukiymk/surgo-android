package app.surgo.ui.settings.plugin

internal sealed class PluginSettingsAction {
    data class ChangeDataSource(val dataSource: String) : PluginSettingsAction()
}
