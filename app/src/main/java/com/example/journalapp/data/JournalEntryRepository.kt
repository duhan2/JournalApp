package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

class JournalEntryRepository(private val journalEntryDao: JournalEntryDao) {

    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getEntries()

    suspend fun insert(journalEntry: JournalEntry) {
        journalEntryDao.insert(journalEntry)
    }

    suspend fun update(journalEntry: JournalEntry) {
        journalEntryDao.update(journalEntry)
    }

    suspend fun delete(journalEntry: JournalEntry) {
        journalEntryDao.delete(journalEntry)
    }

    fun getById(id: Int): Flow<JournalEntry> {
        return journalEntryDao.getEntry(id = id)
    }

}