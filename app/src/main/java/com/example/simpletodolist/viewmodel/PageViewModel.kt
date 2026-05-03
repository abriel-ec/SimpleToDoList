package com.example.simpletodolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.model.Page
import com.example.simpletodolist.data.repository.PageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
 * ViewModel para PageListScreen y PageEditorScreen.
 * Gestiona la lista de páginas de una sección y el contenido
 * de la página actualmente en edición.
 */
class PageViewModel : ViewModel() {

    private val repository = PageRepository()

    private val _uiState = MutableStateFlow(PageUiState())
    val uiState: StateFlow<PageUiState> = _uiState.asStateFlow()

    fun loadPages(sectionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.getPages(sectionId)
                .onSuccess { pages ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        pages = pages
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar páginas"
                    )
                }
        }
    }

    fun createPage(sectionId: String, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.createPage(sectionId, title.trim())
                .onSuccess { loadPages(sectionId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al crear página"
                    )
                }
        }
    }

    fun savePage(page: Page, newTitle: String, newContent: String) {
        val id = page.id ?: return
        val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            repository.savePage(id, newTitle, newContent, now)
                .onSuccess { updated ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        currentPage = updated
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Error al guardar"
                    )
                }
        }
    }

    fun deletePage(id: String, sectionId: String) {
        viewModelScope.launch {
            repository.deletePage(id)
                .onSuccess { loadPages(sectionId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al eliminar página"
                    )
                }
        }
    }

    fun selectPage(page: Page) {
        _uiState.value = _uiState.value.copy(currentPage = page)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/*
 * Estado inmutable que las pantallas de páginas observan.
 */
data class PageUiState(
    val pages: List<Page> = emptyList(),
    val currentPage: Page? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)