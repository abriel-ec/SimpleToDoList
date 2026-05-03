package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.Notebook
import com.example.simpletodolist.data.remote.RetrofitClient

/*
 * Repositorio responsable de las operaciones sobre cuadernos.
 *
 * Aísla al ViewModel de los detalles de Retrofit y facilita el manejo
 * uniforme de errores devolviendo Result<T>. Si en el futuro añadimos
 * caché local con Room, todo el cambio quedará confinado a esta clase.
 */
class NotebookRepository {

    private val notebookApi = RetrofitClient.notebookApi

    suspend fun getNotebooks(userId: String): Result<List<Notebook>> {
        return try {
            // El filtrado se hace en el cliente porque JSON Server v1 beta
            // no aplica correctamente el query param ?userId=...
            val notebooks = notebookApi.getAllNotebooks()
                .filter { it.userId == userId }
            Result.success(notebooks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNotebook(userId: String, title: String): Result<Notebook> {
        return try {
            val newNotebook = Notebook(userId = userId, title = title)
            val created = notebookApi.createNotebook(newNotebook)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renameNotebook(id: String, newTitle: String): Result<Notebook> {
        return try {
            val updated = notebookApi.updateNotebook(id, mapOf("title" to newTitle))
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotebook(id: String): Result<Unit> {
        return try {
            notebookApi.deleteNotebook(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
