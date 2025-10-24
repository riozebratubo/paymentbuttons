package com.example.buttons.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val BUTTON_FONT_SIZE_KEY = floatPreferencesKey("button_font_size")
        private val WALLPAPER_ENABLED_KEY = booleanPreferencesKey("wallpaper_enabled")
        private val BACKGROUND_COLOR_KEY = stringPreferencesKey("background_color")
        const val DEFAULT_BUTTON_FONT_SIZE = 14f
        const val MIN_BUTTON_FONT_SIZE = 6f
        const val MAX_BUTTON_FONT_SIZE = 20f
        const val DEFAULT_BACKGROUND_COLOR = "#FFFFFF"
    }

    val buttonFontSize: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[BUTTON_FONT_SIZE_KEY] ?: DEFAULT_BUTTON_FONT_SIZE
        }

    val wallpaperEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[WALLPAPER_ENABLED_KEY] ?: false
        }

    val backgroundColor: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[BACKGROUND_COLOR_KEY] ?: DEFAULT_BACKGROUND_COLOR
        }

    suspend fun setButtonFontSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[BUTTON_FONT_SIZE_KEY] = size
        }
    }

    suspend fun setWallpaperEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WALLPAPER_ENABLED_KEY] = enabled
        }
    }

    suspend fun setBackgroundColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR_KEY] = color
        }
    }
}
