package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for accessing journal entry data.
 *
 * This class provides a clean API for data access to the rest of the application.
 * It abstracts the data sources (in this case, the Room database) from the rest of the app.
 *
 * @param journalEntryDao The Data Access Object for journal entries.
 */
class JournalEntryRepository(private val journalEntryDao: JournalEntryDao) {

    /**
     * A flow that emits a list of all journal entries.
     */
    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getAll()

    /**
     * Retrieves a single journal entry by its ID.
     *
     * @param id The ID of the journal entry to retrieve.
     * @return A flow emitting the [JournalEntry] or null if not found.
     */
    fun getById(id: Long): Flow<JournalEntry?> {
        return journalEntryDao.getById(id)
    }

    /**
     * Inserts a new journal entry.
     *
     * @param entry The [JournalEntry] to insert.
     */
    suspend fun insert(entry: JournalEntry): Long {
        return journalEntryDao.insert(entry)
    }

    /**
     * Updates an existing journal entry.
     *
     * @param entry The [JournalEntry] to update.
     */
    suspend fun update(entry: JournalEntry) {
        journalEntryDao.update(entry)
    }

    /**
     * Deletes a journal entry.
     *
     * @param entry The [JournalEntry] to delete.
     */
    suspend fun delete(entry: JournalEntry) {
        journalEntryDao.delete(entry)
    }

    /**
     * Inserts or updates a journal entry.
     *
     * @param entry The [JournalEntry] to upsert.
     */
    suspend fun upsert(entry: JournalEntry) {
        journalEntryDao.upsert(entry)
    }

}
