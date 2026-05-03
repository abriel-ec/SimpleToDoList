package com.example.simpletodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.simpletodolist.ui.navigation.AppNavigation
import com.example.simpletodolist.ui.theme.SimpleToDoListTheme

/*
 * Actividad principal y único punto de entrada de la aplicación.
 *
 * Se encarga de inicializar Jetpack Compose mediante setContent,
 * aplicar el tema global (SimpleToDoListTheme) y delegar todo el
 * flujo de pantallas a AppNavigation, que gestiona la navegación
 * según la arquitectura MVVM.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleToDoListTheme {
                AppNavigation()
            }
        }
    }
}
