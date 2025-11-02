package com.example.journalapp

import android.app.Application
import com.example.journalapp.data.JournalDatabase
import com.example.journalapp.data.JournalEntryRepository

class JournalApp: Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { JournalDatabase.getDatabase(this) }
    val repository by lazy { JournalEntryRepository(database.journalEntryDao()) }
}