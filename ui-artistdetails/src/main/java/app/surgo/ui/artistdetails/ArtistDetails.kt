package app.surgo.ui.artistdetails

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.StaggeredGrid
import app.surgo.common.compose.components.label
import app.surgo.common.compose.runtime.LocalContentPadding
import app.surgo.common.compose.utils.iconButtonBackgroundScrim
import app.surgo.data.entities.AlbumEntity
import app.surgo.data.entities.SongEntity
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun ArtistDetailsScreen(
    navigateUp: () -> Unit
) {
    val viewModel = hiltViewModel<ArtistDetailsViewModel>()
    val viewState by viewModel.state.collectAsState()

    ArtistDetailsScreen(
        viewState = viewState,
        navigateUp = navigateUp,
    ) { action -> viewModel.submitAction(action) }
}

@Composable
private fun ArtistDetailsScreen(
    viewState: ArtistDetailsViewState,
    navigateUp: () -> Unit,
    emit: (ArtistDetailsAction) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            var topBarHeight by remember { mutableStateOf(0) }
            val collapsed by remember {
                derivedStateOf {
                    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                    when {
                        visibleItemsInfo.isEmpty() -> false
                        topBarHeight <= 0 -> false
                        else -> {
                            val firstVisibleItem = visibleItemsInfo[0]
                            when {
                                firstVisibleItem.index > 0 -> true
                                else -> firstVisibleItem.size + firstVisibleItem.offset <= topBarHeight
                            }
                        }
                    }
                }
            }

            ArtistDetailsTopAppBar(
                viewState = viewState,
                collapsed = collapsed,
                navigateUp = navigateUp,
                modifier = Modifier.onSizeChanged {
                    topBarHeight = it.height
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = LocalContentPadding.current
        ) {
            item {
                BackdropImage(
                    viewState = viewState,
                    offsetY = when (listState.firstVisibleItemIndex) {
                        0 -> listState.firstVisibleItemScrollOffset / 3
                        else -> 0
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            label {
                Text(stringResource(R.string.text_popular))
            }
            item {
                SongsColumn(
                    songs = viewState.songs,
                    onItemClicked = {}
                )
            }
            label {
                Text(stringResource(R.string.text_albums))
            }
            item {
                AlbumsColumn(viewState.albums)
            }
            label {
                Text(stringResource(R.string.text_music_videos))
            }
            item {
                MusicVideosColumn(viewState.albums)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ArtistDetailsTopAppBar(
    viewState: ArtistDetailsViewState,
    collapsed: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        when {
            collapsed -> MaterialTheme.colors.surface
            else -> Color.Transparent
        }
    )
    val elevation by animateDpAsState(
        when {
            collapsed -> AppBarDefaults.TopAppBarElevation
            else -> 0.dp
        }
    )

    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = collapsed,
                enter = fadeIn() + slideInHorizontally(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Text(
                    text = viewState.artist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        modifier = modifier,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyBottom = false
        ),
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
                modifier = Modifier.iconButtonBackgroundScrim(enabled = !collapsed)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        backgroundColor = backgroundColor,
        elevation = elevation
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun BackdropImage(
    viewState: ArtistDetailsViewState,
    offsetY: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Box(
            Modifier
                .clipToBounds()
                .offset {
                    IntOffset(
                        x = 0,
                        y = offsetY
                    )
                }
        ) {
            val painter = rememberImagePainter(viewState.artist.imageUri)

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = viewState.artist.name,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.h4.copy(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 1f),
                    blurRadius = 0.4f
                ),
                fontWeight = FontWeight.Black
            )
        )
    }
}

@Composable
private fun SongsColumn(
    songs: List<SongEntity>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    StaggeredGrid(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        songs.forEach { song ->
            SongGrid(
                song = song,
                onItemClicked = onItemClicked,
                modifier = Modifier.width(300.dp)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun SongGrid(
    song: SongEntity,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Row(
            modifier = Modifier
                .clickable { onItemClicked(song.id) }
                .padding(8.dp),
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
                    text = song.album ?: "",
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

@Composable
private fun AlbumsColumn(
    albums: List<AlbumEntity>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        val lastIndex = albums.size - 1

        itemsIndexed(albums) { index, album ->
            AlbumRow(
                album = album,
                modifier = Modifier
                    .width(128.dp)
                    .clickable { /* */ }
            )
            if (index < lastIndex) Spacer(Modifier.width(16.dp))
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun AlbumRow(
    album: AlbumEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            if (album.imageUri != null) {
                Image(
                    painter = rememberImagePainter(album.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = album.name,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun MusicVideosColumn(
    albums: List<AlbumEntity>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        val lastIndex = albums.size - 1

        itemsIndexed(albums) { index, album ->
            MusicVideosRow(
                album = album,
                modifier = Modifier
                    .width(360.dp)
                    .clickable { /* */ }
            )
            if (index < lastIndex) Spacer(Modifier.width(16.dp))
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun MusicVideosRow(
    album: AlbumEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10)
        ) {
            if (album.imageUri != null) {
                Image(
                    painter = rememberImagePainter(album.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = album.name,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2
        )
    }
}
