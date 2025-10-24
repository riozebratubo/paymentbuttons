package com.example.buttons.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {
    @Query("SELECT * FROM pages ORDER BY position ASC")
    fun getAllPages(): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE id = :id")
    suspend fun getPageById(id: Long): PageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity): Long

    @Update
    suspend fun updatePage(page: PageEntity)

    @Delete
    suspend fun deletePage(page: PageEntity)

    @Transaction
    suspend fun updatePagePositions(pages: List<PageEntity>) {
        pages.forEach { updatePage(it) }
    }
}
