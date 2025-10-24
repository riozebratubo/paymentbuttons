package com.example.buttons.data

import kotlinx.coroutines.flow.Flow

class PageRepository(private val pageDao: PageDao) {
    val allPages: Flow<List<PageEntity>> = pageDao.getAllPages()

    suspend fun insertPage(page: PageEntity): Long {
        return pageDao.insertPage(page)
    }

    suspend fun updatePage(page: PageEntity) {
        pageDao.updatePage(page)
    }

    suspend fun deletePage(page: PageEntity) {
        pageDao.deletePage(page)
    }

    suspend fun updatePagePositions(pages: List<PageEntity>) {
        pageDao.updatePagePositions(pages)
    }

    suspend fun getPageById(id: Long): PageEntity? {
        return pageDao.getPageById(id)
    }
}
