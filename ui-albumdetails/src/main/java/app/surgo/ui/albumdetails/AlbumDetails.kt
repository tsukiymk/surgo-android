package app.surgo.ui.albumdetails

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.InsetAwareTopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun AlbumDetailsScreen(
    navigateUp: () -> Unit
) {
    val viewModel = hiltViewModel<AlbumDetailsViewModel>()
    val viewState by viewModel.state.collectAsState()

    AlbumDetailsScreen(
        viewState = viewState,
        navigateUp = navigateUp,
    ) { action -> viewModel.submitAction(action) }
}

@Composable
private fun AlbumDetailsScreen(
    viewState: AlbumDetailsViewState,
    navigateUp: () -> Unit,
    emit: (AlbumDetailsAction) -> Unit
) {
    Scaffold(
        topBar = {
            AlbumDetailsTopAppBar(
                viewState = viewState,
                navigateUp = navigateUp
            )
        }
    ) {
        val swipeRefreshState = rememberSwipeRefreshState(viewState.isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { emit(AlbumDetailsAction.Refresh) }
        ) {
        }
    }
}

@Composable
private fun AlbumDetailsTopAppBar(
    viewState: AlbumDetailsViewState,
    navigateUp: () -> Unit
) {
    InsetAwareTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}
