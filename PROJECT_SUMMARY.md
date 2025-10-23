# Buttons App - Project Summary

## Overview
A complete Android application built with Jetpack Compose that allows users to create, manage, and organize custom payment buttons with deep linking capabilities.

## Project Structure

```
buttons/
├── app/
│   ├── build.gradle.kts                           # App-level Gradle configuration
│   ├── src/main/
│   │   ├── AndroidManifest.xml                    # App manifest
│   │   ├── java/com/example/buttons/
│   │   │   ├── MainActivity.kt                    # Main activity with navigation setup
│   │   │   ├── data/                              # Data layer
│   │   │   │   ├── ButtonEntity.kt               # Room entity with PaymentType & ButtonSize enums
│   │   │   │   ├── ButtonDao.kt                  # Room DAO interface
│   │   │   │   ├── AppDatabase.kt                # Room database singleton
│   │   │   │   ├── Converters.kt                 # Type converters for enums
│   │   │   │   └── ButtonRepository.kt           # Repository pattern implementation
│   │   │   ├── viewmodel/
│   │   │   │   └── ButtonViewModel.kt            # ViewModel managing button state
│   │   │   └── ui/                               # UI layer
│   │   │       ├── HomeScreen.kt                 # Main screen with button grid
│   │   │       ├── EditButtonScreen.kt           # Button creation/editing screen
│   │   │       ├── SettingsScreen.kt             # Settings screen
│   │   │       ├── AboutScreen.kt                # About screen
│   │   │       └── theme/                        # Material 3 theme files
│   │   │           ├── Theme.kt
│   │   │           ├── Color.kt
│   │   │           └── Type.kt
│   │   └── res/
│   │       ├── values/
│   │       │   ├── strings.xml                   # App name and version
│   │       │   ├── colors.xml                    # Color resources
│   │       │   └── themes.xml                    # Theme definitions
│   │       └── mipmap-*/                         # Launcher icons (all densities)
├── build.gradle.kts                              # Project-level Gradle configuration
├── settings.gradle.kts                           # Gradle settings
├── gradle.properties                             # Gradle properties
└── README.md                                     # User documentation

```

## Features Implemented

### 1. Button Management
- ✅ Create new buttons with custom properties
- ✅ Edit existing buttons
- ✅ Delete buttons
- ✅ Reorder buttons via drag-and-drop (long-press)
- ✅ Toggle edit mode on/off

### 2. Button Properties
- ✅ **Title**: Internal identifier for the button
- ✅ **Display Text**: Text shown on the button
- ✅ **Parcels**: Number from 1-12 (slider control)
- ✅ **Payment Type**: Credit or Debit (chip selection)
- ✅ **Color**: 16 pre-defined colors (visual color picker)
- ✅ **Size**: Small (80dp), Normal (120dp), or Big (160dp)

### 3. User Interface
- ✅ **Home Screen**: Grid layout displaying buttons (2 columns)
- ✅ **Edit Mode**: Shows edit/delete controls and enables reordering
- ✅ **Floating Action Button**: Add new button (visible in edit mode)
- ✅ **Dropdown Menu**: Access edit mode, settings, and about
- ✅ **Material 3 Design**: Modern UI with proper theming

### 4. Navigation
- ✅ Home screen (default)
- ✅ Edit/Add button screen
- ✅ Settings screen
- ✅ About screen (shows app name and version)
- ✅ Navigation with back button support

### 5. Database Persistence
- ✅ Room database with SQLite backend
- ✅ All button data persisted locally
- ✅ CRUD operations (Create, Read, Update, Delete)
- ✅ Automatic position management for reordering
- ✅ Type converters for enums

### 6. Deep Linking
- ✅ Buttons trigger deep links when clicked (non-edit mode)
- ✅ Base URL: `https://example.com/payment`
- ✅ Parameters: title, parcels, type (credit/debit)
- ✅ Intent opening for external app handling

## Technical Implementation

### Architecture Pattern
- **MVVM**: Model-View-ViewModel for clear separation of concerns
- **Repository Pattern**: Data abstraction layer
- **Single Activity**: Navigation Compose for screen management

