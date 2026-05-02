package com.example.simpletodolist.ui.components
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simpletodolist.data.model.Task

/*
Componente que permite representar visualmente una tarea individual dentro de la lista,
papra su funcionamiento recibe un objeto "Task" que contiene los datos a mostrar de título
y descripción y hace uso del callback onClick que se activa al tocar una tarjeta para llevarlo
a la siguiente pantalla, evita renderizar múltiples tareas en la lista.
 */

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = task.title)

            Text(text = task.description)
        }
    }
}