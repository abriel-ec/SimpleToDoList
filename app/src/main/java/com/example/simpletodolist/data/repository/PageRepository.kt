package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.Page
import com.example.simpletodolist.data.remote.RetrofitClient

/*
 * Repositorio de páginas. Filtra por sectionId en cliente
 * y expone Result<T> para manejo uniforme de errores.
 */
class PageRepository {

    private val api = RetrofitClient.pageApi

    suspend fun getPages(sectionId: String): Result<List<Page>> {
        return try {
            val pages = api.getAllPages()
                .filter { it.sectionId == sectionId }
            Result.success(pages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPage(sectionId: String, title: String): Result<Page> {
        return try {
            val page = Page(sectionId = sectionId, title = title)
            Result.success(api.createPage(page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePage(id: String, title: String, content: String, lastModified: String): Result<Page> {
        return try {
            val fields = mapOf(
                "title" to title,
                "content" to content,
                "lastModified" to lastModified
            )
            Result.success(api.updatePage(id, fields))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePage(id: String): Result<Unit> {
        return try {
            api.deletePage(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}