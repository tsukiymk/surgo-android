package app.surgo.ui.plugindetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.Preference
import app.surgo.common.compose.utils.iconResource
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun PluginDetailsScreen(
    navigateUp: () -> Unit
) {
    val viewModel = hiltViewModel<PluginDetailsViewModel>()
    val viewState by viewModel.state.collectAsState()

    PluginDetailsScreen(
        viewState = viewState,
        navigateUp = navigateUp
    ) { viewModel.submitAction(it) }
}

@Composable
private fun PluginDetailsScreen(
    viewState: PluginDetailsViewState,
    navigateUp: () -> Unit,
    emit: (PluginDetailsAction) -> Unit
) {
    val plugin = viewState.plugin ?: return

    Scaffold(
        topBar = {
            PluginDetailsTopAppBar(navigateUp)
        }
    ) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            PluginDetailsHeader(
                viewState = viewState
            )
            Divider()
            Preference(
                title = "修改设置项",
                onClick = {},
                leading = {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null
                    )
                },
                trailing = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null
                    )
                }
            )
            if (plugin.extensions?.find { it.name == "datasource" } != null) {
                Preference(
                    title = "设为默认数据源",
                    onClick = {},
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Storage,
                            contentDescription = null
                        )
                    },
                    trailing = {
                        TextButton(
                            onClick = {
                                emit(PluginDetailsAction.ChangeDataSource(plugin.packageName))
                            },
                            enabled = !viewState.isDefaultDataSource
                        ) {
                            Text(if (viewState.isDefaultDataSource) "已设置" else "设置")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PluginDetailsTopAppBar(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("插件信息") },
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyStart = false,
            applyEnd = false,
            applyBottom = false
        ),
        navigationIcon = {
            IconButton(navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface
    )
}

@Composable
private fun PluginDetailsHeader(
    viewState: PluginDetailsViewState,
    modifier: Modifier = Modifier
) {
    val plugin = viewState.plugin ?: return

    Row(modifier) {
        Box(Modifier.padding(24.dp)) {
            Image(
                bitmap = iconResource(plugin.packageName),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }
        Column(Modifier.padding(vertical = 24.dp)) {
            Text(
                text = "${plugin.name}",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "${plugin.vendor}",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}
