package com.example.simpletodolist.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simpletodolist.R
import com.example.simpletodolist.ui.components.TaskItem
import com.example.simpletodolist.viewmodel.HomeViewModel

/*
Esta es la pantalla principal de la aplicación (HomeScreen).

Su función es mostrar la lista de tareas almacenadas y permitir la navegación a otras pantallas:
- Agregar nuevas tareas (AddTaskScreen)
- Ver el detalle de una tarea (DetailScreen)

Aquí se aplica el patrón MVVM:
- El ViewModel proporciona el estado de las tareas.
- La UI solo observa y representa esos datos.
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.home_title)) }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally  // ← necesario para centrar la imagen
        ) {

            // 👇 La imagen va aquí, antes del botón
            Image(
                painter = painterResource(id = R.drawable.ic_tasks),
                contentDescription = stringResource(R.string.app_logo_desc),
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 12.dp)
            )

            Button(
                onClick = { navController.navigate("add") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_add_task))
            }

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(task = task) {
                        navController.navigate("detail/${task.title}/${task.description}")
                    }
                }
            }
        }
    }
}