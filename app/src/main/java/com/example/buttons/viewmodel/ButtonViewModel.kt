package com.example.buttons.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buttons.data.AppDatabase
import com.example.buttons.data.ButtonEntity
import com.example.buttons.data.ButtonRepository
import com.example.buttons.data.PageEntity
import com.example.buttons.data.PageRepository
import com.example.buttons.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ButtonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ButtonRepository
    private val pageRepository: PageRepository
    private val preferencesManager = PreferencesManager(application)
    private val _buttons = MutableStateFlow<List<ButtonEntity>>(emptyList())
    val buttons: StateFlow<List<ButtonEntity>> = _buttons.asStateFlow()

    private val _buttonsByPage = MutableStateFlow<Map<Long, List<ButtonEntity>>>(emptyMap())
    val buttonsByPage: StateFlow<Map<Long, List<ButtonEntity>>> = _buttonsByPage.asStateFlow()

    private val _pages = MutableStateFlow<List<PageEntity>>(emptyList())
    val pages: StateFlow<List<PageEntity>> = _pages.asStateFlow()

    private val _currentPageId = MutableStateFlow(1L)
    val currentPageId: StateFlow<Long> = _currentPageId.asStateFlow()

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
        val database = AppDatabase.getDatabase(application)
        val buttonDao = database.buttonDao()
        val pageDao = database.pageDao()
        repository = ButtonRepository(buttonDao)
        pageRepository = PageRepository(pageDao)
        
        viewModelScope.launch {
            pageRepository.allPages.collect { pageList ->
                _pages.value = pageList
                if (pageList.isEmpty()) {
                    val defaultPage = PageEntity(id = 0, name = "Page 1", position = 0)
                    val pageId = pageRepository.insertPage(defaultPage)
                    _currentPageId.value = pageId
                } else if (_currentPageId.value == 1L && pageList.isNotEmpty()) {
                    _currentPageId.value = pageList.first().id
                }
            }
        }

        viewModelScope.launch {
            _currentPageId.collect { pageId ->
                repository.getButtonsByPage(pageId).collect { buttonList ->
                    _buttons.value = buttonList
                }
            }
        }

        viewModelScope.launch {
            repository.allButtons.collect { allButtons ->
                val grouped = allButtons.groupBy { it.pageId }
                _buttonsByPage.value = grouped
            }
        }
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }

    fun setCurrentPage(pageId: Long) {
        _currentPageId.value = pageId
    }

    fun addButton(button: ButtonEntity) {
        viewModelScope.launch {
            val position = _buttons.value.size
            repository.insertButton(button.copy(position = position, pageId = _currentPageId.value))
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

    fun addPage(name: String) {
        viewModelScope.launch {
            val position = _pages.value.size
            val page = PageEntity(id = 0, name = name, position = position)
            val pageId = pageRepository.insertPage(page)
            _currentPageId.value = pageId
        }
    }

    fun updatePage(page: PageEntity) {
        viewModelScope.launch {
            pageRepository.updatePage(page)
        }
    }

    fun deletePage(page: PageEntity) {
        viewModelScope.launch {
            pageRepository.deletePage(page)
            if (_currentPageId.value == page.id) {
                _pages.value.firstOrNull()?.let {
                    _currentPageId.value = it.id
                }
            }
            reorderPages()
        }
    }

    fun movePage(fromIndex: Int, toIndex: Int) {
        val currentPages = _pages.value.toMutableList()
        if (fromIndex in currentPages.indices && toIndex in currentPages.indices) {
            val item = currentPages.removeAt(fromIndex)
            currentPages.add(toIndex, item)
            
            val updatedPages = currentPages.mapIndexed { index, page ->
                page.copy(position = index)
            }
            
            viewModelScope.launch {
                pageRepository.updatePagePositions(updatedPages)
            }
        }
    }

    private fun reorderPages() {
        viewModelScope.launch {
            val updatedPages = _pages.value.mapIndexed { index, page ->
                page.copy(position = index)
            }
            pageRepository.updatePagePositions(updatedPages)
        }
    }
}
