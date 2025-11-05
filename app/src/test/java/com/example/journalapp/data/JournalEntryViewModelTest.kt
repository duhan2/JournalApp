package com.example.journalapp.data

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the [JournalEntryViewModel].
 *
 * This class tests the various functions of the ViewModel, ensuring that the data is handled
 * correctly and that the UI state is updated as expected.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JournalEntryViewModelTest {

    /**
     * A JUnit rule that swaps the main coroutine dispatcher with a test dispatcher.
     */
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Creates a new instance of the [JournalEntryViewModel] for testing.
     *
     * @return A new [JournalEntryViewModel] instance with a [FakeJournalEntryDao] and a test dispatcher.
     */
    private fun vm(): JournalEntryViewModel {
        val dao = FakeJournalEntryDao()
        val repo = JournalEntryRepository(dao)
        return JournalEntryViewModel(repo, mainDispatcherRule.dispatcher)
    }

    /**
     * Tests that the `entries` StateFlow correctly reflects the data from the repository,
     * sorted by date in descending order.
     */
    @Test
    fun entries_emits_from_repository() = runTest {
        val vm = vm()
        vm.entries.test {
            // Initially empty
            assertEquals(emptyList<JournalEntry>(), awaitItem())

            val e1 = JournalEntry(title = "A", content = "a", date = 1000L)
            val e2 = JournalEntry(title = "B", content = "b", date = 2000L)
            vm.upsert(e1); vm.upsert(e2); advanceUntilIdle()

            val list = awaitItem()
            assertEquals(listOf(e2.copy(id = 2), e1.copy(id = 1)), list)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests that `getEntryById` retrieves the correct journal entry.
     */
    @Test
    fun getEntryById_emits_correct_item() = runTest {
        val vm = vm()
        val id = vm.insert(JournalEntry(title = "Note", content = "X"))
        vm.getEntryById(id).test {
            val item = awaitItem()
            assertEquals("Note", item?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests that `insert` returns the new ID and persists the entry.
     */
    @Test
    fun insert_returns_id_and_persists() = runTest {
        val vm = vm()
        val id = vm.insert(JournalEntry(title = "New", content = "Body"))
        assertEquals(1L, id)
        vm.getEntryById(id).test {
            assertEquals("New", awaitItem()?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests that `update` modifies an existing entry.
     */
    @Test
    fun update_modifies_entry() = runTest {
        val vm = vm()
        val id = vm.insert(JournalEntry(title = "Old", content = "Body"))
        vm.update(JournalEntry(id = id, title = "Updated", content = "Body+", date = 9999L))
        advanceUntilIdle()
        vm.getEntryById(id).test {
            assertEquals("Updated", awaitItem()?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests that `delete` removes an entry from the database.
     */
    @Test
    fun delete_removes_entry() = runTest {
        val vm = vm()
        val id = vm.insert(JournalEntry(title = "Tmp", content = "X"))
        vm.delete(JournalEntry(id = id))
        advanceUntilIdle()
        vm.getEntryById(id).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests that `upsert` inserts a new entry if the ID is 0, and updates an existing one otherwise.
     */
    @Test
    fun upsert_inserts_then_updates() = runTest {
        val vm = vm()
        vm.entries.test {
            // 1) Initially empty
            assertEquals(emptyList<JournalEntry>(), awaitItem())

            // 2) upsert with id==0 -> Insert
            vm.upsert(JournalEntry(title = "First", content = "")) // returns Job
            advanceUntilIdle()
            val afterInsert = awaitItem()
            val created = afterInsert.first()  // now NOT empty

            // 3) upsert with existing id -> Update
            vm.upsert(created.copy(title = "Second"))
            advanceUntilIdle()
            val afterUpdate = awaitItem()

            assertEquals(
                "Second",
                afterUpdate.first { it.id == created.id }.title
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
