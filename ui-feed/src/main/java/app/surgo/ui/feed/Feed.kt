package app.surgo.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.DynamicThemePrimaryColorsFromImage
import app.surgo.common.compose.components.InsetAwareTopAppBar
import app.surgo.common.compose.components.rememberDominantColorState
import app.surgo.common.compose.runtime.LocalContentPadding
import app.surgo.common.compose.theme.Keyline1
import app.surgo.common.compose.utils.contrastAgainst
import app.surgo.common.compose.utils.verticalGradientScrim
import app.surgo.data.entities.PlaylistEntity
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@Composable
fun FeedScreen(
    toSettings: () -> Unit,
    toPlaylistDetails: (Long) -> Unit = {}
) {
    val viewModel = hiltViewModel<FeedViewModel>()
    val viewState by viewModel.state.collectAsState()

    FeedContent(
        viewState = viewState,
    ) { action ->
        when (action) {
            is FeedAction.OpenSettings -> toSettings()
            is FeedAction.OpenPlaylistDetails -> toPlaylistDetails(action.playlistId)
        }
    }
}

@Composable
private fun FeedContent(
    viewState: FeedViewState,
    emit: (FeedAction) -> Unit
) {
    val surfaceColor = MaterialTheme.colors.surface
    val colorState = rememberDominantColorState { color ->
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }

    DynamicThemePrimaryColorsFromImage(colorState) {
        LazyColumn(contentPadding = LocalContentPadding.current) {
            item {
                val selectedImageUrl = viewState.recentlyPlaylists.getOrNull(0)?.imageUri
                if (selectedImageUrl != null) {
                    LaunchedEffect(selectedImageUrl) {
                        colorState.updateColorsFromImageUrl(selectedImageUrl)
                    }
                } else {
                    colorState.reset()
                }

                Column(
                    Modifier.verticalGradientScrim(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    )
                ) {
                    FeedTopAppBar(
                        backgroundColor = Color.Transparent,
                        emit = emit
                    )
                    PlaylistsColumn(
                        label = stringResource(R.string.text_recently_played),
                        collections = viewState.recentlyPlaylists,
                        size = 128.dp,
                        onItemClicked = { emit(FeedAction.OpenPlaylistDetails(it)) }
                    )
                }
            }
            item {
                PlaylistsColumn(
                    label = stringResource(R.string.text_recommended_playlists),
                    collections = viewState.recommendedPlaylists,
                    onItemClicked = { emit(FeedAction.OpenPlaylistDetails(it)) }
                )
            }
            item {
                PlaylistsColumn(
                    label = stringResource(R.string.text_for_you),
                    collections = viewState.recommendedPlaylists,
                    onItemClicked = { emit(FeedAction.OpenPlaylistDetails(it)) }
                )
            }
            item {
                PlaylistsColumn(
                    label = stringResource(R.string.text_charts),
                    collections = viewState.popularPlaylists,
                    onItemClicked = { emit(FeedAction.OpenPlaylistDetails(it)) }
                )
            }
        }
    }
}

@Composable
private fun FeedTopAppBar(
    backgroundColor: Color,
    emit: (FeedAction) -> Unit
) {
    InsetAwareTopAppBar(
        backgroundColor = backgroundColor,
        elevation = 0.dp
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { /* TODO: Open history */ }) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null
                )
            }
            IconButton(onClick = { emit(FeedAction.OpenSettings) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlaylistsColumn(
    label: String,
    collections: List<PlaylistEntity>,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    onItemClicked: (Long) -> Unit
) {
    AnimatedVisibility(
        visible = collections.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(modifier) {
            Row(Modifier.padding(start = 24.dp)) {
                Text(
                    text = label,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.Start),
                    style = MaterialTheme.typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(
                    start = Keyline1,
                    top = 8.dp,
                    end = Keyline1,
                    bottom = 24.dp
                )
            ) {
                items(collections) { playlist ->
                    PlaylistColumn(
                        title = playlist.name,
                        imageUrl = playlist.imageUri,
                        modifier = Modifier
                            .width(size)
                            .clickable(
                                onClick = { onItemClicked(playlist.id) }
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaylistColumn(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            if (imageUrl != null) {
                val painter = rememberImagePainter(imageUrl)

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private const val MinContrastOfPrimaryVsSurface = 3f
