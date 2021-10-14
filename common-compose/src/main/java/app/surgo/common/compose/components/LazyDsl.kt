package app.surgo.common.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

fun LazyListScope.label(
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    item {
        Row(Modifier.padding(contentPadding)) {
            ProvideTextStyle(
                value = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                ),
                content = content
            )
        }
    }
}

fun LazyListScope.subHeader(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    item {

    }
}
