package app.surgo.common.compose.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding

@ExperimentalMaterialApi
@Composable
fun InsetAwareNavigationRail(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = NavigationRailDefaults.Elevation,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        elevation = elevation
    ) {
        NavigationRail(
            modifier = Modifier.systemBarsPadding(end = false),
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            header = header,
            content = content
        )
    }
}
