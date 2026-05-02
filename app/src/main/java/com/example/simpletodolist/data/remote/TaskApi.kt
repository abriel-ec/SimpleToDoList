package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.Task
import retrofit2.http.*

/*
Define la interfaz que contiene las peticiones HTTP que la aplicación puede realizar
hacia el servidor backend (en este caso JSON Server).

Retrofit utiliza esta interfaz para generar automáticamente la implementación
de las llamadas a la API.

Podemos observar GET, POST y DELETE para obtener la lista de tareas, enviar una nueva tarea
y eliminar una tarea en específico según su ID respectivamente

Se ejecuta dichas operaciones mediante el endpoint tasks, suspend indica que la función se
ejecuta de manera asíncrona.

*/
interface TaskApi {

    @GET("tasks")
    suspend fun getTasks(): List<Task>

    @POST("tasks")
    suspend fun addTask(@Body task: Task): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}