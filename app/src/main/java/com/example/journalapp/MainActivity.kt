package com.example.journalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.journalapp.data.JournalEntryViewModel
import com.example.journalapp.data.JournalEntryViewModelFactory
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
                MainScreen(viewModel = journalEntryViewModel)
            }
        }
    }
}
