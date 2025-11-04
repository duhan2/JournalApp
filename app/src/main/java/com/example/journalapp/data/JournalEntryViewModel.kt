package com.example.journalapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalEntryViewModel(private val repository: JournalEntryRepository) : ViewModel() {

    val entries: StateFlow<List<JournalEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getEntryById(entryId: Int) = repository.getById(entryId)

    fun insert(entry: JournalEntry) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(entry)
    }

    fun update(entry: JournalEntry) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(entry)
    }

    fun delete(entry: JournalEntry) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(entry)
    }

    fun upsert(entry: JournalEntry) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsert(entry)
    }

}

class JournalEntryViewModelFactory(private val repository: JournalEntryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalEntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
