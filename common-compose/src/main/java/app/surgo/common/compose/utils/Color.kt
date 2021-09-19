package app.surgo.common.compose.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

fun Color.contrastAgainst(backgroundColor: Color): Float {
    val foregroundColor = if (alpha < 1f) compositeOver(backgroundColor) else this

    val fgLuminance = foregroundColor.luminance() + 0.05f
    val bgLuminance = backgroundColor.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}
