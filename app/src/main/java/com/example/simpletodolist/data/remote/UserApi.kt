package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/*
 * Interfaz Retrofit para la entidad User.
 *
 * Define las peticiones HTTP disponibles contra el endpoint /users del servidor
 * (JSON Server). Retrofit genera automáticamente la implementación de esta interfaz.
 *
 * - getUsers(): obtiene todos los usuarios (útil para depuración).
 * - findByEmail(): busca un usuario por su correo. JSON Server permite filtrar
 *   con query params sobre cualquier campo del recurso.
 * - register(): crea un nuevo usuario.
 *
 * El modificador suspend indica que las funciones se ejecutan dentro de una
 * corrutina, sin bloquear el hilo principal.
 */
interface UserApi {

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users")
    suspend fun findByEmail(@Query("email") email: String): List<User>

    @POST("users")
    suspend fun register(@Body user: User): User
}
