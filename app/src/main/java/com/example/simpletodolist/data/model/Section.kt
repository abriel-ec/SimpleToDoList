package com.example.simpletodolist.data.model

/*
 * Modelo que representa una sección dentro de un cuaderno.
 * Cada sección pertenece a un cuaderno (notebookId) y tiene
 * un color asociado para identificación visual.
 */
data class Section(
    val id: String? = null,
    val notebookId: String,
    val title: String,
    val color: String = "#6650A4"
)