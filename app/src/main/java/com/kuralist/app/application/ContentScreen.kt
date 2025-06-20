package com.kuralist.app.application

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
// import androidx.hilt.navigation.compose.hiltViewModel
import com.kuralist.app.features.authentication.AuthScreen
import com.kuralist.app.features.authentication.AuthViewModel

@Composable
fun ContentScreen() {
    val authViewModel = remember { AuthViewModel() } // hiltViewModel<AuthViewModel>()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated) {
        Log.d("ContentScreen", "Authentication state changed: $isAuthenticated")
    }

    if (isAuthenticated) {
        MainAppScreen(authViewModel = authViewModel)
    } else {
        AuthScreen(authViewModel)
    }
} 