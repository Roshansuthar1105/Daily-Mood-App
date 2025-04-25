# Daily Mood and Mental Health Journal Application

## Local Database Implementation

This application uses Room Database for local data storage. Room is a persistence library that provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.

### Database Architecture

The database implementation follows the Repository pattern with the following components:

1. **Entities**: Java classes that represent tables in the database
   - `User`: Stores user information
   - `MoodEntry`: Stores mood tracking data
   - `JournalEntry`: Stores journal entries

2. **DAOs (Data Access Objects)**: Interfaces that define database operations
   - `UserDao`: Operations for user data
   - `MoodEntryDao`: Operations for mood entries
   - `JournalEntryDao`: Operations for journal entries

3. **Database**: Main database class that ties everything together
   - `AppDatabase`: Defines the database configuration and serves as the main access point

4. **Repositories**: Classes that abstract the data sources
   - `UserRepository`: Handles user data operations
   - `MoodRepository`: Handles mood entry operations
   - `JournalRepository`: Handles journal entry operations

5. **ViewModels**: Classes that prepare and manage data for UI components
   - `UserViewModel`: Manages user data for UI
   - `MoodViewModel`: Manages mood data for UI
   - `JournalViewModel`: Manages journal data for UI

### Key Features

- **Local Storage**: All data is stored locally on the device
- **No External Tools Required**: No need for external database tools like MySQL Workbench
- **Asynchronous Operations**: Database operations run on background threads
- **LiveData Support**: UI updates automatically when data changes
- **Callback Pattern**: Asynchronous operations use callbacks for better flow control

### How to Use

#### Adding a Mood Entry

```java
moodViewModel.addMoodEntry("HAPPY", 8, "Feeling great today!", id -> {
    if (id != -1) {
        // Success
    } else {
        // Error
    }
});
```

#### Adding a Journal Entry

```java
journalViewModel.addJournalEntry("Title", "Content", "tags", id -> {
    if (id != -1) {
        // Success
    } else {
        // Error
    }
});
```

#### Getting User Data

```java
userViewModel.getCurrentUser().observe(this, user -> {
    if (user != null) {
        // Use user data
    }
});
```

### Running Tests

To run the database tests:

1. Connect a device or start an emulator
2. Right-click on the test file in Android Studio
3. Select "Run 'AppDatabaseTest'" or "Run 'RepositoryTest'"

Or run from the command line:

```
./gradlew connectedAndroidTest
```

### Database Initialization

The database is automatically initialized when the application starts through the `MoodJournalApplication` class.
