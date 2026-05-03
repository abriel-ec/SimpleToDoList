package com.example.simpletodolist.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions

/*
 * Componente reutilizable de entrada de texto.
 *
 * Encapsula un OutlinedTextField configurado con valores por defecto
 * que se repiten en toda la app (ancho completo, una sola línea, etc.).
 *
 * Soporta dos comportamientos especiales:
 *  - isPassword: oculta el contenido con puntos.
 *  - keyboardType: permite mostrar el teclado adecuado (texto, email, etc.).
 *
 * Se utiliza en LoginScreen y RegisterScreen, cumpliendo el requisito
 * de tener componentes reutilizables.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
