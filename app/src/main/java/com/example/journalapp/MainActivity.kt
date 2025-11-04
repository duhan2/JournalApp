package com.example.journalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.journalapp.data.JournalEntryViewModel
import com.example.journalapp.data.JournalEntryViewModelFactory
import com.example.journalapp.ui.theme.JournalAppTheme

/**
 * The main activity for the Journal App.
 *
 * This activity is the entry point of the application and hosts the Jetpack Compose UI.
 * It is responsible for initializing the [JournalEntryViewModel] and setting up the navigation
 * for the app.
 */
class MainActivity : ComponentActivity() {

    /**
     * The ViewModel for journal entries.
     *
     * This is lazily initialized using the `viewModels` delegate and a custom factory
     * to provide the repository from the Application class.
     */
    private val journalEntryViewModel: JournalEntryViewModel by viewModels {
        JournalEntryViewModelFactory((application as JournalApp).repository)
    }

    /**
     * Called when the activity is first created.
     *
     * This method sets up the edge-to-edge display, and sets the content of the activity
     * to the [JournalAppTheme] which contains the [AppNavigation] composable.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied in
     * [onSaveInstanceState]. Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JournalAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController, viewModel = journalEntryViewModel)
            }
        }
    }
}
