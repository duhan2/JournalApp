package com.example.journalapp

import android.app.Application
import com.example.journalapp.data.JournalDatabase
import com.example.journalapp.data.JournalEntryRepository

/**
 * The Application class for the Journal App.
 *
 * This class is responsible for initializing and providing application-wide singletons,
 * such as the database and repository.
 */
class JournalApp: Application() {
    /**
     * Lazily initializes the [JournalDatabase].
     * The database is created only when it's first needed, rather than at application startup.
     */
    val database by lazy { JournalDatabase.getDatabase(this) }

    /**
     * Lazily initializes the [JournalEntryRepository].
     * The repository is created only when it's first needed, using the DAO from the database.
     */
    val repository by lazy { JournalEntryRepository(database.journalEntryDao()) }
}
