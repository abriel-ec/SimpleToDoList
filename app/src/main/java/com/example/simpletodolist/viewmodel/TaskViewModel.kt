package com.example.simpletodolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.model.Task
import com.example.simpletodolist.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
 * ViewModel para TaskScreen.
 * Gestiona las tareas de una lista específica,
 * incluyendo creación, completado, importancia y eliminación.
 */
class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun loadTasks(listId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getTasks(listId)
                .onSuccess { tasks ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tasks = tasks.filter { !it.isCompleted },
                        completedTasks = tasks.filter { it.isCompleted }
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
        }
    }

    fun createTask(listId: String, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.createTask(listId, title.trim())
                .onSuccess { loadTasks(listId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
        }
    }

    fun toggleComplete(task: Task, listId: String) {
        viewModelScope.launch {
            repository.toggleComplete(task)
                .onSuccess { loadTasks(listId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
        }
    }

    fun toggleImportant(task: Task, listId: String) {
        viewModelScope.launch {
            repository.toggleImportant(task)
                .onSuccess { loadTasks(listId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
        }
    }

    fun deleteTask(id: String, listId: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
                .onSuccess { loadTasks(listId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
        }
    }

    fun selectTask(task: Task) {
        _uiState.value = _uiState.value.copy(selectedTask = task)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/*
 * Estado inmutable que TaskScreen observa.
 * Separa tareas pendientes de completadas.
 */
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val selectedTask: Task? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)