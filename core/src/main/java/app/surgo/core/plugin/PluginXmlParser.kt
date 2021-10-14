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

class PluginXmlParser(
    resources: Resources,
    @XmlRes xmlResId: Int
) {
    var displayName: String? = null
        private set
    var vendor: String? = null
        private set

    private val _actions = ArrayList<ActionDescriptor>()
    val actions: List<ActionDescriptor>
        get() = _actions

    private val _extensions = ArrayList<ExtensionDescriptor>()
    val extensions: List<ExtensionDescriptor>
        get() = _extensions

    private var namespace: String = ""

    init {
        val parser = resources.getXml(xmlResId)
        try {
            parseXml(resources, parser)
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }
    }

    private fun parseXml(resources: Resources, parser: XmlResourceParser) {
        val attrs = Xml.asAttributeSet(parser)
        val depth = parser.depth
        var type: Int
        loop@ while (
            (parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth) &&
            type != XmlPullParser.END_DOCUMENT
        ) {
            if (type != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TAG_ROOT -> {
                    attrs.getAttributeValue(null, "defaultNs")?.also { namespace = it }
                }
                TAG_META_DATA -> {
                    // FIXME: It seems not working
                    /*
                    val a = resources.obtainAttributes(parser, R.styleable.SurgoPluginMetaData)
                    val name = a.getString(R.styleable.SurgoPluginMetaData_value)
                    ...
                    a.recycle()
                     */
                    when (attrs.getAttributeValue(ATTRIBUTE_NAMESPACE_APP, "name")) {
                        "displayName" -> displayName = attrs.obtainString(resources, "value")
                        "vendor" -> vendor = attrs.obtainString(resources, "value")
                    }
                }
                TAG_ACTIONS -> parseActions(parser)
                TAG_EXTENSIONS -> parseExtensions(parser)
            }
        }
    }

    private fun parseActions(parser: XmlResourceParser) {
        val attrs = Xml.asAttributeSet(parser)
        val depth = parser.depth
        var type: Int
        while (
            (parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth) &&
            type != XmlPullParser.END_DOCUMENT
        ) {
            if (type != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TAG_ACTION -> {
                }
            }
        }
    }

    private fun parseExtensions(parser: XmlResourceParser) {
        val attrs = Xml.asAttributeSet(parser)
        val depth = parser.depth
        var type: Int
        while (
            (parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth) &&
            type != XmlPullParser.END_DOCUMENT
        ) {
            if (type != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TAG_DATASOURCE -> {
                    val factoryClass = attrs.getAttributeValue(ATTRIBUTE_NAMESPACE_APP, "factoryClass")
                    if (factoryClass.isNullOrEmpty()) continue
                    _extensions.add(
                        ExtensionDescriptor(parser.name, "$namespace$factoryClass")
                    )
                }
            }
        }
    }

    private fun AttributeSet.obtainString(resources: Resources, attribute: String): String? {
        val resId = getAttributeResourceValue(ATTRIBUTE_NAMESPACE_APP, attribute, -1)

        return when {
            resId != -1 -> resources.getString(resId)
            else -> getAttributeValue(ATTRIBUTE_NAMESPACE_APP, attribute)
        }
    }

    companion object {
        private const val ATTRIBUTE_NAMESPACE_APP = "http://schemas.android.com/apk/res-auto"

        private const val TAG_ROOT = "surgo-plugin"
        private const val TAG_META_DATA = "meta-data"
        private const val TAG_EXTENSIONS = "extensions"
        private const val TAG_ACTIONS = "actions"

        private const val TAG_DATASOURCE = "datasource"

        private const val TAG_ACTION = "action"
    }
}
