package com.kuralist.app.features.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.services.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// import javax.inject.Inject
// import dagger.hilt.android.lifecycle.HiltViewModel

class AuthViewModel constructor() : ViewModel() {
    
    companion object {
        private const val TAG = "AuthViewModel"
    }
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    init {
        Log.d(TAG, "AuthViewModel initialized")
        
        // Check if Supabase is properly configured
        try {
            val client = SupabaseManager.client
            Log.d(TAG, "Supabase client is accessible: ${client.javaClass.simpleName}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to access Supabase client", e)
        }
        
        listenToAuthState()
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        clearError()
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
        clearError()
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        clearError()
    }

    fun toggleSignUpMode() {
        _isSignUpMode.value = !_isSignUpMode.value
        clearError()
        clearForm()
    }

    fun signUp() {
        if (!validateSignUpForm()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            Log.d(TAG, "Attempting sign up for email: ${_email.value}")
            
            try {
                SupabaseManager.client.auth.signUpWith(Email) {
                    email = _email.value
                    password = _password.value
                }
                Log.d(TAG, "Sign up successful")
                // Successful sign up - user needs to verify email
                _errorMessage.value = "Please check your email to verify your account"
            } catch (e: Exception) {
                Log.e(TAG, "Sign up failed", e)
                _errorMessage.value = "Sign up failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn() {
        if (!validateSignInForm()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            Log.d(TAG, "Attempting sign in for email: ${_email.value}")
            
            try {
                SupabaseManager.client.auth.signInWith(Email) {
                    email = _email.value
                    password = _password.value
                }
                Log.d(TAG, "Sign in successful")
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
                _errorMessage.value = "Sign in failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting sign out")
                SupabaseManager.client.auth.signOut()
                Log.d(TAG, "Sign out successful")
            } catch (e: Exception) {
                Log.e(TAG, "Sign out failed", e)
                _errorMessage.value = "Sign out failed: ${e.message}"
            }
        }
    }

    fun resetPassword() {
        if (_email.value.isBlank()) {
            _errorMessage.value = "Please enter your email address"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            Log.d(TAG, "Attempting password reset for email: ${_email.value}")
            
            try {
                SupabaseManager.client.auth.resetPasswordForEmail(_email.value)
                Log.d(TAG, "Password reset email sent")
                _errorMessage.value = "Password reset email sent"
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send reset email", e)
                _errorMessage.value = "Failed to send reset email: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun listenToAuthState() {
        viewModelScope.launch {
            Log.d(TAG, "Starting to listen to auth state")
            SupabaseManager.client.auth.sessionStatus.collect { status ->
                Log.d(TAG, "Auth status changed: $status")
                _isAuthenticated.value = status is SessionStatus.Authenticated
            }
        }
    }

    private fun validateSignInForm(): Boolean {
        when {
            _email.value.isBlank() -> {
                _errorMessage.value = "Email is required"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> {
                _errorMessage.value = "Please enter a valid email address"
                return false
            }
            _password.value.isBlank() -> {
                _errorMessage.value = "Password is required"
                return false
            }
        }
        return true
    }

    private fun validateSignUpForm(): Boolean {
        when {
            _email.value.isBlank() -> {
                _errorMessage.value = "Email is required"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> {
                _errorMessage.value = "Please enter a valid email address"
                return false
            }
            _password.value.isBlank() -> {
                _errorMessage.value = "Password is required"
                return false
            }
            _password.value.length < 6 -> {
                _errorMessage.value = "Password must be at least 6 characters long"
                return false
            }
            _password.value != _confirmPassword.value -> {
                _errorMessage.value = "Passwords do not match"
                return false
            }
        }
        return true
    }

    private fun clearError() {
        _errorMessage.value = null
    }

    private fun clearForm() {
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
    }
} 