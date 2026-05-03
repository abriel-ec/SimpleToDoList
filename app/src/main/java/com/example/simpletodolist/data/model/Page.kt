package com.example.simpletodolist.data.model

/*
 * Modelo que representa una página dentro de una sección.
 * Contiene el contenido de texto editable por el usuario
 * y la fecha de última modificación.
 */
data class Page(
    val id: String? = null,
    val sectionId: String,
    val title: String,
    val content: String = "",
    val lastModified: String = ""
)