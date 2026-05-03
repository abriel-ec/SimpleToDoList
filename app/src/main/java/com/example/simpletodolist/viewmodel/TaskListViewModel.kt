package com.example.simpletodolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.local.SessionManager
import com.example.simpletodolist.data.model.Notebook
import com.example.simpletodolist.data.repository.NotebookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
 * ViewModel encargado de la lista de cuadernos del usuario autenticado.
 *
 * Lee del SessionManager el id del usuario activo y solicita al
 * NotebookRepository únicamente sus cuadernos (filtrado por userId),
 * de modo que cada usuario nunca ve los del resto.
 *
 * Expone un StateFlow con todo lo que la UI necesita: lista de
 * cuadernos, estado de carga y posibles mensajes de error.
 *
 * Las operaciones de creación, renombrado y borrado refrescan la lista
 * tras finalizar para mantener la UI sincronizada con el servidor.
 */
class NotebookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotebookRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(NotebookUiState())
    val uiState: StateFlow<NotebookUiState> = _uiState.asStateFlow()

    init {
        loadNotebooks()
    }

    fun loadNotebooks() {
        val userId = sessionManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getNotebooks(userId)
                .onSuccess { notebooks ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notebooks = notebooks
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar cuadernos"
                    )
                }
        }
    }

    fun createNotebook(title: String) {
        val userId = sessionManager.getUserId() ?: return
        if (title.isBlank()) return

        viewModelScope.launch {
            repository.createNotebook(userId, title.trim())
                .onSuccess { loadNotebooks() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al crear cuaderno"
                    )
                }
        }
    }

    fun renameNotebook(id: String, newTitle: String) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            repository.renameNotebook(id, newTitle.trim())
                .onSuccess { loadNotebooks() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al renombrar"
                    )
                }
        }
    }

    fun deleteNotebook(id: String) {
        viewModelScope.launch {
            repository.deleteNotebook(id)
                .onSuccess { loadNotebooks() }
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
 * Estado inmutable que la pantalla de cuadernos observa.
 */
data class NotebookUiState(
    val notebooks: List<Notebook> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
