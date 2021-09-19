package app.surgo.common.compose.utils

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.google.accompanist.insets.statusBarsHeight

fun Modifier.statusBarsScrim(
    enabled: Boolean = true,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 0.4f
): Modifier = composed {
    if (enabled) {
        Modifier
            .fillMaxWidth()
            .statusBarsHeight()
            .background(
                color = MaterialTheme.colors.surface.copy(alpha = alpha)
            )
    } else this
}
