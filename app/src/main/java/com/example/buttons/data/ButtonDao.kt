package com.example.buttons.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ButtonDao {
    @Query("SELECT * FROM buttons ORDER BY position ASC")
    fun getAllButtons(): Flow<List<ButtonEntity>>

    @Query("SELECT * FROM buttons WHERE id = :id")
    suspend fun getButtonById(id: Long): ButtonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertButton(button: ButtonEntity): Long

    @Update
    suspend fun updateButton(button: ButtonEntity)

    @Delete
    suspend fun deleteButton(button: ButtonEntity)

    @Query("DELETE FROM buttons WHERE id = :id")
    suspend fun deleteButtonById(id: Long)

    @Transaction
    suspend fun updateButtonPositions(buttons: List<ButtonEntity>) {
        buttons.forEach { updateButton(it) }
    }
}
