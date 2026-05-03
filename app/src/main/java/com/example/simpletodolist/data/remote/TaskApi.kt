package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.Task
import retrofit2.http.*

/*
 * Interfaz Retrofit para el CRUD de tareas.
 * Opera contra el endpoint /tasks de JSON Server.
 */
interface TaskApi {

    @GET("tasks")
    suspend fun getAllTasks(): List<Task>

    @POST("tasks")
    suspend fun createTask(@Body task: Task): Task

    @PATCH("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body fields: Map<String, @JvmSuppressWildcards Any>
    ): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String)
}