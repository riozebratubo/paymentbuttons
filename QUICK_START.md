# Quick Start Guide

## Getting Started with the Buttons App

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 8 or later
- Android SDK with API level 34

### Opening the Project

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `buttons` folder
4. Click "OK"
5. Wait for Gradle sync to complete (this may take a few minutes on first run)

### Running the App

#### On an Emulator
1. Click "Device Manager" in Android Studio
2. Create a new virtual device (if needed):
   - Choose a device definition (e.g., Pixel 5)
   - Select a system image (API 34 recommended)
   - Finish setup
3. Click the Run button (▶️) or press Shift+F10
4. Select your emulator from the list

#### On a Physical Device
1. Enable Developer Options on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Go to Settings → Developer Options
   - Turn on "USB Debugging"
3. Connect your device via USB
4. Click Run and select your device

### First Use

1. **App Opens**: You'll see an empty home screen
2. **Open Menu**: Tap the three dots (⋮) in the top-right corner
3. **Enable Edit Mode**: Select "Edit Buttons"
4. **Add Your First Button**:
   - Tap the floating action button (+) at the bottom-right
   - Fill in the form:
     - Title: "Coffee Purchase" (internal name)
     - Display Text: "Buy Coffee" (shown on button)
     - Parcels: Slide to 3
     - Payment Type: Select "Credit"
     - Color: Choose your favorite color
     - Size: Select "Normal"
   - Tap "Add Button"
5. **View Your Button**: Your new button appears on the home screen
6. **Exit Edit Mode**: Tap menu → "Exit Edit Mode"
7. **Test the Button**: Tap the button to trigger the deep link

### Managing Buttons

#### Edit a Button
1. Enter edit mode (menu → Edit Buttons)
2. Tap the edit icon (✏️) on any button
3. Modify the fields
4. Tap "Update Button"

#### Delete a Button
1. Enter edit mode
2. Tap the delete icon (🗑️) on any button
3. The button is removed immediately

#### Reorder Buttons
1. Enter edit mode
2. Long-press on a button
3. Drag it to the desired position
4. Release to drop

### Understanding Deep Links

When you tap a button (in normal mode), it opens a deep link URL like:
```
https://example.com/payment?title=Coffee%20Purchase&parcels=3&type=credit
```

By default, this will:
- Try to open in a browser (if no app handles the URL)
- Or open in an app registered to handle this URL pattern

To customize the base URL, see PROJECT_SUMMARY.md → Customization Guide.

### Navigation

- **Home Screen**: Main screen with button grid
- **Menu** (⋮):
  - Edit Buttons / Exit Edit Mode
  - Settings (placeholder screen)
  - About (shows app name and version)
- **Back Button**: Returns to previous screen

### Tips

- **Organize**: Use meaningful titles for easy identification
- **Color Code**: Assign colors by category (e.g., all coffee-related buttons in brown)
- **Size Matters**: Use bigger buttons for frequently used actions
- **Test First**: Add a test button to verify deep link behavior
- **Persistence**: All buttons are automatically saved to local database

### Troubleshooting

#### Gradle Sync Failed
- Check internet connection
- Update Android Studio
- File → Invalidate Caches / Restart

#### App Won't Run
- Ensure device/emulator has API 24+
- Check USB debugging is enabled (physical device)
- Try Clean Project (Build → Clean Project)

#### Deep Links Don't Work
- This is expected! By default, no app handles the example URL
- To make them functional, either:
  - Change the base URL to a real app's deep link scheme
  - Create another app that handles these URLs

#### Buttons Not Saving
- Check logcat for database errors
- Try clearing app data: Settings → Apps → Buttons → Clear Data
- Reinstall the app

### Next Steps

1. Read README.md for feature overview
2. See PROJECT_SUMMARY.md for technical details
3. Customize the deep link URL for your needs
4. Add more buttons and organize them
5. Explore the code to learn Jetpack Compose!

### Support

This is a sample application. For issues or questions:
- Review the PROJECT_SUMMARY.md documentation
- Check the source code comments
- Experiment with modifications

Enjoy building with Compose! 🚀
