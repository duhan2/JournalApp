package com.example.journalapp.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.journalapp.data.JournalEntry
import com.example.journalapp.data.JournalEntryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The main screen of the application, displaying a list of journal entries.
 *
 * This screen shows a list of all journal entries and includes a floating action button (FAB)
 * to create a new entry. Tapping on an entry navigates to the [EditScreen] for that entry.
 * A long press on an entry opens a dialog to confirm its deletion.
 *
 * @param viewModel The [JournalEntryViewModel] for accessing and managing journal entry data.
 * @param onNavigateToEditor A callback function to navigate to the editor screen, passing the entry ID.
 */
@Composable
fun MainScreen(viewModel: JournalEntryViewModel, onNavigateToEditor: (Long) -> Unit) {

    val entries by viewModel.entries.collectAsState(emptyList())

    var chosenEntry by remember { mutableStateOf<JournalEntry?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    scope.launch {
                        val id = viewModel.insert(JournalEntry())
                        onNavigateToEditor(id)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(imageVector = Icons.Default.Create, contentDescription = null)
            }
        }) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(entries) { entry ->
                    JournalEntryItem(entry = entry, onCLick = {
                        onNavigateToEditor(entry.id)
                    }, onLongClick = {
                        chosenEntry = entry
                    })
                }
            }
            if (chosenEntry != null) {
                EntryAlertDialog(onDismissRequest = { chosenEntry = null }, onConfirmation = {
                    viewModel.delete(chosenEntry!!)
                    chosenEntry = null
                })
            }
        }
    }
}

/**
 * A composable that displays a single journal entry as a card.
 *
 * This item is clickable to open the entry and supports a long click to initiate a delete action.
 *
 * @param entry The [JournalEntry] to display.
 * @param onCLick The callback function to be invoked when the item is clicked.
 * @param onLongClick The callback function to be invoked when the item is long-clicked.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JournalEntryItem(
    entry: JournalEntry,
    onCLick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = { onCLick() }, onLongClick = { onLongClick() }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault()).format(
                    Date(entry.date)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = entry.content, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}

/**
 * An alert dialog for confirming the deletion of a journal entry.
 *
 * @param onDismissRequest The callback function to be invoked when the dialog is dismissed.
 * @param onConfirmation The callback function to be invoked when the user confirms the deletion.
 */
@Composable
fun EntryAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        title = { Text(text = "Delete entry ?") },
        text = { Text(text = "This action cannot be undone.") },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
