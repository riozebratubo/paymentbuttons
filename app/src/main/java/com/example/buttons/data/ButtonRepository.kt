package com.example.buttons.data

import kotlinx.coroutines.flow.Flow

class ButtonRepository(private val buttonDao: ButtonDao) {
    val allButtons: Flow<List<ButtonEntity>> = buttonDao.getAllButtons()

    fun getButtonsByPage(pageId: Long): Flow<List<ButtonEntity>> {
        return buttonDao.getButtonsByPage(pageId)
    }

    suspend fun getButtonById(id: Long): ButtonEntity? {
        return buttonDao.getButtonById(id)
    }

    suspend fun insertButton(button: ButtonEntity): Long {
        return buttonDao.insertButton(button)
    }

    suspend fun updateButton(button: ButtonEntity) {
        buttonDao.updateButton(button)
    }

    suspend fun deleteButton(button: ButtonEntity) {
        buttonDao.deleteButton(button)
    }

    suspend fun deleteButtonById(id: Long) {
        buttonDao.deleteButtonById(id)
    }

    suspend fun updateButtonPositions(buttons: List<ButtonEntity>) {
        buttonDao.updateButtonPositions(buttons)
    }
}
