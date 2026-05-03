package com.example.simpletodolist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpletodolist.ui.screens.AddTaskScreen
import com.example.simpletodolist.ui.screens.DetailScreen
import com.example.simpletodolist.ui.screens.HomeScreen

/*
Define la navegación principal de la aplicación utilizando Jetpack Navigation Compose.
Gestiona las transiciones entre HomeScreen, AddTaskScreen y DetailScreen.
Cada pantalla maneja su propio ViewModel de forma independiente.
*/

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("add") {
            AddTaskScreen(navController)
        }

        composable("detail/{title}/{description}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            DetailScreen(navController, title, description)
        }
    }
}