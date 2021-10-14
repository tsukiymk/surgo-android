package app.surgo.ui.playlistdetails

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.runtime.LocalContentPadding
import app.surgo.data.entities.ArtistEntity
import app.surgo.data.entities.SongEntity
import app.surgo.data.resultentities.SongWithArtists
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun PlaylistDetailsScreen(
    navigateUp: () -> Unit
) {
    val viewModel = hiltViewModel<PlaylistDetailsViewModel>()
    val viewState by viewModel.state.collectAsState()

    PlaylistDetailsScreen(
        viewState = viewState,
        navigateUp = navigateUp
    ) { action -> viewModel.submitAction(action) }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaylistDetailsScreen(
    viewState: PlaylistDetailsViewState,
    navigateUp: () -> Unit,
    emit: (PlaylistDetailsAction) -> Unit
) {
    Scaffold(
        topBar = {
            PlaylistDetailsTopAppBar(
                viewState = viewState,
                navigateUp = navigateUp
            )
        }
    ) {
        val swipeRefreshState = rememberSwipeRefreshState(viewState.isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { emit(PlaylistDetailsAction.Refresh) }
        ) {
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                contentPadding = LocalContentPadding.current
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(240.dp)
                                .align(Alignment.Center)
                                .clip(MaterialTheme.shapes.medium),
                            painter = rememberImagePainter(viewState.playlist.imageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                lazyItemOfSongs(
                    songWithArtists = viewState.songWithArtists,
                    onItemClicked = { emit(PlaylistDetailsAction.Play(it)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlaylistDetailsTopAppBar(
    viewState: PlaylistDetailsViewState,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = viewState.playlist.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyBottom = false
        ),
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

@OptIn(ExperimentalCoilApi::class)
private fun LazyListScope.lazyItemOfSongs(
    songWithArtists: List<SongWithArtists>,
    onItemClicked: (Long) -> Unit
) {
    item {
        Surface {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text(
                    text = stringResource(R.string.text_songs),
                    modifier = Modifier.weight(1f)
                        .wrapContentWidth(Alignment.Start),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    items(songWithArtists) { songWithArtist ->
        SongColumn(
            song = songWithArtist.song,
            artists = songWithArtist.artists,
            onItemClicked = onItemClicked
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun SongColumn(
    song: SongEntity,
    artists: List<ArtistEntity>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onItemClicked(song.id) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(55.dp)
                    .aspectRatio(1f)
            ) {
                val painter = rememberImagePainter(song.imageUri)

                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            ) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.h6.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = artists.joinToString(" & ") { it.name },
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                modifier = Modifier.padding(4.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
