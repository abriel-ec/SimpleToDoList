package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.TaskList
import retrofit2.http.*

/*
 * Interfaz Retrofit para las listas de tareas.
 * Opera contra el endpoint /lists de JSON Server.
 */
interface TaskListApi {

    @GET("lists")
    suspend fun getAllTaskLists(): List<TaskList>

    @POST("lists")
    suspend fun createTaskList(@Body taskList: TaskList): TaskList

    @PATCH("lists/{id}")
    suspend fun updateTaskList(
        @Path("id") id: String,
        @Body fields: Map<String, String>
    ): TaskList

    @DELETE("lists/{id}")
    suspend fun deleteTaskList(@Path("id") id: String)
}