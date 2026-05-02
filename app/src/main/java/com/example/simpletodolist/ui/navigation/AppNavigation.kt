package com.example.simpletodolist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpletodolist.ui.screens.AddTaskScreen
import com.example.simpletodolist.ui.screens.DetailScreen
import com.example.simpletodolist.ui.screens.HomeScreen
import com.example.simpletodolist.viewmodel.TaskViewModel

/*
Este archivo define la navegación principal de la aplicación utilizando Jetpack Navigation Compose.

Se encarga de gestionar las distintas pantallas (Home, AddTask y Detail) y permitir la transición entre ellas
mediante rutas (strings). También inicializa el NavController, que controla el flujo de navegación,
y el ViewModel compartido, que permite mantener y compartir el estado de las tareas entre pantallas.
*/

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController, viewModel)
        }

        composable("add") {
            AddTaskScreen(navController, viewModel)
        }
        composable(
            "detail/{title}/{description}"
        ) { backStackEntry ->

            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""

            DetailScreen(navController, title, description)
        }
    }
}