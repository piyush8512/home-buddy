package com.example.buddy.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buddy.data.auth.GoogleAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        GoogleAuthRepository(application.applicationContext)

    private val _authState =
        MutableStateFlow<AuthState>(
            if (repository.isUserLoggedIn()) {
                AuthState.Success
            } else {
                AuthState.Idle
            }
        )

    val authState: StateFlow<AuthState> =
        _authState.asStateFlow()

    fun signInWithGoogle(activity: Activity) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading

            // 1. Login with Google + Firebase
            val loginResult =
                repository.signInWithGoogle(activity)

            if (loginResult.isFailure) {
                _authState.value = AuthState.Error(
                    loginResult.exceptionOrNull()?.message
                        ?: "Google Sign-In failed"
                )
                return@launch
            }

            // 2. Firebase login successful
            // Get Firebase token and call Spring Boot
            val backendResult =
                repository.getBackendUser()

            _authState.value = backendResult.fold(

                onSuccess = {
                    AuthState.Success
                },

                onFailure = {
                    AuthState.Error(
                        it.message
                            ?: "Backend authentication failed"
                    )
                }
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Idle
    }
}