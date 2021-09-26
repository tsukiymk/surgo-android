package com.tsukiymk.surgo.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.surgo.common.compose.NavArguments
import app.surgo.ui.albumdetails.AlbumDetailsScreen
import app.surgo.ui.artistdetails.ArtistDetailsScreen
import app.surgo.ui.playlistdetails.PlaylistDetailsScreen
import app.surgo.ui.plugindetails.PluginDetailsScreen
import com.tsukiymk.surgo.ui.pages.HomeScreens
import com.tsukiymk.surgo.ui.pages.SettingsDestinations
import com.tsukiymk.surgo.ui.pages.addHomeGraph
import com.tsukiymk.surgo.ui.pages.addSettingsGraph

@OptIn(ExperimentalMaterialApi::class)
class MainActions(
    navController: NavHostController
) {
    val back: (from: NavBackStackEntry) -> Unit = { from ->
        if (from.lifecycle.currentState == Lifecycle.State.RESUMED) {
            navController.navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = MainDestinations.HOME_ROUTE
    ) {
        navigation(
            route = MainDestinations.HOME_ROUTE,
            startDestination = HomeScreens.FEED.route
        ) {
            addHomeGraph(navController)
        }
        navigation(
            route = MainDestinations.SETTINGS_ROUTE,
            startDestination = SettingsDestinations.ROOT_ROUTE
        ) {
            addSettingsGraph(navController)
        }
        composable(
            route = "${MainDestinations.ALBUM_DETAILS_ROUTE}/{${NavArguments.ALBUM_DETAILS_ID_KEY}}",
            arguments = listOf(
                navArgument(NavArguments.ALBUM_DETAILS_ID_KEY) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            AlbumDetailsScreen(
                navigateUp = { actions.back(backStackEntry) }
            )
        }
        composable(
            route = "${MainDestinations.ARTIST_DETAILS_ROUTE}/{${NavArguments.ARTIST_DETAILS_ID_KEY}}",
            arguments = listOf(
                navArgument(NavArguments.ARTIST_DETAILS_ID_KEY) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            ArtistDetailsScreen(
                navigateUp = { actions.back(backStackEntry) }
            )
        }
        composable(
            route = "${MainDestinations.PLAYLIST_DETAILS_ROUTE}/{${NavArguments.PLAYLIST_DETAILS_ID_KEY}}",
            arguments = listOf(
                navArgument(NavArguments.PLAYLIST_DETAILS_ID_KEY) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            PlaylistDetailsScreen(
                navigateUp = { actions.back(backStackEntry) }
            )
        }
        composable(
            route = "${MainDestinations.PLUGIN_DETAILS_ROUTE}/{${NavArguments.PLUGIN_DETAILS_ID_KEY}}",
            arguments = listOf(
                navArgument(NavArguments.PLUGIN_DETAILS_ID_KEY) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            PluginDetailsScreen(
                navigateUp = { actions.back(backStackEntry) }
            )
        }
    }
}

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val SETTINGS_ROUTE = "settings"
    const val ALBUM_DETAILS_ROUTE = "albumDetails"
    const val ARTIST_DETAILS_ROUTE = "artistDetails"
    const val PLAYLIST_DETAILS_ROUTE = "playlistDetails"
    const val PLUGIN_DETAILS_ROUTE = "pluginDetails"
}
