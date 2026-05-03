package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.Section
import com.example.simpletodolist.data.remote.RetrofitClient

/*
 * Repositorio de secciones. Filtra por notebookId en cliente
 * y expone Result<T> para manejo uniforme de errores.
 */
class SectionRepository {

    private val api = RetrofitClient.sectionApi

    suspend fun getSections(notebookId: String): Result<List<Section>> {
        return try {
            val sections = api.getAllSections()
                .filter { it.notebookId == notebookId }
            Result.success(sections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSection(notebookId: String, title: String, color: String): Result<Section> {
        return try {
            val section = Section(notebookId = notebookId, title = title, color = color)
            Result.success(api.createSection(section))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renameSection(id: String, newTitle: String): Result<Section> {
        return try {
            Result.success(api.updateSection(id, mapOf("title" to newTitle)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSection(id: String): Result<Unit> {
        return try {
            api.deleteSection(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}