package app.surgo.core.plugin

import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.Xml
import androidx.annotation.XmlRes
import app.surgo.core.plugin.extensions.ExtensionDescriptor
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class PluginXmlReader(
    resources: Resources,
    @XmlRes xmlResId: Int
) {
    var displayName: String? = null
        private set

    val extensions = ArrayList<ExtensionDescriptor>()

    init {
        val parser = resources.getXml(xmlResId)
        try {
            readXml(resources, parser)
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }
    }

    private fun readXml(
        resources: Resources,
        parser: XmlResourceParser
    ) {
        val attrs = Xml.asAttributeSet(parser)
        val depth = parser.depth
        var type: Int
        loop@ while (
            (parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth) &&
            type != XmlPullParser.END_DOCUMENT
        ) {
            if (type != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TAG_META_DATA -> {
                    when (attrs.getAttributeValue(null, "name")) {
                        "displayName" -> displayName = attrs.loadString(resources, "value")
                    }
                }
                TAG_EXTENSIONS -> {
                    readExtensions(resources, parser)
                }
            }
        }
    }

    private fun readExtensions(
        resources: Resources,
        parser: XmlResourceParser
    ) {
        val attrs = Xml.asAttributeSet(parser)
        val depth = parser.depth
        var type: Int
        loop@ while (
            (parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth) &&
            type != XmlPullParser.END_DOCUMENT
        ) {
            if (type != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TAG_DATASOURCE -> {
                    val factoryClass = attrs.loadString(resources, "factoryClass") ?: throw Exception("Can't find factoryClass")
                    extensions.add(
                        ExtensionDescriptor(
                            name = parser.name,
                            beanClass = factoryClass
                        )
                    )
                }
            }
        }
    }

    private fun AttributeSet.loadString(
        resources: Resources,
        attribute: String
    ): String? {
        val resId = getAttributeResourceValue(null, attribute, -1)

        return when {
            resId != -1 -> resources.getString(resId)
            else -> getAttributeValue(null, attribute)
        }
    }

    companion object {
        private const val TAG_ROOT = "surgo-plugin"
        private const val TAG_META_DATA = "meta-data"
        private const val TAG_EXTENSIONS = "extensions"

        private const val TAG_DATASOURCE = "datasource"
    }
}
