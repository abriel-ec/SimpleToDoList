package com.example.simpletodolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.local.SessionManager
import com.example.simpletodolist.data.model.TaskList
import com.example.simpletodolist.data.repository.TaskListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
 * ViewModel encargado de las listas de tareas del usuario autenticado.
 *
 * Lee del SessionManager el id del usuario activo y solicita al
 * TaskListRepository únicamente sus listas (filtrado por userId),
 * de modo que cada usuario nunca ve las del resto.
 *
 * Expone un StateFlow con todo lo que la UI necesita: lista de
 * listas, estado de carga y posibles mensajes de error.
 */
class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskListRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTaskLists()
    }

    fun loadTaskLists() {
        val userId = sessionManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getTaskLists(userId)
                .onSuccess { taskLists ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        taskLists = taskLists
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar listas"
                    )
                }
        }
    }

    fun createTaskList(title: String) {
        val userId = sessionManager.getUserId() ?: return
        if (title.isBlank()) return

        viewModelScope.launch {
            repository.createTaskList(userId, title.trim())
                .onSuccess { loadTaskLists() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al crear lista"
                    )
                }
        }
    }

    fun renameTaskList(id: String, newTitle: String) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            repository.renameTaskList(id, newTitle.trim())
                .onSuccess { loadTaskLists() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al renombrar"
                    )
                }
        }
    }

    fun deleteTaskList(id: String) {
        viewModelScope.launch {
            repository.deleteTaskList(id)
                .onSuccess { loadTaskLists() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al eliminar"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/*
 * Estado inmutable que HomeScreen observa.
 */
data class TaskListUiState(
    val taskLists: List<TaskList> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)