package com.example.journalapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


/**
 * A JUnit TestWatcher that swaps the main coroutine dispatcher with a test dispatcher.
 * This rule allows tests to control the execution of coroutines that use `Dispatchers.Main`.
 *
 * @param testDispatcher The [TestDispatcher] to use as the main dispatcher. Defaults to [UnconfinedTestDispatcher].
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    /**
     * Replaces the main dispatcher with the [testDispatcher] before the test starts.
     */
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Resets the main dispatcher to its original state after the test finishes.
     */
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

/**
 * Test suite for [JournalEntryViewModel].
 *
 * This class contains unit tests for the various functions of the [JournalEntryViewModel],
 * ensuring that it interacts correctly with the [JournalEntryRepository].
 */
@ExperimentalCoroutinesApi
class JournalEntryViewModelTest {

    // This rule swaps the background executor used by the Architecture Components with a different one which executes each task synchronously.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // This rule handles setting and resetting the main coroutine dispatcher for tests.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: JournalEntryViewModel
    private lateinit var repository: JournalEntryRepository
    private val testEntries = listOf(JournalEntry(1, "Test Title", "Test Content"))
    private val testEntry = JournalEntry(1, "Test Title", "Test Content")

    /**
     * Sets up the test environment before each test.
     * This involves mocking the repository, defining its behavior, and initializing the ViewModel.
     */
    @Before
    fun setup() {

        repository = mock()

        // Whenever allEntries is accessed on the repository, return a flow containing our test entries.
        whenever(repository.allEntries).thenReturn(flowOf(testEntries))

        // Initialize the ViewModel with the mock repository and the test dispatcher.
        viewModel = JournalEntryViewModel(repository, mainDispatcherRule.testDispatcher)
    }

    /**
     * Tests that the ViewModel correctly retrieves all journal entries from the repository.
     */
    @Test
    fun `test get all entries`() = runTest {
        val expectedEntries = testEntries

        // Collect the first non-empty list from the entries flow.
        val actualEntries = viewModel.entries.first { it.isNotEmpty() }

        // Assert that the collected entries match the expected entries.
        assertEquals(expectedEntries, actualEntries)
    }

    /**
     * Tests that the `insert` function on the ViewModel correctly calls the repository's `insert` method.
     */
    @Test
    fun `test insert entry`() = runTest {
        viewModel.insert(testEntry)
        // Verify that the insert method was called on the repository with the test entry.
        verify(repository).insert(testEntry)
    }

    /**
     * Tests that the `update` function on the ViewModel correctly calls the repository's `update` method.
     */
    @Test
    fun `test update entry`() = runTest {
        viewModel.update(testEntry)
        // Verify that the update method was called on the repository with the test entry.
        verify(repository).update(testEntry)
    }

    /**
     * Tests that the `delete` function on the ViewModel correctly calls the repository's `delete` method.
     */
    @Test
    fun `test delete entry`() = runTest {
        viewModel.delete(testEntry)
        // Verify that the delete method was called on the repository with the test entry.
        verify(repository).delete(testEntry)
    }

    /**
     * Tests that the `upsert` function on the ViewModel correctly calls the repository's `upsert` method.
     */
    @Test
    fun `test upsert entry`() = runTest {
        viewModel.upsert(testEntry)
        // Verify that the upsert method was called on the repository with the test entry.
        verify(repository).upsert(testEntry)
    }

    /**
     * Tests that the `createDraft` function on the ViewModel correctly calls the repository's `createDraft` method
     * and returns the expected result.
     */
    @Test
    fun `test create draft`() = runTest {
        // Define the behavior of the mock repository's createDraft method.
        whenever(repository.createDraft()).thenReturn(1)
        val result = viewModel.createDraft()
        // Assert that the result from the ViewModel matches the expected result.
        assertEquals(1, result)
    }
}
