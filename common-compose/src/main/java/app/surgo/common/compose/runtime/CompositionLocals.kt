package app.surgo.common.compose.runtime

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalContentPadding = staticCompositionLocalOf {
    PaddingValues(0.dp)
}
