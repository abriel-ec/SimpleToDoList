package com.example.simpletodolist.data.model

/*
 * Modelo de datos que representa a un usuario dentro de la aplicación.
 *
 * Forma parte de la capa "data/model" en la arquitectura MVVM.
 * El campo id puede ser nulo cuando creamos un usuario nuevo, ya que
 * JSON Server lo genera automáticamente al hacer POST.
 *
 * NOTA EDUCATIVA: la contraseña se guarda en texto plano porque estamos
 * usando JSON Server con fines didácticos. En una app real SIEMPRE se
 * debe almacenar un hash (por ejemplo BCrypt) y nunca la contraseña original.
 */
data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val password: String
)
