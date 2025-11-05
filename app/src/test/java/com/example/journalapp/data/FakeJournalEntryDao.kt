package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * A fake implementation of [JournalEntryDao] for testing purposes.
 *
 * This class simulates the behavior of the real DAO by storing journal entries in an in-memory list.
 * This allows for predictable and controlled testing of the ViewModel and other components that
 * depend on the DAO.
 */
class FakeJournalEntryDao : JournalEntryDao {
    private val items = mutableListOf<JournalEntry>()
    private val state = MutableStateFlow<List<JournalEntry>>(emptyList())
    private var nextId = 1L

    private fun emit() {
        state.value = items.sortedByDescending { it.date }
    }

    /**
     * Returns a flow of all journal entries, sorted by date in descending order.
     */
    override fun getAll(): Flow<List<JournalEntry>> = state

    /**
     * Returns a flow of a single journal entry by its ID.
     */
    override fun getById(id: Long): Flow<JournalEntry?> =
        state.map { list -> list.firstOrNull { it.id == id } }.distinctUntilChanged()

    /**
     * Inserts a new journal entry and assigns a new ID.
     * If the entry's date is 0, it sets the current system time.
     * @return The ID of the newly inserted entry.
     */
    override suspend fun insert(entry: JournalEntry): Long {
        val id = nextId++
        val withId = entry.copy(id = id, date = if (entry.date == 0L) System.currentTimeMillis() else entry.date)
        items.add(withId)
        emit()
        return id
    }

    /**
     * Updates an existing journal entry.
     */
    override suspend fun update(entry: JournalEntry) {
        val idx = items.indexOfFirst { it.id == entry.id }
        if (idx >= 0) {
            items[idx] = entry
            emit()
        }
    }

    /**
     * Deletes a journal entry.
     */
    override suspend fun delete(entry: JournalEntry) {
        items.removeAll { it.id == entry.id }
        emit()
    }

    /**
     * Inserts or updates a journal entry.
     */
    override suspend fun upsert(entry: JournalEntry) {
        if (entry.id == 0L) insert(entry) else update(entry)
    }

    /**
     * Returns the journal entry with the highest ID.
     */
    override suspend fun getLastInsertedId(): JournalEntry? =
        items.maxByOrNull { it.id }
}