package com.example.simpletodolist.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.simpletodolist.data.model.Task

import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.simpletodolist.viewmodel.TaskViewModel

/*
Este ViewModel forma parte del patrón MVVM y actúa como intermediario entre la capa de datos (API)
y la interfaz de usuario (Compose).

Su función principal es:
- Gestionar el estado de la lista de tareas.
- Realizar llamadas a la API utilizando Retrofit.
- Ejecutar operaciones en segundo plano mediante corrutinas (viewModelScope).
- Mantener la UI reactiva mediante StateFlow.

Logcat se utiliza para depuración y seguimiento del flujo de datos, fue fundamental
para poder entender qué se envía al servidor y qué responde la API
*/

class TaskViewModel : ViewModel() {

    ////Pruebas con API
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        fetchTasks()
    }

    // Obtener tareas desde API
    fun fetchTasks() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getTasks()
                _tasks.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    //Guardar tarea en API
    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            try {
                val newTask = Task(
                    title = title,
                    description = description
                )

                Log.d("TEST_API", "Enviando: $newTask")

                val response = RetrofitClient.api.addTask(newTask)

                Log.d("TEST_API", "Respuesta API: $response")

                fetchTasks() // refresca lista

            } catch (e: Exception) {
                Log.e("TEST_API", e.toString())
            }
        }
    }
}
