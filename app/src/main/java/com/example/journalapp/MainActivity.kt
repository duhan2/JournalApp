package com.example.journalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.journalapp.data.JournalEntryViewModel
import com.example.journalapp.data.JournalEntryViewModelFactory
import com.example.journalapp.ui.screen.EditScreen
import com.example.journalapp.ui.screen.MainScreen
import com.example.journalapp.ui.theme.JournalAppTheme

class MainActivity : ComponentActivity() {

    private val journalEntryViewModel: JournalEntryViewModel by viewModels {
        JournalEntryViewModelFactory((application as JournalApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JournalAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable(route = "main") {
                        MainScreen(viewModel = journalEntryViewModel, onNavigateToEditor = { id ->
                            navController.navigate("edit/$id")
                        })
                    }
                    composable(route = "edit/{id}", arguments = listOf(navArgument("id") {
                        type = NavType.IntType
                    })) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        EditScreen(
                            viewModel = journalEntryViewModel,
                            entryId = id,
                            onNavigateBack = {
                                navController.popBackStack()
                            })
                    }
                }

            }
        }
    }
}