package app.surgo.ui.playback

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.utils.lerp
import app.surgo.core.media2.displayIconUri
import app.surgo.core.media2.displaySubtitle
import app.surgo.core.media2.isPlaying
import app.surgo.core.media2.title
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlaybackScreen(
    openFraction: Float,
    doCollapse: () -> Unit,
    toArtistDetails: (Long) -> Unit = {}
) {
    val viewModel = hiltViewModel<PlaybackViewModel>()
    val viewState by viewModel.state.collectAsState()

    PlaybackScreen(
        viewState = viewState,
        openFraction = openFraction
    ) { action ->
        when (action) {
            is PlaybackAction.Close -> doCollapse()
            is PlaybackAction.OpenArtistDetails -> toArtistDetails(action.artistId)
            else -> viewModel.submitAction(action)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class, ExperimentalCoilApi::class)
@Composable
private fun PlaybackScreen(
    viewState: PlaybackViewState,
    openFraction: Float,
    emit: (PlaybackAction) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        PlaybackBackground(
            viewState = viewState,
            modifier = Modifier
                .graphicsLayer {
                    alpha = lerp(0f, 1f, 0.3f, 1f, openFraction)
                }
        )

        /**
         * FIXME: [ContentScale] is still working
        CollapsingImageLayout(
            modifier = Modifier.padding(horizontal = 12.dp),
            collapseFraction = openFraction,
            reservedTopHeight = topContentHeight,
            reservedBottomHeight = bottomContentHeight,
        ) {
            Box(Modifier.clip(MaterialTheme.shapes.medium)) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    painter = rememberImagePainter(viewState.nowPlaying.displayIconUri),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            }
        }
         */

        PlaybackContent(
            viewState = viewState,
            modifier = Modifier
                .graphicsLayer {
                   alpha = lerp(0f, 1f, 0.8f, 1f, openFraction)
                },
            emit = emit
        )
        PlaybackMiniControls(
            viewState = viewState,
            modifier = Modifier
                .graphicsLayer {
                    alpha = lerp(1f, 0f, 0f, 0.3f, openFraction)
                },
            emit = emit
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun BoxWithConstraintsScope.PlaybackBackground(
    viewState: PlaybackViewState,
    modifier: Modifier = Modifier
) {
    val (boxWidth, boxHeight) = with(LocalDensity.current) { Pair(maxWidth.toPx(), maxHeight.toPx()) }
    val scale = max(boxWidth, boxHeight) / min(boxWidth, boxHeight) / sqrt(2f) * 2
    val rotate by animateFloatAsState(
        targetValue = viewState.progressState.elapsed.toFloat() / 500,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    Box(modifier) {
        Image(
            painter = rememberImagePainter(
                data = viewState.nowPlaying.displayIconUri,
                builder = {
                    transformations(
                        CircleCropTransformation(),
                        BlurTransformation(
                            context = LocalContext.current,
                            radius = 20f,
                            sampling = 10f
                        )
                    )
                },
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotate
                }
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaybackContent(
    viewState: PlaybackViewState,
    modifier: Modifier = Modifier,
    emit: (PlaybackAction) -> Unit
) {
    Column(modifier) {
        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyStart = false,
                applyEnd = false,
                applyBottom = false
            )
        ) {
            IconButton(onClick = { emit(PlaybackAction.Close) }) {
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        }
        Box(
            Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                painter = rememberImagePainter(viewState.nowPlaying.displayIconUri),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewState.nowPlaying.title ?: "",
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Text(
                text = viewState.nowPlaying.displaySubtitle ?: "",
                fontSize = 15.sp
            )
        }
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            progress = 0f,
            color = Color.White
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewState.progressState.currentDuration,
                fontSize = 12.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = viewState.progressState.totalDuration,
                fontSize = 12.sp
            )
        }
        PlaybackController(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            isPlaying = viewState.playbackState.isPlaying,
            emit = emit
        )
    }
}

@Composable
private fun PlaybackProgressSlider(
    viewState: PlaybackViewState
) {
    Box {
        Slider(
            value = 0f,
            onValueChange = {},
            onValueChangeFinished = {},
        )
    }
}

@Composable
private fun PlaybackController(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    emit: (PlaybackAction) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { emit(PlaybackAction.SkipToPrevious) },
            modifier = Modifier.size(96.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.FastRewind,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        }
        IconButton(
            onClick = { emit(PlaybackAction.PlayOrPause("")) },
            modifier = Modifier.size(96.dp)
        ) {
            Icon(
                painter = rememberVectorPainter(
                    if (isPlaying) {
                        Icons.Rounded.PauseCircleFilled
                    } else {
                        Icons.Rounded.PlayCircleFilled
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        }
        IconButton(
            onClick = { emit(PlaybackAction.SkipToNext) },
            modifier = Modifier.size(96.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.FastForward,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaybackMiniControls(
    viewState: PlaybackViewState,
    modifier: Modifier = Modifier,
    emit: (PlaybackAction) -> Unit
) {
    BottomAppBar(
        modifier = modifier,
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        ConstraintLayout {
            val (artwork, text, controls) = createRefs()

            Box(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .constrainAs(artwork) {
                        start.linkTo(parent.start, 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .aspectRatio(1f),
                    painter = rememberImagePainter(viewState.nowPlaying.displayIconUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                Modifier.constrainAs(controls) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            ) {
                IconButton(onClick = { emit(PlaybackAction.PlayOrPause("")) }) {
                    Icon(
                        painter = rememberVectorPainter(
                            if (viewState.playbackState.isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            }
                        ),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { emit(PlaybackAction.SkipToNext) }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null
                    )
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(text) {
                        linkTo(
                            start = artwork.end,
                            end = controls.start,
                            startMargin = 12.dp,
                            endMargin = 12.dp
                        )
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.preferredWrapContent
                    },
                text = viewState.nowPlaying.title ?: "",
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * FIXME: Consumes too much performance
@Composable
private fun CollapsingImageLayout(
    collapseFraction: Float,
    modifier: Modifier = Modifier,
    reservedTopHeight: Int = 0,
    reservedBottomHeight: Int = 0,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val availableHeight = constraints.maxHeight - reservedTopHeight - reservedBottomHeight

        var imageMaxSize = min(availableHeight, constraints.maxWidth) - MaxImageOffset.roundToPx()
        imageMaxSize = max((imageMaxSize * 0.9f).toInt(), 0)

        val imageMinSize = max(CollapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth = lerp(imageMinSize, imageMaxSize, collapseFraction)
        val imagePlaceable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(MinImageOffset.roundToPx(), reservedTopHeight + (availableHeight - imageMaxSize) / 2, collapseFraction)
        val imageX = lerp(0, (constraints.maxWidth - imageWidth) / 2, collapseFraction)

        layout(
            width = constraints.maxWidth,
            height = imageY
        ) {
            imagePlaceable.place(imageX, imageY)
        }
    }
}

private val CollapsedImageSize = 40.dp

private val MinImageOffset = 8.dp
private val MaxImageOffset = 18.dp
 */
