@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.example.journalapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit test rule that swaps the main coroutine dispatcher with a test dispatcher.
 *
 * This rule allows you to control the execution of coroutines on the main thread in your tests,
 * making them more predictable and reliable.
 *
 * @param dispatcher The [TestDispatcher] to use as the main dispatcher. Defaults to a [StandardTestDispatcher].
 */
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    /**
     * Sets the main dispatcher to the test dispatcher before each test.
     */
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }
    /**
     * Resets the main dispatcher after each test.
     */
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
