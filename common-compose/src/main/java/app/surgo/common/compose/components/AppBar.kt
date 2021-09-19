package app.surgo.common.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun InsetAwareTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        elevation = elevation
    ) {
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            backgroundColor = Color.Transparent,
            contentColor,
            elevation = 0.dp,
            contentPadding,
            content = content
        )
    }
}

@Composable
fun InsetAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        elevation = elevation
    ) {
        TopAppBar(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false),
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp
        )
    }
}

@Composable
fun InsetAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        elevation = elevation
    ) {
        Column {
            TopAppBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false),
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                backgroundColor = Color.Transparent,
                contentColor = contentColor,
                elevation = 0.dp
            )
            content()
        }
    }
}
