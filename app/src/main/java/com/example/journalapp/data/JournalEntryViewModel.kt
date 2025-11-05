package com.example.journalapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing journal entries.
 *
 * This class provides data to the UI and survives configuration changes. It acts as a
 * communication center between the Repository and the UI.
 *
 * @param repository The repository for accessing journal entry data.
 * @param ioDispatcher The coroutine dispatcher for background I/O operations.
 */
class JournalEntryViewModel(
    private val repository: JournalEntryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    /**
     * A [StateFlow] that holds the list of all journal entries.
     * The data is collected from the repository and exposed to the UI.
     * The flow is started when a UI component subscribes to it and stops 5 seconds after the last subscriber unsubscribes.
     */
    val entries: StateFlow<List<JournalEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Retrieves a single journal entry by its ID.
     *
     * @param entryId The ID of the journal entry to retrieve.
     * @return A flow emitting the [JournalEntry] or null if not found.
     */
    fun getEntryById(entryId: Long) = repository.getById(entryId)

    /**
     * Inserts a new journal entry into the database and returns its new ID.
     * The operation is performed on the [ioDispatcher].
     *
     * @param entry The [JournalEntry] to insert.
     * @return The ID of the newly inserted entry.
     */
    suspend fun insert(entry: JournalEntry): Long = withContext(ioDispatcher){
        repository.insert(entry)
    }


    /**
     * Updates an existing journal entry in the database.
     * The operation is performed on the [ioDispatcher].
     *
     * @param entry The [JournalEntry] to update.
     */
    fun update(entry: JournalEntry) = viewModelScope.launch(ioDispatcher) {
        repository.update(entry)
    }

    /**
     * Deletes a journal entry from the database.
     * The operation is performed on the [ioDispatcher].
     *
     * @param entry The [JournalEntry] to delete.
     */
    fun delete(entry: JournalEntry) = viewModelScope.launch(ioDispatcher) {
        repository.delete(entry)
    }

    /**
     * Inserts or updates a journal entry. If the entry already exists, it's updated.
     * Otherwise, it's inserted.
     * The operation is performed on the [ioDispatcher].
     *
     * @param entry The [JournalEntry] to upsert.
     */
    fun upsert(entry: JournalEntry) = viewModelScope.launch(ioDispatcher) {
        repository.upsert(entry)
    }

}

/**
 * Factory for creating [JournalEntryViewModel] instances.
 *
 * This factory allows us to pass the [JournalEntryRepository] and a [CoroutineDispatcher]
 * to the ViewModel's constructor.
 *
 * @param repository The repository for accessing journal entry data.
 * @param ioDispatcher The coroutine dispatcher for background I/O operations.
 */
class JournalEntryViewModelFactory(
    private val repository: JournalEntryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalEntryViewModel(repository, ioDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
