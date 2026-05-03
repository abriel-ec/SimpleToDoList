package com.example.simpletodolist.data.model

/*
 * Modelo de datos que representa un cuaderno (notebook) de un usuario.
 *
 * Cada cuaderno pertenece a un único usuario (relación uno-a-muchos:
 * un User tiene N Notebooks). Esa relación se materializa con el campo
 * userId, que guarda el id del propietario.
 *
 * El campo id es nulo al crear un cuaderno nuevo; JSON Server lo asigna
 * al hacer POST y nos lo devuelve en la respuesta.
 */
data class Notebook(
    val id: String? = null,
    val userId: String,
    val title: String
)
