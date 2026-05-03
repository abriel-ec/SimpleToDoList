package com.example.simpletodolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.model.Task
import com.example.simpletodolist.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
ViewModel correspondiente a HomeScreen.
Se encarga de obtener la lista de tareas desde la API
y de eliminar tareas. Expone el estado mediante StateFlow.
*/
class HomeViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        fetchTasks()
    }

    fun fetchTasks() {
        viewModelScope.launch {
            try {
                _tasks.value = RetrofitClient.api.getTasks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.deleteTask(id)
                fetchTasks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}