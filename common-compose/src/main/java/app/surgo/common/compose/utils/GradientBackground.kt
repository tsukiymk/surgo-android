package app.surgo.common.compose.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.horizontalGradientBackground(
    colors: List<Color>
) = composed {
    var width by remember { mutableStateOf(0f) }
    val brush = remember(colors, width) {
        Brush.horizontalGradient(
            colors = colors,
            startX = 0f,
            endX = width
        )
    }

    drawBehind {
        width = size.width
        drawRect(brush = brush)
    }
}
