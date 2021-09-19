package app.surgo.ui.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.surgo.common.compose.components.Chip
import app.surgo.common.compose.components.SearchTextField
import app.surgo.common.compose.components.rememberDominantColorState
import app.surgo.common.compose.utils.horizontalGradientBackground
import app.surgo.data.entities.CategoryEntity
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExploreScreen(
    toArtistDetails: (Long) -> Unit = {}
) {
    val viewModel = hiltViewModel<ExploreViewModel>()
    val viewState by viewModel.state.collectAsState()

    ExploreScreen(
        viewState = viewState
    ) { action ->
        when (action) {
            is ExploreAction.OpenArtistDetails -> toArtistDetails(action.artistId)
            else -> viewModel.submitAction(action)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ExploreScreen(
    viewState: ExploreViewState,
    emit: (ExploreAction) -> Unit
) {
    val searchState = rememberSearchState()

    Scaffold(
        topBar = {
            ExploreTopAppBar(
                state = searchState,
                onSearch = { emit(ExploreAction.Search(searchState.searchQuery.text)) }
            )
        }
    ) {
        when (searchState.searchDisplay) {
            SearchDisplay.Categories -> CategoriesContent(viewState.categories)
            //SearchDisplay.Suggestions -> SearchSuggestions(suggestions)
            SearchDisplay.Results -> {
                SearchResultsContent(
                    results = viewState.searchResults,
                    emit = emit
                )
            }
            SearchDisplay.NoResults -> SearchNoResultsContent()
        }
    }
}

@Stable
private class SearchState(
    searchQuery: TextFieldValue,
    isSearchActive: Boolean
) {
    var searchQuery by mutableStateOf(searchQuery)
    var searchFilters by mutableStateOf(SearchTypes.values().toSet())

    var isSearchActive by mutableStateOf(isSearchActive)
    var isNoResults by mutableStateOf(false)

    val searchDisplay: SearchDisplay
        get() = when {
            !isSearchActive && searchQuery.text.isEmpty() -> SearchDisplay.Categories
            isNoResults -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }
}

@Composable
private fun rememberSearchState(
    searchQuery: TextFieldValue = TextFieldValue(""),
    isSearchActive: Boolean = false
): SearchState {
    return remember {
        SearchState(
            searchQuery = searchQuery,
            isSearchActive = isSearchActive
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ExploreTopAppBar(
    modifier: Modifier = Modifier,
    state: SearchState = rememberSearchState(),
    onSearch: (KeyboardActionScope.() -> Unit)? = null
) {
    val hasWindowFocus = LocalWindowInfo.current.isWindowFocused
    val keyboardVisible = LocalWindowInsets.current.ime.isVisible
    var isFocused by remember { mutableStateOf(false) }
    state.isSearchActive = isFocused && hasWindowFocus && keyboardVisible

    Surface(modifier) {
        Column(Modifier.statusBarsPadding()) {
            Row(Modifier.padding(start = 16.dp, top = 16.dp)) {
                Text(
                    text = stringResource(R.string.title_explore),
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
                )
            }
            Box {
                SearchTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .onFocusChanged { isFocused = it.isFocused },
                    value = state.searchQuery,
                    onValueChange = { state.searchQuery = it },
                    onSearch = onSearch
                )
            }
            AnimatedVisibility(
                visible = isFocused,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                val filters = remember { SearchTypes.values() }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(filters) { filter ->
                        val selected = state.searchFilters.contains(filter)

                        Chip(
                            modifier = Modifier.toggleable(
                                value = selected,
                                onValueChange = {
                                    state.searchFilters = if (selected) {
                                        state.searchFilters.minus(filter)
                                    } else {
                                        state.searchFilters.plus(filter)
                                    }
                                }
                            ),
                            color = when {
                                selected -> MaterialTheme.colors.primary.copy(alpha = 0.08f)
                                else -> MaterialTheme.colors.surface
                            },
                            contentColor = when {
                                selected -> Color.Transparent
                                else -> MaterialTheme.colors.onSurface.copy(alpha = 0.24f)
                            }
                        ) {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary
                                )
                            } else {
                                Icon(
                                    painter = painterResource(filter.drawable),
                                    contentDescription = null,
                                    tint = filter.color
                                )
                            }
                            Text(
                                text = stringResource(filter.label),
                                color = when {
                                    selected -> MaterialTheme.colors.primary
                                    else -> Color.Unspecified
                                },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoriesContent(
    categories: List<CategoryEntity>
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 2),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            CategoryContent(
                category = category,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun CategoryContent(
    category: CategoryEntity,
    modifier: Modifier = Modifier
) {
    val colorState = rememberDominantColorState()
    val imageUrl = category.imageUri
    if (imageUrl != null) {
        LaunchedEffect(imageUrl) {
            colorState.updateColorsFromImageUrl(imageUrl)
        }
    } else {
        colorState.reset()
    }
    val colors = remember { listOf(colorState.color, colorState.color.copy(alpha = 0.6f)) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .height(100.dp)
            .clip(CategoryShape)
            .horizontalGradientBackground(colors)
            .clickable { /* TODO */ },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = category.name,
            style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
            color = colorState.onColor
        )
        Image(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Bottom)
                .graphicsLayer(translationX = 40f, rotationZ = 32f, shadowElevation = 16f),
            painter = rememberImagePainter(category.imageUri),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

private val CategoryShape = RoundedCornerShape(8.dp)
