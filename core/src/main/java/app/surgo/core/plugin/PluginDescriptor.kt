package app.surgo.core.plugin

import app.surgo.core.plugin.extensions.ExtensionDescriptor

data class PluginDescriptor(
    var name: String? = null,
    var version: String? = null,

    var packageName: String
) {
    var extensions: MutableList<ExtensionDescriptor>? = null
}
