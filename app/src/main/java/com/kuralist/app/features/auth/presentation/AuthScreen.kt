package com.kuralist.app.features.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel
import com.kuralist.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel? = null
) {
    val actualViewModel = viewModel ?: remember { AuthViewModel() }
    
    val email by actualViewModel.email.collectAsState()
    val password by actualViewModel.password.collectAsState()
    val confirmPassword by actualViewModel.confirmPassword.collectAsState()
    val isLoading by actualViewModel.isLoading.collectAsState()
    val authError by actualViewModel.authError.collectAsState()
    val isSignUpMode by actualViewModel.isSignUpMode.collectAsState()
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Convert AuthError to localized string
    val errorMessage = authError?.let { error ->
        when (error) {
            is AuthError.EmailRequired -> stringResource(R.string.email_required)
            is AuthError.EmailInvalid -> stringResource(R.string.email_invalid)
            is AuthError.PasswordRequired -> stringResource(R.string.password_required)
            is AuthError.PasswordTooShort -> stringResource(R.string.password_too_short)
            is AuthError.PasswordsDoNotMatch -> stringResource(R.string.passwords_do_not_match)
            is AuthError.EnterEmailAddress -> stringResource(R.string.enter_email_address)
            is AuthError.EmailVerificationSent -> stringResource(R.string.email_verification_sent)
            is AuthError.PasswordResetSent -> stringResource(R.string.password_reset_sent)
            is AuthError.SignUpFailed -> stringResource(R.string.sign_up_failed, error.message)
            is AuthError.SignInFailed -> stringResource(R.string.sign_in_failed, error.message)
            is AuthError.SignOutFailed -> stringResource(R.string.sign_out_failed, error.message)
            is AuthError.PasswordResetFailed -> stringResource(R.string.password_reset_failed, error.message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = stringResource(R.string.kuralist),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Error Message
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = actualViewModel::updateEmail,
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = actualViewModel::updatePassword,
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Info else Icons.Default.Info,
                        contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true
        )

        // Confirm Password Field (only in sign up mode)
        if (isSignUpMode) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = actualViewModel::updateConfirmPassword,
                label = { Text(stringResource(R.string.confirm_password)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Info else Icons.Default.Info,
                            contentDescription = if (confirmPasswordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Action Button
        Button(
            onClick = { 
                if (isSignUpMode) {
                    actualViewModel.signUp()
                } else {
                    actualViewModel.signIn()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isSignUpMode) stringResource(R.string.sign_up) else stringResource(R.string.login))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle between Sign In and Sign Up
        TextButton(
            onClick = actualViewModel::toggleSignUpMode,
            enabled = !isLoading
        ) {
            Text(
                if (isSignUpMode) 
                    stringResource(R.string.already_have_account)
                else 
                    stringResource(R.string.dont_have_account)
            )
        }

        // Forgot Password (only in sign in mode)
        if (!isSignUpMode) {
            TextButton(
                onClick = actualViewModel::resetPassword,
                enabled = !isLoading && email.isNotBlank()
            ) {
                Text(stringResource(R.string.forgot_password))
            }
        }
    }
} 