package app.surgo.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.label
import app.surgo.common.compose.components.rememberDominantColorState
import app.surgo.common.compose.runtime.LocalContentPadding
import app.surgo.common.compose.theme.AppTheme
import app.surgo.common.compose.theme.Keyline1
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.Kind
import kotlinx.coroutines.launch

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
    LazyColumn(contentPadding = LocalContentPadding.current) {
        item {
            FeedTopAppBar(
                backgroundColor = Color.Transparent,
                emit = emit
            )
        }
        lazyItemOfCatalog(viewState.recommendations)
    }
}

@Composable
private fun FeedTopAppBar(
    backgroundColor: Color,
    emit: (FeedAction) -> Unit
) {
    TopAppBar(
        backgroundColor = backgroundColor,
        elevation = 0.dp,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyStart = false,
            applyEnd = false,
            applyBottom = false
        )
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

private fun LazyListScope.lazyItemOfCatalog(
    recommendations: Resource
) {
    recommendations.data.orEmpty()
        .forEach { resource ->
            when (resource.attributes?.display?.kind) {
                Kind.HERO_SHELF -> lazyItemOfHeroShelf(resource)
                Kind.COVER_SHELF -> lazyItemOfCoverShelf(resource)
            }
        }
}

private fun LazyListScope.lazyItemOfHeroShelf(
    resource: Resource
) {
    val label = resource.attributes?.title?.stringForDisplay ?: ""
    val contents = resource.relationships?.contents?.data

    label {
        Text(label)
    }
    item {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                start = Keyline1,
                top = 8.dp,
                end = Keyline1,
                bottom = 24.dp
            )
        ) {
            items(contents.orEmpty()) { content ->
                HeroShelfRow(
                    title = content.attributes?.name ?: "",
                    imageUrl = content.attributes?.artwork?.url,
                    modifier = Modifier
                        .width(200.dp)
                        .clickable { /* TODO */ },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun HeroShelfRow(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium
) {
    val scope = rememberCoroutineScope()

    val dominantColor = rememberDominantColorState()
    LaunchedEffect(imageUrl) {
        scope.launch {
            if (imageUrl != null) {
                dominantColor.updateColorsFromImageUrl(imageUrl)
            }
        }
    }

    Surface(
        modifier = modifier,
        shape = shape,
        color = dominantColor.color
    ) {
        ConstraintLayout(
            Modifier.aspectRatio(3/4f)
        ) {
            val (image, text) = createRefs()

            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .constrainAs(image) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                    },
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.constrainAs(text) {
                    centerHorizontallyTo(parent)
                    top.linkTo(image.bottom)
                    bottom.linkTo(parent.bottom)
                },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = dominantColor.onColor,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun LazyListScope.lazyItemOfCoverShelf(
    resource: Resource
) {
    val label = resource.attributes?.title?.stringForDisplay ?: ""
    val contents = resource.relationships?.contents?.data.orEmpty()

    label {
        Text(label)
    }
    item {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                start = Keyline1,
                top = 8.dp,
                end = Keyline1,
                bottom = 24.dp
            )
        ) {
            items(contents) { content ->
                CoverShelfRow(
                    title = content.attributes?.name ?: "",
                    imageUrl = content.attributes?.artwork?.url,
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun CoverShelfRow(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.surface
    ) {
        ConstraintLayout {
            val (image, text) = createRefs()

            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(shape)
                    .constrainAs(image) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                    }
            )
            Column(
                modifier = Modifier.constrainAs(text) {
                    centerHorizontallyTo(parent)
                    top.linkTo(image.bottom)
                    bottom.linkTo(parent.bottom)
                },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun HeroShelfRowPreview() {
    AppTheme {
        HeroShelfRow(
            title = "Preview",
            imageUrl = "https://www.example.com/image.jpg",
            modifier = Modifier.width(200.dp)
        )
    }
}

@Preview
@Composable
private fun CoverShelfRowPreview() {
    AppTheme {
        CoverShelfRow(
            title = "Preview",
            imageUrl = "https://www.example.com/image.jpg",
            modifier = Modifier.width(200.dp)
        )
    }
}

private const val MinContrastOfPrimaryVsSurface = 3f
