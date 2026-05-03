package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.Task
import com.example.simpletodolist.data.remote.RetrofitClient

/*
 * Repositorio de tareas. Filtra por listId en cliente
 * y expone Result<T> para manejo uniforme de errores.
 */
class TaskRepository {

    private val api = RetrofitClient.taskApi

    suspend fun getTasks(listId: String): Result<List<Task>> {
        return try {
            val tasks = api.getAllTasks().filter { it.listId == listId }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(listId: String, title: String): Result<Task> {
        return try {
            val task = Task(listId = listId, title = title)
            Result.success(api.createTask(task))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleComplete(task: Task): Result<Task> {
        return try {
            val id = task.id ?: return Result.failure(Exception("ID nulo"))
            val updated = api.updateTask(id, mapOf("isCompleted" to !task.isCompleted))
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleImportant(task: Task): Result<Task> {
        return try {
            val id = task.id ?: return Result.failure(Exception("ID nulo"))
            val updated = api.updateTask(id, mapOf("isImportant" to !task.isImportant))
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val id = task.id ?: return Result.failure(Exception("ID nulo"))
            val fields: Map<String, Any> = mapOf(
                "title" to task.title,
                "notes" to task.notes,
                "dueDate" to task.dueDate,
                "isCompleted" to task.isCompleted,
                "isImportant" to task.isImportant
            )
            Result.success(api.updateTask(id, fields))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            api.deleteTask(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}