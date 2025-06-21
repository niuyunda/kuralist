package com.kuralist.app.features.schools.detail.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.AnalyticsService
import com.kuralist.app.core.services.SchoolService
import com.kuralist.app.core.services.database.SchoolDatabase
import com.kuralist.app.features.schools.detail.presentation.components.ErrorState
import com.kuralist.app.features.schools.detail.presentation.components.LoadingState
import com.kuralist.app.features.schools.detail.presentation.components.SchoolDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailScreen(
    schoolId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = rememberSchoolDetailViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(schoolId) {
        viewModel.loadSchool(schoolId)
    }
    
    Scaffold(
        topBar = {
            SchoolDetailTopBar(
                title = uiState.school?.schoolName ?: "School Details",
                onBack = onBack,
                isFavorite = uiState.isFavorite,
                onToggleFavorite = viewModel::toggleFavorite,
                showActions = uiState.school != null
            )
        }
    ) { paddingValues ->
        SchoolDetailContent(
            uiState = uiState,
            onRetry = { 
                viewModel.clearError()
                viewModel.loadSchool(schoolId)
            },
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun rememberSchoolDetailViewModel(): SchoolDetailViewModel {
    val context = LocalContext.current
    return viewModel {
        val database = SchoolDatabase.getDatabase(context)
        val schoolService = SchoolService(database.schoolDao())
        val analyticsService = AnalyticsService(context)
        SchoolDetailViewModel(schoolService, analyticsService)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolDetailTopBar(
    title: String,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    showActions: Boolean,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { 
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            RoundedIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack
            )
        },
        actions = {
            if (showActions) {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onToggle = onToggleFavorite
                )
                ShareButton()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        modifier = modifier
    )
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundedIconButton(
        icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
        onClick = onToggle,
        contentColor = if (isFavorite) 
            MaterialTheme.colorScheme.onErrorContainer 
        else 
            MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun ShareButton(
    modifier: Modifier = Modifier
) {
    RoundedIconButton(
        icon = Icons.Default.Share,
        contentDescription = "Share school",
        onClick = { /* TODO: Implement share */ },
        modifier = modifier
    )
}

@Composable
private fun RoundedIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
private fun SchoolDetailContent(
    uiState: SchoolDetailUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.errorMessage != null -> {
                ErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRetry
                )
            }
            uiState.school != null -> {
                SchoolDetailContent(
                    school = uiState.school,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}