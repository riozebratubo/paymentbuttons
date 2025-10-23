# Buttons App

A Jetpack Compose Android application for managing and organizing custom payment buttons.

## Features

- **Button Management**: Create, edit, reorder, and remove custom buttons
- **Customization Options**:
  - Title and display text
  - Number of parcels (1-12)
  - Payment type (Credit/Debit)
  - 16 color choices
  - Size options (Small, Normal, Big)
- **Edit Mode**: Toggle edit mode to manage buttons with drag-and-drop reordering
- **Deep Linking**: Buttons open deeplinks with parameters based on button data
- **SQLite Persistence**: All button data is stored locally using Room database
- **Navigation**: Settings and About screens accessible from dropdown menu

## Technical Details

### Architecture
- MVVM (Model-View-ViewModel) pattern
- Jetpack Compose for UI
- Room database for persistence
- Kotlin Coroutines and Flow for async operations

### Key Components
- **Room Database**: SQLite database with ButtonEntity, ButtonDao, and Repository
- **ViewModel**: ButtonViewModel manages state and business logic
- **Navigation**: Navigation Compose for screen navigation
- **Reorderable Library**: Drag-and-drop functionality for button reordering

### Deep Link Format
When a button is clicked, it opens a deep link with the following format:
```
https://example.com/payment?title={title}&parcels={parcels}&type={credit|debit}
```

You can change the base URL in `HomeScreen.kt` by modifying the `buildDeeplink` function.

## Building the App

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

### Requirements
- Android Studio Hedgehog or later
- Android SDK 34
- Minimum SDK 24 (Android 7.0)

## Usage

1. **First Launch**: The home screen will be empty. Tap the menu (three dots) in the top right
2. **Enable Edit Mode**: Select "Edit Buttons" from the menu
3. **Add Buttons**: Tap the floating action button (+) to create a new button
4. **Customize**: Fill in the button details and choose colors, size, and payment options
5. **Reorder**: Long-press and drag buttons to reorder them
6. **Edit/Delete**: In edit mode, use the edit and delete icons on each button
7. **Use Buttons**: Exit edit mode and tap buttons to trigger deep links

## Version

Current version: 1.0.0

## License

This is a sample application for demonstration purposes.
