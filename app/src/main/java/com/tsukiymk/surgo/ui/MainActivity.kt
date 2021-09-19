package com.tsukiymk.surgo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import app.surgo.common.compose.components.*
import app.surgo.common.compose.runtime.LocalContentPadding
import app.surgo.common.compose.theme.AppTheme
import app.surgo.ui.playback.PlaybackScreen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.tsukiymk.surgo.ui.pages.HomeBottomNavigation
import com.tsukiymk.surgo.ui.pages.HomeNavigationRail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
private fun AppContent() {
    ProvideWindowInsets(consumeWindowInsets = false) {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }

        val configuration = LocalConfiguration.current
        val useBottomNavigation by remember {
            derivedStateOf { configuration.smallestScreenWidthDp < 600 }
        }

        Box(Modifier.navigationBarsPadding(bottom = false)) {
            val scope = rememberCoroutineScope()
            val sheetState = rememberBottomSheetLayoutState(BottomSheetLayoutValue.Collapsed)

            val navController = rememberNavController()
            navController.enableOnBackPressed(sheetState.isCollapsed)

            BackHandler(
                enabled = sheetState.isExpanded,
                onBack = {
                    scope.launch { sheetState.collapse() }
                }
            )

            val bottomInsets = with(LocalDensity.current) { LocalWindowInsets.current.systemBars.bottom.toDp() }
            val sheetPeekHeight = when {
                useBottomNavigation -> BottomAppBarHeight + BottomNavigationHeight + bottomInsets
                else -> BottomNavigationHeight + bottomInsets
            }

            BottomSheetLayout(
                sheetState = sheetState,
                bottomBar = {
                    if (useBottomNavigation) {
                        HomeBottomNavigation(navController)
                    } else {
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .navigationBarsHeight()
                        )
                    }
                },
                sheetPeekHeight = sheetPeekHeight,
                sheetContent = { openFraction ->
                    PlaybackScreen(
                        openFraction = openFraction,
                        doCollapse = {
                            scope.launch { sheetState.collapse() }
                        },
                        toArtistDetails = { artistId ->
                            navController.navigate("${MainDestinations.ARTIST_DETAILS_ROUTE}/$artistId")
                        }
                    )
                },
                bodyContent = { contentPadding ->
                    Row(Modifier.fillMaxSize()) {
                        if (!useBottomNavigation) {
                            HomeNavigationRail(navController)
                        }
                        CompositionLocalProvider(LocalContentPadding provides contentPadding) {
                            AppNavigation(navController)
                        }
                    }
                }
            )
        }
    }
}
