package com.example.simpletodolist.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/*
Este componente es utilizado para el caso de campos de entrada de texto,
permite evitar la duplicación de código y mantener consistencia.

- La función onValueChange es una función que se ejecuta cuando el usuario escribe
permite actualizar el estado.

-
 */

@Composable
fun TaskInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine
    )
}