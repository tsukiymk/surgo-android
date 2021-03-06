package app.surgo.ui.settings.plugin

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.ExtendedTopAppBar
import app.surgo.common.compose.components.Preference
import app.surgo.ui.settings.R
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

enum class PluginSettingsTab(
    @StringRes val title: Int
) {
    INSTALLED(R.string.label_installed),
    MARKETPLACE(R.string.label_marketplace)
}

@Composable
fun PluginSettingsScreen(
    toPluginDetails: (Long) -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel = hiltViewModel<PluginSettingsViewModel>()
    val viewState by viewModel.state.collectAsState()

    val context = LocalContext.current

    PluginSettingsContent(
        viewState = viewState,
        navigateUp = navigateUp
    ) { action ->
        when (action) {
            is PluginSettingsAction.OpenPluginDetails -> toPluginDetails(action.pluginId)
            is PluginSettingsAction.RequestUninstall -> {
                val uri = Uri.fromParts("package", action.packageName, null)
                val intent = Intent(Intent.ACTION_DELETE, uri)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            else -> viewModel.submitAction(action)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun PluginSettingsContent(
    viewState: PluginSettingsViewState,
    navigateUp: () -> Unit,
    emit: (PluginSettingsAction) -> Unit
) {
    val scope = rememberCoroutineScope()

    val tabs = remember { PluginSettingsTab.values() }
    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            ExtendedTopAppBar(
                title = {
                    Text(stringResource(R.string.title_settings))
                },
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyStart = false,
                    applyEnd = false,
                    applyBottom = false
                ),
                navigationIcon = {
                    IconButton(
                        onClick = navigateUp
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface
            ) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    backgroundColor = MaterialTheme.colors.surface,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                            color = MaterialTheme.colors.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(tab.ordinal) }
                            },
                            text = {
                                Text(
                                    text = stringResource(tab.title),
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {
        HorizontalPager(
            count = tabs.size,
            state = pagerState
        ) { page ->
            when (tabs[page]) {
                PluginSettingsTab.INSTALLED -> {
                    InstalledContent(
                        viewState = viewState,
                        modifier = Modifier.fillMaxSize(),
                        emit = emit
                    )
                }
                PluginSettingsTab.MARKETPLACE -> {
                    MarketplaceContent(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun InstalledContent(
    viewState: PluginSettingsViewState,
    modifier: Modifier = Modifier,
    emit: (PluginSettingsAction) -> Unit
) {
    Surface(modifier) {
        LazyColumn {
            items(viewState.installedPlugins) { plugin ->
                Preference(
                    title = plugin.name ?: "",
                    onClick = { emit(PluginSettingsAction.OpenPluginDetails(plugin.id)) },
                    trailing = {
                        var expanded by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = { expanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem({ /*TODO*/ }) {
                                Text("????????????")
                            }
                            DropdownMenuItem({
                                emit(PluginSettingsAction.RequestUninstall(plugin.packageName))
                            }) {
                                Text("??????")
                            }
                        }
                    }
                )
                /*
                RadioPreference(
                    selected = viewState.selectedDataSource == extension.packageName,
                    title = extension.displayName,
                    onValueChange = {
                        emit(PluginSettingsAction.ChangeDataSource(extension.packageName))
                    }
                )
                 */
            }
        }
    }
}

@Composable
private fun MarketplaceContent(
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(false)

    val plugins = listOf(
        Sample("com.tsukiymk.surgo.openapi.datasource.netease", "Netease Cloud Music"),
        Sample("com.tsukiymk.surgo.openapi.datasource.spotify", "Spotify"),
        Sample("com.tsukiymk.surgo.openapi.datasource.youtube", "YouTube Music"),
        Sample("com.tsukiymk.surgo.openapi.datasource.apple", "Apple Music"),
        Sample("com.tsukiymk.surgo.openapi.datasource.soundcloud", "SoundCloud")
    )

    Surface(modifier) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { /* TODO */ }
        ) {
            LazyColumn {
                items(plugins) { plugin ->
                    Preference(
                        title = plugin.displayName,
                        onClick = {},
                        trailing = {
                            TextButton(onClick = { /* TODO */ }) {
                                Text("Install")
                            }
                        }
                    )
                }
            }
        }
    }
}

data class Sample(
    val packageName: String,
    val displayName: String = ""
)
