package app.surgo.common.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ChipDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp
    )
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    contentColor: Color = contentColorFor(color),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        border = BorderStroke(
            width = 1.dp,
            color = contentColor
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(ChipDefaults.ContentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
