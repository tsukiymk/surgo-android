package app.surgo.ui.plugindetails

internal sealed class PluginDetailsAction {
    data class ChangeDataSource(val dataSource: String) : PluginDetailsAction()
}
