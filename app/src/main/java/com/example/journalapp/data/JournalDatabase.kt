package com.example.journalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for the Journal App.
 *
 * This class defines the database configuration and serves as the main access point to the
 * persisted data.
 */
@Database(entities = [JournalEntry::class], version = 1, exportSchema = false)
abstract class JournalDatabase : RoomDatabase() {

    /**
     * Returns the Data Access Object for the journal_entries table.
     *
     * @return An instance of [JournalEntryDao].
     */
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var INSTANCE: JournalDatabase? = null

        /**
         * Returns a singleton instance of the [JournalDatabase].
         *
         * @param context The application context.
         * @return The singleton instance of the [JournalDatabase].
         */
        fun getDatabase(context: Context): JournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
