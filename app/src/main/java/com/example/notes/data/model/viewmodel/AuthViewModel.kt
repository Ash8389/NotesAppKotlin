package com.example.notes.data.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.repository.SupabaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object PasswordResetEmailSent : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: SupabaseAuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        if (authRepository.isUserLoggedIn()) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess {
                _authState.value = AuthState.Authenticated
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signup(email, password)
            result.onSuccess {
                if (authRepository.isUserLoggedIn()) {
                    _authState.value = AuthState.Authenticated
                } else {
                    // If signup was successful but user is not logged in, it usually means email confirmation is required.
                    // However, if the user ALREADY exists, Supabase might return an error which is caught in onFailure.
                    // If we are here, it means a NEW user was likely created (or a confirmation email sent).
                    _authState.value = AuthState.Authenticated
                }
            }.onFailure {
                // Supabase returns specific error messages. We should pass them to the UI.
                val errorMessage = it.message ?: "Signup failed"
                if (errorMessage.contains("User already registered", ignoreCase = true)) {
                     _authState.value = AuthState.Error("User already registered. Please login.")
                } else {
                     _authState.value = AuthState.Error(errorMessage)
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendPasswordResetEmail(email)
            result.onSuccess {
                _authState.value = AuthState.PasswordResetEmailSent
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Failed to send reset email")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Initial
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.updatePassword(password)
            result.onSuccess {
                _authState.value = AuthState.Authenticated
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Failed to update password")
            }
        }
    }
}
