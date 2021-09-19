package com.tsukiymk.surgo.ui.pages

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.surgo.ui.settings.plugin.PluginSettingsScreen
import app.surgo.ui.settings.root.RootSettingsScreen
import com.tsukiymk.surgo.ui.MainDestinations

fun NavGraphBuilder.addSettingsGraph(
    navController: NavController
) {
    composable(SettingsDestinations.ROOT_ROUTE) {
        RootSettingsScreen(
            toPluginSettings = {
                navController.navigate(SettingsDestinations.PLUGIN_ROUTE)
            },
            navigateUp = { navController.navigateUp() }
        )
    }
    composable(SettingsDestinations.PLUGIN_ROUTE) {
        PluginSettingsScreen(
            navigateUp = { navController.navigateUp() }
        )
    }
}

object SettingsDestinations {
    const val ROOT_ROUTE = "${MainDestinations.SETTINGS_ROUTE}/root"
    const val PLUGIN_ROUTE = "${MainDestinations.SETTINGS_ROUTE}/plugin"
}
