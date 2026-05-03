package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.TaskList
import com.example.simpletodolist.data.remote.RetrofitClient

/*
 * Repositorio responsable de las operaciones sobre listas de tareas.
 * Aísla al ViewModel de los detalles de Retrofit y facilita el manejo
 * uniforme de errores devolviendo Result<T>.
 */
class TaskListRepository {

    private val api = RetrofitClient.taskListApi

    suspend fun getTaskLists(userId: String): Result<List<TaskList>> {
        return try {
            val taskLists = api.getAllTaskLists()
                .filter { it.userId == userId }
            Result.success(taskLists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTaskList(userId: String, title: String): Result<TaskList> {
        return try {
            val newTaskList = TaskList(userId = userId, title = title)
            Result.success(api.createTaskList(newTaskList))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renameTaskList(id: String, newTitle: String): Result<TaskList> {
        return try {
            Result.success(api.updateTaskList(id, mapOf("title" to newTitle)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTaskList(id: String): Result<Unit> {
        return try {
            api.deleteTaskList(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}