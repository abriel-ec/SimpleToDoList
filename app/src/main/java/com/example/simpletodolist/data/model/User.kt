package com.example.simpletodolist.data.model

/*
 * Modelo que representa un usuario de la aplicación.
 * Contiene los datos necesarios para autenticación
 * y personalización de la experiencia.
 */
data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val password: String
)