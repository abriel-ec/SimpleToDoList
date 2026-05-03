package com.example.simpletodolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.model.Section
import com.example.simpletodolist.data.repository.SectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
 * ViewModel para SectionScreen.
 * Gestiona las secciones de un cuaderno específico,
 * incluyendo creación, renombrado, eliminación y selección de color.
 */
class SectionViewModel : ViewModel() {

    private val repository = SectionRepository()

    private val _uiState = MutableStateFlow(SectionUiState())
    val uiState: StateFlow<SectionUiState> = _uiState.asStateFlow()

    fun loadSections(notebookId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.getSections(notebookId)
                .onSuccess { sections ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        sections = sections
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar secciones"
                    )
                }
        }
    }

    fun createSection(notebookId: String, title: String, color: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.createSection(notebookId, title.trim(), color)
                .onSuccess { loadSections(notebookId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al crear sección"
                    )
                }
        }
    }

    fun renameSection(id: String, newTitle: String, notebookId: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.renameSection(id, newTitle.trim())
                .onSuccess { loadSections(notebookId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al renombrar"
                    )
                }
        }
    }

    fun deleteSection(id: String, notebookId: String) {
        viewModelScope.launch {
            repository.deleteSection(id)
                .onSuccess { loadSections(notebookId) }
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
 * Estado inmutable que SectionScreen observa.
 */
data class SectionUiState(
    val sections: List<Section> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)