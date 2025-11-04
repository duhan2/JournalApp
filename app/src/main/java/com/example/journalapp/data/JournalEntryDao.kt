package com.example.journalapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the journal_entries table.
 *
 * This interface defines the methods for interacting with the journal entry data in the database.
 */
@Dao
interface JournalEntryDao {

    /**
     * Retrieves all journal entries from the database, ordered by their ID in descending order.
     *
     * @return A [Flow] of a list of [JournalEntry] objects.
     */
    @Query("SELECT * FROM journal_entries ORDER BY id DESC")
    fun getAll(): Flow<List<JournalEntry>>

    /**
     * Retrieves a single journal entry by its ID.
     *
     * @param id The ID of the journal entry to retrieve.
     * @return A [Flow] of the [JournalEntry] or null if not found.
     */
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun getById(id: Int): Flow<JournalEntry?>

    /**
     * Inserts a new journal entry into the database.
     *
     * @param entry The [JournalEntry] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry)

    /**
     * Updates an existing journal entry in the database.
     *
     * @param entry The [JournalEntry] to update.
     */
    @Update
    suspend fun update(entry: JournalEntry)

    /**
     * Deletes a journal entry from the database.
     *
     * @param entry The [JournalEntry] to delete.
     */
    @Delete
    suspend fun delete(entry: JournalEntry)

    /**
     * Inserts or updates a journal entry. If the entry already exists, it's updated.
     * Otherwise, it's inserted.
     *
     * @param entry The [JournalEntry] to upsert.
     */
    suspend fun upsert(entry: JournalEntry) {
        if (entry.id == 0) {
            insert(entry)
        } else {
            update(entry)
        }
    }

    /**
     * Creates a new draft journal entry with default title and content.
     *
     * @return The ID of the newly created draft entry.
     */
    suspend fun createDraft(): Int {
        val draft = JournalEntry(title = "New Entry", content = "", isDraft = true)
        insert(draft)
        //This is not ideal, but for the sake of simplicity we will assume the last inserted id is the one we just inserted
        return getLastInsertedId()?.id ?: 0
    }

    /**
     * Retrieves the last inserted journal entry from the database.
     *
     * @return The last inserted [JournalEntry] or null if the table is empty.
     */
    @Query("SELECT * FROM journal_entries ORDER BY id DESC LIMIT 1")
    suspend fun getLastInsertedId(): JournalEntry?

}
