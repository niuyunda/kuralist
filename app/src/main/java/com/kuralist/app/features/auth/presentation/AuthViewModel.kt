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

// Error codes for localization
sealed class AuthError {
    object EmailRequired : AuthError()
    object EmailInvalid : AuthError()
    object PasswordRequired : AuthError()
    object PasswordTooShort : AuthError()
    object PasswordsDoNotMatch : AuthError()
    object EnterEmailAddress : AuthError()
    object EmailVerificationSent : AuthError()
    object PasswordResetSent : AuthError()
    data class SignUpFailed(val message: String) : AuthError()
    data class SignInFailed(val message: String) : AuthError()
    data class SignOutFailed(val message: String) : AuthError()
    data class PasswordResetFailed(val message: String) : AuthError()
}

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

    private val _authError = MutableStateFlow<AuthError?>(null)
    val authError: StateFlow<AuthError?> = _authError.asStateFlow()

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
            _authError.value = null
            
            Log.d(TAG, "Attempting sign up for email: ${_email.value}")
            
            try {
                SupabaseManager.client.auth.signUpWith(Email) {
                    email = _email.value
                    password = _password.value
                }
                Log.d(TAG, "Sign up successful")
                // Successful sign up - user needs to verify email
                _authError.value = AuthError.EmailVerificationSent
            } catch (e: Exception) {
                Log.e(TAG, "Sign up failed", e)
                _authError.value = AuthError.SignUpFailed(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn() {
        if (!validateSignInForm()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            
            Log.d(TAG, "Attempting sign in for email: ${_email.value}")
            
            try {
                SupabaseManager.client.auth.signInWith(Email) {
                    email = _email.value
                    password = _password.value
                }
                Log.d(TAG, "Sign in successful")
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
                _authError.value = AuthError.SignInFailed(e.message ?: "Unknown error")
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
                _authError.value = AuthError.SignOutFailed(e.message ?: "Unknown error")
            }
        }
    }

    fun resetPassword() {
        if (_email.value.isBlank()) {
            _authError.value = AuthError.EnterEmailAddress
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            
            Log.d(TAG, "Attempting password reset for email: ${_email.value}")
            
            try {
                SupabaseManager.client.auth.resetPasswordForEmail(_email.value)
                Log.d(TAG, "Password reset email sent")
                _authError.value = AuthError.PasswordResetSent
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send reset email", e)
                _authError.value = AuthError.PasswordResetFailed(e.message ?: "Unknown error")
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
                _authError.value = AuthError.EmailRequired
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> {
                _authError.value = AuthError.EmailInvalid
                return false
            }
            _password.value.isBlank() -> {
                _authError.value = AuthError.PasswordRequired
                return false
            }
        }
        return true
    }

    private fun validateSignUpForm(): Boolean {
        when {
            _email.value.isBlank() -> {
                _authError.value = AuthError.EmailRequired
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> {
                _authError.value = AuthError.EmailInvalid
                return false
            }
            _password.value.isBlank() -> {
                _authError.value = AuthError.PasswordRequired
                return false
            }
            _password.value.length < 6 -> {
                _authError.value = AuthError.PasswordTooShort
                return false
            }
            _password.value != _confirmPassword.value -> {
                _authError.value = AuthError.PasswordsDoNotMatch
                return false
            }
        }
        return true
    }

    private fun clearError() {
        _authError.value = null
    }

    private fun clearForm() {
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
    }
} 