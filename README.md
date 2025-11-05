# Journal App

A simple, offline-first journaling application for Android, built with Jetpack Compose and other modern Android development tools.

## Features

*   **Create, Read, Update, and Delete (CRUD) Journal Entries:** Users can create new journal entries, view a list of their existing entries, edit them, and delete them.
*   **Offline-First:** All journal entries are stored locally on the device using Room, so the app works seamlessly without an internet connection.
*   **Modern UI:** The user interface is built entirely with Jetpack Compose, providing a clean and modern look and feel.
*   **Navigation:** Uses Jetpack Navigation for navigating between screens.

## Technologies Used

*   **[Kotlin](https://kotlinlang.org/):** The programming language used for the entire application.
*   **[Jetpack Compose](https://developer.android.com/jetpack/compose):** Android's modern toolkit for building native UI.
*   **[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel):** Manages UI-related data in a lifecycle-conscious way.
*   **[Room](https://developer.android.com/training/data-storage/room):** A persistence library that provides an abstraction layer over SQLite to allow for more robust database access.
*   **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Flow](https://kotlinlang.org/docs/flow.html):** Used for asynchronous and reactive programming, especially for database operations and data streams.
*   **[Jetpack Navigation](https://developer.android.com/guide/navigation):** For navigating between screens in the app.

## Project Structure

The project is organized into the following main packages:

*   `data`: Contains the data layer of the application, including:
    *   `JournalEntry.kt`: The data class representing a single journal entry.
    *   `JournalEntryDao.kt`: The Data Access Object for Room, defining database operations.
    *   `JournalDatabase.kt`: The Room database class.
    *   `JournalEntryRepository.kt`: The repository that abstracts the data source from the rest of the app.
    *   `JournalEntryViewModel.kt`: The ViewModel that provides data to the UI and handles user interactions.
*   `ui`:
    *   `screen`: Contains the Composable screens for the application:
        *   `MainScreen.kt`: Displays the list of journal entries.
        *   `EditScreen.kt`: Allows for the creation and editing of journal entries.
    *   `theme`: Contains the theming for the application, including colors, typography, and shapes.
*   `AppNavigation.kt`: Defines the navigation graph for the app.
*   `MainActivity.kt`: The main activity and entry point of the app.
*   `JournalApp.kt`: The application class.

## Setup

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    ```
2.  **Open in Android Studio:**
    Open the cloned project in Android Studio.
3.  **Build and Run:**
    Build and run the application on an Android emulator or a physical device.
