package com.example.buttons.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buttons.data.AppDatabase
import com.example.buttons.data.ButtonEntity
import com.example.buttons.data.ButtonRepository
import com.example.buttons.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ButtonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ButtonRepository
    private val preferencesManager = PreferencesManager(application)
    private val _buttons = MutableStateFlow<List<ButtonEntity>>(emptyList())
    val buttons: StateFlow<List<ButtonEntity>> = _buttons.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    val buttonFontSize: StateFlow<Float> = MutableStateFlow(PreferencesManager.DEFAULT_BUTTON_FONT_SIZE).apply {
        viewModelScope.launch {
            preferencesManager.buttonFontSize.collect { value = it }
        }
    }

    val wallpaperEnabled: StateFlow<Boolean> = MutableStateFlow(false).apply {
        viewModelScope.launch {
            preferencesManager.wallpaperEnabled.collect { value = it }
        }
    }

    val backgroundColor: StateFlow<String> = MutableStateFlow(PreferencesManager.DEFAULT_BACKGROUND_COLOR).apply {
        viewModelScope.launch {
            preferencesManager.backgroundColor.collect { value = it }
        }
    }

    init {
        val buttonDao = AppDatabase.getDatabase(application).buttonDao()
        repository = ButtonRepository(buttonDao)
        
        viewModelScope.launch {
            repository.allButtons.collect { buttonList ->
                _buttons.value = buttonList
            }
        }
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }

    fun addButton(button: ButtonEntity) {
        viewModelScope.launch {
            val position = _buttons.value.size
            repository.insertButton(button.copy(position = position))
        }
    }

    fun updateButton(button: ButtonEntity) {
        viewModelScope.launch {
            repository.updateButton(button)
        }
    }

    fun deleteButton(button: ButtonEntity) {
        viewModelScope.launch {
            repository.deleteButton(button)
            reorderButtons()
        }
    }

    fun moveButton(fromIndex: Int, toIndex: Int) {
        val currentButtons = _buttons.value.toMutableList()
        if (fromIndex in currentButtons.indices && toIndex in currentButtons.indices) {
            val item = currentButtons.removeAt(fromIndex)
            currentButtons.add(toIndex, item)
            
            val updatedButtons = currentButtons.mapIndexed { index, button ->
                button.copy(position = index)
            }
            
            viewModelScope.launch {
                repository.updateButtonPositions(updatedButtons)
            }
        }
    }

    private fun reorderButtons() {
        viewModelScope.launch {
            val updatedButtons = _buttons.value.mapIndexed { index, button ->
                button.copy(position = index)
            }
            repository.updateButtonPositions(updatedButtons)
        }
    }

    fun setButtonFontSize(size: Float) {
        viewModelScope.launch {
            preferencesManager.setButtonFontSize(size)
        }
    }

    fun setWallpaperEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setWallpaperEnabled(enabled)
        }
    }

    fun setBackgroundColor(color: String) {
        viewModelScope.launch {
            preferencesManager.setBackgroundColor(color)
        }
    }
}
