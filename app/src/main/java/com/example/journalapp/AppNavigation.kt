package com.example.journalapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.journalapp.data.JournalEntryViewModel
import com.example.journalapp.ui.screen.EditScreen
import com.example.journalapp.ui.screen.MainScreen

/**
 * Defines the navigation graph for the Journal App.
 *
 * This composable function sets up the navigation routes and connects them to the corresponding
 * screens. It uses a [NavHost] to manage the navigation between the [MainScreen] and the [EditScreen].
 *
 * @param navController The [NavHostController] that manages the app's navigation stack.
 * @param viewModel The [JournalEntryViewModel] that provides data to the screens.
 */
@Composable
fun AppNavigation(navController: NavHostController, viewModel: JournalEntryViewModel) {
    NavHost(navController = navController, startDestination = "main") {
        composable(route = "main") {
            MainScreen(viewModel = viewModel, onNavigateToEditor = { id ->
                navController.navigate("edit/$id")
            })
        }
        composable(route = "edit/{id}", arguments = listOf(navArgument("id") {
            type = NavType.IntType
        })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditScreen(
                viewModel = viewModel,
                entryId = id,
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}
