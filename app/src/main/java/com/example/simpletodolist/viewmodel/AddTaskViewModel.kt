package com.example.simpletodolist.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.model.Task
import com.example.simpletodolist.data.remote.RetrofitClient
import kotlinx.coroutines.launch

/*
ViewModel correspondiente a AddTaskScreen.
Se encarga únicamente de enviar una nueva tarea a la API.
Separa la lógica de negocio de la interfaz de usuario.
*/
class AddTaskViewModel : ViewModel() {

    fun addTask(title: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val newTask = Task(title = title, description = description)
                Log.d("AddTaskVM", "Enviando: $newTask")
                RetrofitClient.api.addTask(newTask)
                onSuccess()
            } catch (e: Exception) {
                Log.e("AddTaskVM", e.toString())
            }
        }
    }
}