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
import com.example.simpletodolist.viewmodel.AuthViewModel

/*
 * Configuración central de navegación de la aplicación.
 *
 * Define las rutas disponibles y la pantalla inicial:
 *  - "login":    pantalla de inicio de sesión.
 *  - "register": pantalla de registro de nuevos usuarios.
 *  - "home":     pantalla principal una vez autenticado.
 *
 * La pantalla de inicio (startDestination) depende de si existe sesión
 * activa en SessionManager: si la hay, el usuario va directo a "home";
 * en caso contrario, debe iniciar sesión.
 *
 * El AuthViewModel se obtiene una sola vez y se comparte entre
 * LoginScreen y RegisterScreen para que ambas pantallas observen el
 * mismo estado de autenticación.
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
    }
}