### Key Technologies
- **Jetpack Compose**: Modern declarative UI framework
- **Room**: SQLite database abstraction
- **Kotlin Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management
- **Navigation Compose**: Type-safe navigation
- **Material 3**: Latest Material Design components
- **Reorderable Library**: Drag-and-drop functionality

### Dependencies
```kotlin
// Core Android & Compose
androidx.core:core-ktx:1.12.0
androidx.lifecycle:lifecycle-runtime-ktx:2.7.0
androidx.activity:activity-compose:1.8.2
androidx.compose:compose-bom:2023.10.01

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1
androidx.room:room-compiler:2.6.1 (KSP)

// Navigation
androidx.navigation:navigation-compose:2.7.6

// ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0

// Reorderable
org.burnoutcrew.composereorderable:reorderable:0.9.6
```

### Build Configuration
- **Compile SDK**: 34 (Android 14)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34
- **Kotlin**: 1.9.20
- **Gradle**: 8.2
- **KSP**: 1.9.20-1.0.14

## How It Works

### Data Flow
1. **User Action** → UI event in Composable
2. **ViewModel** → Processes event, updates state
3. **Repository** → Handles data operations
4. **Room DAO** → Executes database queries
5. **Flow** → Emits updated data back to ViewModel
6. **Composable** → Recomposes with new state

### Button Click Behavior
- **Normal Mode**: Opens deep link with button parameters
- **Edit Mode**: Shows edit/delete controls, enables drag-and-drop

### State Management
- `ButtonViewModel` holds all buttons as `StateFlow<List<ButtonEntity>>`
- UI observes state via `collectAsState()`
- Changes automatically trigger recomposition

### Deep Link Construction
```kotlin
https://example.com/payment?title={title}&parcels={parcels}&type={credit|debit}
```
To change the base URL, modify the `buildDeeplink()` function in `HomeScreen.kt`.

## Customization Guide

### Change Base Deep Link URL
Edit `HomeScreen.kt`, line ~290:
```kotlin
private fun buildDeeplink(button: ButtonEntity): String {
    val baseUrl = "https://your-domain.com/your-path"  // Change this
    return "$baseUrl?title=${Uri.encode(button.title)}" +
            "&parcels=${button.parcels}" +
            "&type=${button.paymentType.name.lowercase()}"
}
```

### Add More Colors
Edit `EditButtonScreen.kt`, the `colors` list in `ColorPicker`:
```kotlin
val colors = listOf(
    "#YOUR_HEX" to "Color Name",
    // ... add more colors
)
```

### Modify Button Sizes
Edit `ButtonEntity.kt` and update sizes in `ButtonItem` composable:
```kotlin
val height = when (button.size) {
    ButtonSize.SMALL -> 80.dp   // Change these values
    ButtonSize.NORMAL -> 120.dp
    ButtonSize.BIG -> 160.dp
}
```

## Building and Running

### Using Android Studio
1. Open the project folder in Android Studio
2. Wait for Gradle sync to complete
3. Click Run (▶️) or press Shift+F10
4. Select a device/emulator

### Command Line Build
```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install
./gradlew installDebug
```

## Testing the App

1. **Launch**: Open the app (empty state)
2. **Add Button**: Menu → Edit Buttons → Tap FAB (+)
3. **Configure**: Fill in all fields, choose color and size
4. **Save**: Tap "Add Button"
5. **Reorder**: Long-press and drag to new position
6. **Edit**: Tap edit icon on a button
7. **Delete**: Tap delete icon on a button
8. **Use**: Exit edit mode → Tap button → Opens deep link
9. **Persist**: Close app → Reopen → All buttons remain

## Future Enhancement Ideas

- Export/import button configurations
- Button categories/folders
- Search and filter buttons
- Customizable deep link templates
- Button usage analytics
- Backup to cloud storage
- Dark mode toggle in settings
- Custom button shapes
- Button press animations
- Undo/redo functionality

## Known Limitations

- Deep links require external app to handle the URL
- Icon placeholder is a simple colored square
- No confirmation dialog for delete action
- No validation for duplicate titles
- Settings screen is placeholder only

## Version Information

- **Version**: 1.0.0
- **Version Code**: 1
- **Created**: 2025

## License

Sample application for demonstration purposes.
