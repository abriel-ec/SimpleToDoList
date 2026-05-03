package com.example.simpletodolist.viewmodel

import androidx.lifecycle.ViewModel

/*
ViewModel correspondiente a DetailScreen.
Actualmente gestiona los datos de la tarea seleccionada
recibidos por parámetros de navegación.
Preparado para futuras operaciones sobre el detalle.
*/
class DetailViewModel : ViewModel() {

    fun formatTitle(title: String): String = title.replaceFirstChar { it.uppercase() }

    fun formatDescription(description: String): String =
        description.ifBlank { "Sin descripción" }
}