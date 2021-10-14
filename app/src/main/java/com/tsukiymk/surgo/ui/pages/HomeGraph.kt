package com.tsukiymk.surgo.ui.pages

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.surgo.common.compose.components.BottomNavigationHeight
import app.surgo.common.compose.components.NavigationRail
import app.surgo.common.compose.theme.AppTheme
import app.surgo.ui.explore.ExploreScreen
import app.surgo.ui.feed.FeedScreen
import app.surgo.ui.library.LibraryScreen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.tsukiymk.surgo.R
import com.tsukiymk.surgo.ui.MainDestinations

enum class HomeScreens(
    @StringRes val title: Int,
    val imageVector: ImageVector,
    val route: String
) {
    FEED(R.string.title_feed, Icons.Default.Home, "${MainDestinations.HOME_ROUTE}/feed"),
    EXPLORE(R.string.title_explore, Icons.Default.Explore, "${MainDestinations.HOME_ROUTE}/explore"),
    LIBRARY(R.string.title_library, Icons.Default.LibraryMusic, "${MainDestinations.HOME_ROUTE}/library")
}

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.addHomeGraph(
    navController: NavController
) {
    composable(HomeScreens.FEED.route) {
        FeedScreen(
            toSettings = {
                navController.navigate(MainDestinations.SETTINGS_ROUTE)
            },
            toPlaylistDetails = { playlistId ->
                navController.navigate("${MainDestinations.PLAYLIST_DETAILS_ROUTE}/$playlistId")
            }
        )
    }
    composable(HomeScreens.EXPLORE.route) {
        ExploreScreen(
            toArtistDetails = { artistId ->
                navController.navigate("${MainDestinations.ARTIST_DETAILS_ROUTE}/$artistId")
            }
        )
    }
    composable(HomeScreens.LIBRARY.route) {
        LibraryScreen()
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(
    startDestination: HomeScreens
): State<HomeScreens> {
    val selectedItem = remember { mutableStateOf(startDestination) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == HomeScreens.FEED.route } -> {
                    selectedItem.value = HomeScreens.FEED
                }
                destination.hierarchy.any { it.route == HomeScreens.EXPLORE.route } -> {
                    selectedItem.value = HomeScreens.EXPLORE
                }
                destination.hierarchy.any { it.route == HomeScreens.LIBRARY.route } -> {
                    selectedItem.value = HomeScreens.LIBRARY
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

@Composable
fun HomeBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier,
    startDestination: HomeScreens = HomeScreens.FEED
) {
    Surface(modifier) {
        val tabs = remember { HomeScreens.values() }

        val currentSelectedTab by navController.currentScreenAsState(startDestination)
        val currentIndex = tabs.find { it == currentSelectedTab }?.ordinal ?: startDestination.ordinal

        HomeBottomNavigationLayout(
            selectedIndex = currentIndex,
            itemCount = tabs.size,
            modifier = Modifier.navigationBarsPadding(start = false, end = false)
        ) {
            tabs.forEach { tab ->
                val selected = currentSelectedTab == tab
                val colorState by animateColorAsState(
                    if (selected) {
                        MaterialTheme.colors.secondary
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    }
                )

                HomeBottomNavigationItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(tab.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = tab.imageVector,
                            contentDescription = null,
                            tint = colorState
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(tab.title),
                            color = colorState,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeBottomNavigationLayout(
    selectedIndex: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = BottomNavigationAnimationSpec,
    indicator: @Composable BoxScope.() -> Unit = { HomeBottomNavigationIndicator() },
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, selectionFraction ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animationSpec) {
            selectionFraction.animateTo(target, animationSpec)
        }
    }

    // Animate the position of the indicator
    val indicatorIndex = remember { Animatable(0f) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animationSpec)
    }

    Layout(
        modifier = modifier.height(BottomNavigationHeight),
        content = {
            content()
            Box(
                modifier = Modifier.layoutId("indicator"),
                content = indicator
            )
        }
    ) { measurables, constraints ->
        check(itemCount == (measurables.size - 1))

        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = 2 * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                // Animate item's width based upon the selection amount
                val width =
                    lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
private fun HomeBottomNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (BoxScope.() -> Unit)? = null,
    label: @Composable (BoxScope.() -> Unit)? = null,
    animationSpec: AnimationSpec<Float> = BottomNavigationAnimationSpec
) {
    Box(
        modifier = modifier
            .then(BottomNavigationItemHorizontalPadding)
            .clip(BottomNavigationIndicatorShape)
            .selectable(selected = selected, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val animationProgress by animateFloatAsState(
            targetValue = if (selected) 1f else 0f,
            animationSpec = animationSpec
        )

        Layout(
            content = {
                if (icon != null) {
                    Box(
                        modifier = Modifier.layoutId("icon")
                            .padding(horizontal = 2.dp),
                        content = icon
                    )
                }

                val scale = lerp(0.6f, 1f, animationProgress)

                if (label != null) {
                    Box(
                        modifier = Modifier.layoutId("label")
                            .padding(horizontal = 2.dp)
                            .graphicsLayer {
                                alpha = animationProgress
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin(0f, 0.5f)
                            },
                        content = label
                    )
                }
            }
        ) { measurables, constraints ->
            val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
            val labelPlaceable = measurables.first { it.layoutId == "label" }.measure(constraints)

            val width = constraints.maxWidth
            val height = constraints.maxHeight

            val iconY = (height - iconPlaceable.height) / 2
            val labelY = (height - labelPlaceable.height) / 2

            val labelWidth = labelPlaceable.width * animationProgress
            val iconX = (width - labelWidth - iconPlaceable.width) / 2
            val labelX = iconX + iconPlaceable.width

            layout(width, height) {
                iconPlaceable.placeRelative(iconX.toInt(), iconY)
                if (animationProgress != 0f) {
                    labelPlaceable.placeRelative(labelX.toInt(), labelY)
                }
            }
        }
    }
}

@Composable
private fun HomeBottomNavigationIndicator(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
    color: Color = MaterialTheme.colors.secondary,
    shape: Shape = BottomNavigationIndicatorShape
) {
    Spacer(
        modifier.fillMaxSize()
            .then(BottomNavigationItemHorizontalPadding)
            .border(strokeWidth, color, shape)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeNavigationRail(
    navController: NavController,
    modifier: Modifier = Modifier,
    startDestination: HomeScreens = HomeScreens.FEED
) {
    NavigationRail(
        modifier = modifier,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyEnd = false
        )
    ) {
        val items = remember { HomeScreens.values() }

        val currentSelectedItem by navController.currentScreenAsState(startDestination)

        items.forEach { item ->
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = item.imageVector,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(item.title))
                },
                alwaysShowLabel = false,
                selected = item == currentSelectedItem,
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}

@Preview
@Composable
private fun HomeBottomNavigationPreview() {
    AppTheme {
        HomeBottomNavigation(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun HomeNavigationRailPreview() {
    AppTheme {
        HomeNavigationRail(navController = rememberNavController())
    }
}

private val BottomNavigationAnimationSpec = SpringSpec<Float>(
    stiffness = 800f,
    dampingRatio = 0.8f
)

private val BottomNavigationIndicatorShape = RoundedCornerShape(percent = 50)

private val BottomNavigationItemHorizontalPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
