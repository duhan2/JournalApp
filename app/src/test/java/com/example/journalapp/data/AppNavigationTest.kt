package com.example.journalapp.data

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.journalapp.AppNavigation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBuild

/**
 * Unit tests for the [AppNavigation] composable.
 *
 * This class tests the navigation logic of the application, ensuring that the correct
 * destinations are reached and that arguments are passed correctly.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class AppNavigationTest {

    /**
     * The compose rule for testing composables.
     */
    @get:Rule
    val composeRule = createComposeRule()

    /**
     * Sets up the test environment.
     *
     * This method sets the Robolectric fingerprint to prevent a NullPointerException.
     */
    @Before
    fun setUp() {
        ShadowBuild.setFingerprint("robolectric") // prevents the NPE
    }

    /**
     * Tests that the start destination of the navigation is the main screen.
     */
    @Test fun startDestination_is_main() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val nav = TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeRule.setContent {
            val vm = JournalEntryViewModel(JournalEntryRepository(FakeJournalEntryDao()))
            AppNavigation(navController = nav, viewModel = vm)
        }
        assertEquals("main", nav.currentDestination?.route)
    }

    /**
     * Builds the navigation graph for testing.
     *
     * @return A pair containing the [TestNavHostController] and the [JournalEntryViewModel].
     */
    private fun buildGraph(): Pair<TestNavHostController, JournalEntryViewModel> {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }

        val dao = FakeJournalEntryDao()
        val repo = JournalEntryRepository(dao)
        val vm = JournalEntryViewModel(repo) // Default IO-Dispatcher is sufficient here

        composeRule.setContent {
            AppNavigation(navController = navController, viewModel = vm)
        }
        return navController to vm
    }

    /**
     * Tests that navigating to the edit screen with an ID sets the argument correctly.
     */
    @Test fun navigate_to_edit_sets_id_argument() {
        val (nav, _) = buildGraph()
        composeRule.runOnUiThread { nav.navigate("edit/42") }
        assertEquals("edit/{id}", nav.currentDestination?.route)
        assertEquals(42L, nav.currentBackStackEntry?.arguments?.getLong("id"))
    }

    /**
     * Tests that popping the back stack returns to the main screen.
     */
    @Test fun popBackStack_returns_to_main() {
        val (nav, _) = buildGraph()
        composeRule.runOnUiThread { nav.navigate("edit/7") }
        composeRule.runOnUiThread { nav.popBackStack() }
        assertEquals("main", nav.currentDestination?.route)
    }

    /**
     * Tests that the edit screen shows "Add Entry" when the title and content are empty.
     */
    @Test fun edit_screen_shows_AddEntry_for_empty_item() {
        val (nav, vm) = buildGraph()
        // Create an empty entry that the screen will load
        composeRule.runOnUiThread {
            // direct insert via VM
            // (date remains 0 -> FakeDao sets timestamp)
            // ID becomes 1L on first insert
        }
        // Execute insert in the test thread
        val id = kotlinx.coroutines.runBlocking {
            vm.insert(JournalEntry(title = "", content = ""))
        }
        composeRule.runOnUiThread { nav.navigate("edit/$id") }

        // Expectation: TopAppBar-Title "Add Entry" for an empty entry (see EditScreen)
        composeRule.onNodeWithText("Add Entry").assertIsDisplayed()
    }
}
