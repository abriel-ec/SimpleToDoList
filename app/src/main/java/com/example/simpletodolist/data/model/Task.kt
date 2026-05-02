package com.example.simpletodolist.data.model

/*
Corresponde a la capa de modelo en el patrón MVVM utilizado, permite
definir la estructura de datos de la aplicación, se consideró solo tres campos
id único, título y descripción
 */

data class Task(
    val id: String? = null,
    val title: String,
    val description: String
)