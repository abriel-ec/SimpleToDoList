package com.example.simpletodolist.data.model

/*
 * Modelo que representa una tarea dentro de una lista.
 * Contiene título, notas opcionales, fecha límite,
 * estado de completado y prioridad visual.
 */
data class Task(
    val id: String? = null,
    val listId: String,
    val title: String,
    val notes: String = "",
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val isImportant: Boolean = false
)