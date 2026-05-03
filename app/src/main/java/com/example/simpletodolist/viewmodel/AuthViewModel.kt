package com.example.simpletodolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.local.SessionManager
import com.example.simpletodolist.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
 * ViewModel responsable de la autenticación (login y registro).
 *
 * Hereda de AndroidViewModel para poder acceder al Context de la aplicación
 * y así inicializar el SessionManager (que necesita SharedPreferences).
 *
 * Expone un único StateFlow (uiState) que las pantallas observan para
 * renderizar el estado actual (cargando, error, autenticado).
 *
 * Las operaciones de red se ejecutan en viewModelScope, una corrutina
 * vinculada al ciclo de vida del ViewModel: si el ViewModel se destruye,
 * la operación se cancela automáticamente.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.login(email.trim(), password)
            result.onSuccess { user ->
                sessionManager.saveSession(user.id ?: "", user.name)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.register(name.trim(), email.trim(), password)
            result.onSuccess { user ->
                sessionManager.saveSession(user.id ?: "", user.name)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = AuthUiState()
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}

/*
 * Estado inmutable de la UI de autenticación.
 *
 * Compose se redibuja automáticamente cada vez que este objeto cambia,
 * gracias al StateFlow del ViewModel.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)
