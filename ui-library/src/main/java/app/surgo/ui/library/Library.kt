package app.surgo.ui.library

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun LibraryScreen() {
    val viewModel = hiltViewModel<LibraryViewModel>()
    val viewState by viewModel.state.collectAsState()

    LibraryContent(
        viewState = viewState
    )
}

@Composable
private fun LibraryContent(
    viewState: LibraryViewState
) {
    Scaffold(
        topBar = {
            LibraryTopBar()
        }
    ) {
    }
}

@Composable
private fun LibraryTopBar() {
    TopAppBar(
        title = {
            Row { Text(stringResource(R.string.text_your_library)) }
        },
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyStart = false,
            applyEnd = false,
            applyBottom = false
        ),
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}
