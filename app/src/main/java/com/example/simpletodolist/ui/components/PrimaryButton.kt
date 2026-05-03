package com.example.simpletodolist.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/*
 * Botón principal reutilizable.
 *
 * Proporciona un Button con ancho completo y soporte para estado de carga.
 * Cuando isLoading es true, sustituye el texto por un indicador circular,
 * lo que permite que las pantallas (Login y Register) muestren feedback
 * visual sin tener que duplicar lógica.
 *
 * Se utiliza en LoginScreen y RegisterScreen, cumpliendo el requisito
 * de tener al menos dos componentes reutilizables.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text)
        }
    }
}
