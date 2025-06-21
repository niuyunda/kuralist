package com.kuralist.app.shared.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun PermissionHandler(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    
    // Check if permission is already granted
    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PermissionChecker.PERMISSION_GRANTED
    
    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
} 