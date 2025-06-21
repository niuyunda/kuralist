package com.kuralist.app.features.favorites.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kuralist.app.R
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.AnalyticsService
import com.kuralist.app.core.services.FavoritesManager
import com.kuralist.app.core.services.SchoolService
import com.kuralist.app.core.services.database.SchoolDatabase
import com.kuralist.app.features.schools.list.presentation.components.SchoolListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onSchoolClick: (Int) -> Unit = {},
    viewModel: FavoritesViewModel = run {
        val context = LocalContext.current
        val database = remember { SchoolDatabase.getDatabase(context) }
        val schoolDao = remember { database.schoolDao() }
        val schoolService = remember { SchoolService(schoolDao) }
        val favoritesManager = remember { FavoritesManager(context) }
        val analyticsService = remember { AnalyticsService(context) }
        viewModel { FavoritesViewModel(favoritesManager, schoolService, analyticsService) }
    }
) {
    val favoriteSchools by viewModel.favoriteSchools.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }

    // Track screen view
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val analyticsService = AnalyticsService(context)
        analyticsService.trackScreenView("Favorites")
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar with actions
        TopAppBar(
            title = { 
                Text(stringResource(R.string.favorites)) 
            },
            actions = {
                if (favoriteSchools.isNotEmpty()) {
                    Box {
                        IconButton(onClick = { showDropdownMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.clear_all_favorites)) },
                                onClick = {
                                    showDropdownMenu = false
                                    showClearConfirmDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )

        // Error message
        errorMessage?.let { error ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                }
            }
        }

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.loading_favorites),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            favoriteSchools.isEmpty() -> {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = stringResource(R.string.no_favorite_schools),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(R.string.add_favorites_instruction),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            else -> {
                // Favorites list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // item {
                    //     // Results count
                    //     Text(
                    //         text = "${favoriteSchools.size} favorite ${if (favoriteSchools.size == 1) "school" else "schools"}",
                    //         style = MaterialTheme.typography.bodyMedium,
                    //         color = MaterialTheme.colorScheme.onSurfaceVariant,
                    //         modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    //     )
                    // }
                    
                    items(
                        items = favoriteSchools,
                        key = { it.id }
                    ) { school ->
                        SchoolListItem(
                            school = school,
                            onSchoolClick = { onSchoolClick(it.id) },
                            onFavoriteClick = { viewModel.removeFromFavorites(it) },
                            isFavorite = true // All schools in this list are favorites
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }

    // Clear confirmation dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text(stringResource(R.string.clear_all_favorites)) },
            text = { 
                Text(stringResource(R.string.clear_all_favorites_confirmation)) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text(stringResource(R.string.clear_all))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
} 