package com.example.journalapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.journalapp.data.JournalEntry
import com.example.journalapp.data.JournalEntryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(viewModel: JournalEntryViewModel) {

    val entries by viewModel.entries.collectAsState(emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Create, contentDescription = null)
            }
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(entries) { entry ->
                JournalEntryItem(entry = entry)
            }
        }
    }
}


@Composable
fun JournalEntryItem(entry: JournalEntry) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                    Date(entry.timestamp)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = entry.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
