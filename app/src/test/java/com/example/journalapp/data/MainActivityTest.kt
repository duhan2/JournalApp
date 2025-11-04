package com.example.journalapp.data

import androidx.navigation.compose.ComposeNavigator
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.journalapp.AppNavigation
import com.example.journalapp.ui.theme.JournalAppTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Test suite for the main activity and navigation behavior of the JournalApp.
 * This class uses Robolectric to run tests on a local JVM without the need for an emulator or physical device.
 * It specifically tests the navigation flow, argument passing, and initial state of the NavController.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.DEFAULT_MANIFEST_NAME, sdk = [34])
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var viewModel: JournalEntryViewModel

    /**
     * Sets up the test environment before each test.
     * This function initializes the mock repository, view model, and the NavController.
     * It also sets the content of the test rule with the [AppNavigation] composable.
     */
    @Before
    fun setup() {

        val repository: JournalEntryRepository = mock()

        whenever(repository.allEntries).thenReturn(flowOf(emptyList()))

        viewModel = JournalEntryViewModel(repository)

        composeTestRule.setContent {
            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            JournalAppTheme {
                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }

    /**
     * Verifies that the NavController is properly initialized.
     */
    @Test
    fun `NavController initialization`() {
        assert(::navController.isInitialized)
    }

    /**
     * Checks if the start destination of the NavHost is the main screen.
     */
    @Test
    fun `NavHost start destination`() {
        val startDestination = navController.graph.startDestinationRoute
        Assert.assertEquals("main", startDestination)
    }

    /**
     * Ensures that the initial screen displayed is the main route.
     */
    @Test
    fun `Initial screen display   main  route `() {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals("main", currentRoute)
    }

    /**
     * Tests navigation from the MainScreen to the EditScreen.
     */
    @Test
    fun `Navigation from MainScreen to EditScreen`() {
        composeTestRule.runOnUiThread {
            navController.navigate("edit/1")
        }

        val newRoute = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals("edit/{id}", newRoute)
    }

    /**
     * Verifies that arguments are correctly passed to the EditScreen with a valid ID.
     */
    @Test
    fun `Argument passing to EditScreen  valid ID `() {
        val testId = 123
        composeTestRule.runOnUiThread {
            navController.navigate("edit/$testId")
        }

        val backStackEntry = navController.currentBackStackEntry
        val receivedId = backStackEntry?.arguments?.getInt("id")
        Assert.assertEquals("edit/{id}", backStackEntry?.destination?.route)
        Assert.assertEquals(testId, receivedId)
    }

    /**
     * Verifies that arguments are correctly passed to the EditScreen with a default ID.
     */
    @Test
    fun `Argument passing to EditScreen  default ID `() {
        composeTestRule.runOnUiThread {
            navController.navigate("edit/0")
        }

        val backStackEntry = navController.currentBackStackEntry
        val receivedId = backStackEntry?.arguments?.getInt("id")
        Assert.assertEquals(0, receivedId)
    }

    /**
     * Tests the back navigation from the EditScreen to the MainScreen.
     */
    @Test
    fun `Back navigation from EditScreen`() {
        composeTestRule.runOnUiThread {
            navController.navigate("edit/1")
        }

        var currentRoute = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals("edit/{id}", currentRoute)

        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }

        currentRoute = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals("main", currentRoute)
    }
}
