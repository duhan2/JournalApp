package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

class JournalEntryRepository(private val journalEntryDao: JournalEntryDao) {

    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getEntries()

    fun getById(id: Int): Flow<JournalEntry> {
        return journalEntryDao.getEntry(id = id)
    }

    suspend fun insert(journalEntry: JournalEntry): Int {
        return journalEntryDao.insert(journalEntry).toInt()
    }
    //Erschaffe leeren Eintrag
    suspend fun createDraft(): Int {
        return insert(JournalEntry(title = "", content = ""))
    }

    suspend fun update(journalEntry: JournalEntry) {
        journalEntryDao.update(journalEntry)
    }

    suspend fun delete(journalEntry: JournalEntry) {
        journalEntryDao.delete(journalEntry)
    }

    suspend fun upsert(journalEntry: JournalEntry) {
        journalEntryDao.upsert(journalEntry)
    }

}