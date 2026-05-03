package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.User
import com.example.simpletodolist.data.remote.RetrofitClient

/*
 * Repositorio de autenticación.
 *
 * Capa intermedia entre el ViewModel y la fuente de datos remota (UserApi).
 * Su propósito es centralizar la lógica de acceso a datos:
 *  - Si en el futuro añadimos una caché local (Room), el ViewModel no
 *    tendrá que cambiar; solo se modificará este repositorio.
 *  - Encapsula la "magia" del login simulado (buscar por email + comparar
 *    contraseña) para que el ViewModel solo conozca la operación de alto
 *    nivel: "iniciar sesión".
 *
 * Devuelve Result<User> para que el ViewModel pueda manejar éxito/error
 * de forma explícita y tipada, sin propagar excepciones.
 */
class AuthRepository {

    private val userApi = RetrofitClient.userApi

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val users = userApi.findByEmail(email)
            val user = users.firstOrNull()
                ?: return Result.failure(Exception("Usuario no encontrado"))

            if (user.password != password) {
                return Result.failure(Exception("Contraseña incorrecta"))
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val existing = userApi.findByEmail(email)
            if (existing.isNotEmpty()) {
                return Result.failure(Exception("Este correo ya está registrado"))
            }
            val newUser = User(name = name, email = email, password = password)
            val created = userApi.register(newUser)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
