package com.example.journalapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.journalapp.data.JournalEntryViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A composable screen for editing a journal entry.
 *
 * This screen allows the user to edit the title and content of a journal entry.
 * It features an auto-save mechanism that updates the entry after a short delay of inactivity.
 * It also handles the creation of new entries and the deletion of empty entries.
 *
 * @param viewModel The [JournalEntryViewModel] for interacting with the data layer.
 * @param entryId The ID of the journal entry to be edited. If it's a new entry, this might be a special value (e.g., 0).
 * @param onNavigateBack A lambda function to be called when the user navigates back.
 */
@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(viewModel: JournalEntryViewModel, entryId: Int, onNavigateBack: () -> Boolean) {

    val entryFlow = remember(entryId) { viewModel.getEntryById(entryId) }
    val entry by entryFlow.collectAsState(initial = null)

    var title by rememberSaveable(entryId) { mutableStateOf(entry?.title.orEmpty()) }
    var content by rememberSaveable(entryId) { mutableStateOf(entry?.content.orEmpty()) }
    var prefilled by remember(entryId) { mutableStateOf(false) }

    val latestTitle by rememberUpdatedState(title)
    val latestContent by rememberUpdatedState(content)

    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        errorContainerColor = MaterialTheme.colorScheme.errorContainer,
        errorLabelColor = MaterialTheme.colorScheme.onErrorContainer
    )

    // Populates the text fields once the entry data is loaded.
    LaunchedEffect(entry?.id) {
        if (!prefilled && entry != null) {
            title = entry!!.title
            content = entry!!.content
            prefilled = true
        }
    }

    // Auto-saves the entry after a 500ms debounce period.
    LaunchedEffect(entryId, prefilled) {
        snapshotFlow { prefilled }
            .dropWhile { !it }
            .collectLatest {
                snapshotFlow { title to content }
                    .debounce(500)
                    .distinctUntilChanged()
                    .collectLatest { (t, c) ->
                        val current = entry ?: return@collectLatest
                        if (t != current.title || c != current.content) {
                            viewModel.upsert(
                                current.copy(
                                    title = t,
                                    content = c,
                                )
                            )
                        }
                    }
            }
    }

    // Cleans up the entry when the user navigates away.
    // Deletes the entry if it's empty, otherwise saves the latest changes.
    DisposableEffect(entryId) {
        onDispose {
            val e = entry
            if (e != null) {
                if (latestTitle.isBlank() && latestContent.isBlank()) {
                    viewModel.delete(e)
                } else if (latestTitle != e.title || latestContent != e.content) {
                    viewModel.upsert(
                        e.copy(
                            title = latestTitle,
                            content = latestContent,
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == -1) "Add Entry" else "Edit Entry") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                })
        }
    ) { innerPadding ->
        Surface( // Screen-Section
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!prefilled && entry == null) {
                    Text("Loading Entry …")
                }
                val ts = System.currentTimeMillis()
                Text(
                    text = "last change at " + SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(ts))
                )
                OutlinedTextField(
                    label = { Text("Title") },
                    value = title,
                    colors = colors,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}