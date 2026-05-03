package com.example.simpletodolist.data.model

/*
 * Modelo que representa una lista de tareas del usuario.
 * Cada lista tiene un color para identificación visual
 * al estilo Microsoft To Do.
 */
data class TaskList(
    val id: String? = null,
    val userId: String,
    val title: String,
    val color: String? = "#6650A4"
)