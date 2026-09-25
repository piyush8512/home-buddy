package com.example.buddy.viewmodel

import android.app.Application
import android.app.Activity
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

            val result =
                repository.signInWithGoogle(activity)

            _authState.value = result.fold(

                onSuccess = {
                    AuthState.Success
                },

                onFailure = {
                    AuthState.Error(
                        it.message ?: "Google Sign-In failed"
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