package app.surgo.ui.settings.root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.Preference
import app.surgo.common.compose.components.PreferenceGroup
import app.surgo.ui.settings.R
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun RootSettingsScreen(
    toPluginSettings: () -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel = hiltViewModel<RootSettingsViewModel>()
    val viewState by viewModel.state.collectAsState()

    RootSettingsContent(
        viewState = viewState,
        navigateUp = navigateUp
    ) { action ->
        when (action) {
            is RootSettingsAction.OpenPluginSettings -> toPluginSettings()
            else -> viewModel.submitAction(action)
        }
    }
}

@Composable
private fun RootSettingsContent(
    viewState: RootSettingsViewState,
    navigateUp: () -> Unit,
    emit: (RootSettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row { Text(text = stringResource(R.string.title_settings)) }
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
            )
        }
    ) {
        Column {
            PreferenceGroup(title = "通常") {
                Preference(
                    title = "扩展管理",
                    onClick = { emit(RootSettingsAction.OpenPluginSettings) },
                    subtitle = viewState.selectedDataSource
                )
            }
        }
    }
}
