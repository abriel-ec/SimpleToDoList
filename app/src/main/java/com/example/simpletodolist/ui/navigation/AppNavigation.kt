package com.example.simpletodolist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpletodolist.data.local.SessionManager
import com.example.simpletodolist.ui.screens.HomeScreen
import com.example.simpletodolist.ui.screens.LoginScreen
import com.example.simpletodolist.ui.screens.RegisterScreen
import com.example.simpletodolist.ui.screens.TaskScreen
import com.example.simpletodolist.viewmodel.AuthViewModel

/*
 * Configuración central de navegación de la aplicación.
 *
 * Rutas disponibles:
 *  - "login":                  pantalla de inicio de sesión
 *  - "register":               pantalla de registro
 *  - "home":                   lista de listas/cuadernos
 *  - "tasks/{listId}/{title}": tareas de una lista
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authViewModel: AuthViewModel = viewModel()

    val startDestination = if (sessionManager.isLoggedIn()) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }

        composable("register") {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

        composable("home") {
            HomeScreen(navController = navController, viewModel = authViewModel)
        }

        composable("tasks/{listId}/{title}") { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            TaskScreen(
                navController = navController,
                listId = listId,
                listTitle = title,
                listColor = "#6650A4"
            )
        }
    }
}