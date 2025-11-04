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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    //Tippen wird nicht überschrieben dadurch
    LaunchedEffect(entry?.id) {
        if (!prefilled && entry != null) {
            title = entry!!.title
            content = entry!!.content
            prefilled = true
        }
    }

    // === Autosave ===
    LaunchedEffect(entryId, prefilled) {
        // Warte bis die ursprünglichen Werte einmal in die Textfelder übernommen wurden
        snapshotFlow { prefilled }
            .dropWhile { !it }
            .collectLatest {
                // Ab jetzt Eingaben beobachten und speichern
                snapshotFlow { title to content }
                    .debounce(500)               // 0,5s nach der letzten Eingabe
                    .distinctUntilChanged()      // nur echte Änderungen
                    .collectLatest { (t, c) ->
                        val current = entry ?: return@collectLatest
                        // Timestamp bleibt unverändert; nur Title/Content werden gespeichert
                        viewModel.upsert(current.copy(title = t, content = c))
                    }
            }
    }
    //So wird beim Verlassen (z. B. Back-Button in der TopAppBar) ein letzter Save ausgelöst, falls gerade noch getippt wurde.
    DisposableEffect(entryId) {
        onDispose {
            val current = entry
            if (current != null) {
                viewModel.upsert(current.copy(title = latestTitle, content = latestContent))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == -1) "Add Entry" else "Edit Entry") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!prefilled && entry == null) {
                Text("Lade Eintrag …")
            }
            Text(text = entry?.id.toString())
            OutlinedTextField(
                label = { Text("Title") },
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth()
            )
            val ts = entry?.timestamp ?: System.currentTimeMillis()
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(Date(ts))
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
