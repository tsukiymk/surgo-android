package app.surgo.ui.settings.root

internal sealed class RootSettingsAction {
    object OpenPluginSettings : RootSettingsAction()
    data class ChangeDataSource(val dataSource: String) : RootSettingsAction()
}
