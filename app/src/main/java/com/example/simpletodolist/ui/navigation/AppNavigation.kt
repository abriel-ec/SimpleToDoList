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
import com.example.simpletodolist.ui.screens.PageEditorScreen
import com.example.simpletodolist.ui.screens.PageListScreen
import com.example.simpletodolist.ui.screens.RegisterScreen
import com.example.simpletodolist.ui.screens.SectionScreen
import com.example.simpletodolist.viewmodel.AuthViewModel
import com.example.simpletodolist.viewmodel.PageViewModel

/*
 * Configuración central de navegación de la aplicación.
 *
 * Rutas disponibles:
 *  - "login":                        pantalla de inicio de sesión
 *  - "register":                     pantalla de registro
 *  - "home":                         lista de cuadernos
 *  - "sections/{notebookId}/{title}": secciones de un cuaderno
 *  - "pages/{sectionId}/{title}":    páginas de una sección
 *  - "editor/{pageId}":              editor de una página
 *
 * PageViewModel se comparte entre PageListScreen y PageEditorScreen
 * para mantener la página seleccionada en memoria.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authViewModel: AuthViewModel = viewModel()
    val pageViewModel: PageViewModel = viewModel()

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

        composable("sections/{notebookId}/{title}") { backStackEntry ->
            val notebookId = backStackEntry.arguments?.getString("notebookId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            SectionScreen(
                navController = navController,
                notebookId = notebookId,
                notebookTitle = title
            )
        }

        composable("pages/{sectionId}/{title}") { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            PageListScreen(
                navController = navController,
                sectionId = sectionId,
                sectionTitle = title,
                viewModel = pageViewModel
            )
        }

        composable("editor/{pageId}") {
            PageEditorScreen(
                navController = navController,
                viewModel = pageViewModel
            )
        }
    }
}