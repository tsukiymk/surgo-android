package app.surgo.core.plugin

import app.surgo.core.plugin.extensions.ExtensionDescriptor
import java.security.MessageDigest

data class PluginDescriptor(
    var packageName: String,

    var name: String? = null,
    var version: String? = null,
    var vendor: String? = null
) {
    val id by lazy {
        val bytes = MessageDigest.getInstance("MD5")
            .digest(packageName.toByteArray())

        (0..7).map {
            bytes[it].toLong() and 0xff shl 8 * (7 - it)
        }.reduce(Long::or) and Long.MAX_VALUE
    }

    var actions: List<ActionDescriptor>? = null
    var extensions: List<ExtensionDescriptor>? = null
}
