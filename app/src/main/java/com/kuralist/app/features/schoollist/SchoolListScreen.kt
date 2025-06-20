package com.kuralist.app.features.schoollist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kuralist.app.shared.views.SchoolListItem
import com.kuralist.app.shared.views.filterbar.SchoolFilterBar
import com.kuralist.app.shared.views.filterbar.SchoolFilterState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolListScreen(
    modifier: Modifier = Modifier,
    viewModel: SchoolListViewModel,
    onSchoolClick: (Int) -> Unit = {},
    onFavoriteClick: (Int) -> Unit = {}
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val filteredSchools by viewModel.filteredSchools.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    
    // Add filter state
    val filterState: SchoolFilterState = viewModel()
    
    // Bind filter state to school data
    LaunchedEffect(filteredSchools) {
        filterState.bindFiltering(MutableStateFlow(filteredSchools))
    }
    
    // Use filtered schools from filter state instead of viewModel
    val finalFilteredSchools by filterState.filteredSchools.collectAsStateWithLifecycle()
    val filterSearchText by filterState.searchText.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // TopAppBar(
        //     title = { Text("Schools") }
        // )
        
        // Search Bar
        OutlinedTextField(
            value = filterSearchText,
            onValueChange = { 
                viewModel.updateSearchText(it) // Keep existing search
                filterState.updateSearchText(it) // Update filter search too
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search schools...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (filterSearchText.isNotEmpty()) {
                    IconButton(onClick = { 
                        viewModel.updateSearchText("")
                        filterState.updateSearchText("")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true
        )
        
        // Add Filter Bar
        SchoolFilterBar(
            filterState = filterState,
            modifier = Modifier.fillMaxWidth()
        )

        // Error Message
        errorMessage?.let { error ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && finalFilteredSchools.isEmpty() -> {
                    // Initial loading state
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
                                text = "Loading schools...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                finalFilteredSchools.isEmpty() && !isLoading -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = if (filterSearchText.isNotEmpty()) {
                                    "No schools found matching \"$filterSearchText\""
                                } else {
                                    "No schools available"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (filterSearchText.isNotEmpty()) {
                                    "Try adjusting your search terms or filters"
                                } else {
                                    "Pull down to refresh or check your connection"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.refreshSchools() }
                            ) {
                                Text("Refresh")
                            }
                        }
                    }
                }
                else -> {
                    // School list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            // Results count
                            Text(
                                text = "${finalFilteredSchools.size} ${if (finalFilteredSchools.size == 1) "school" else "schools"} found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        
                        items(
                            items = finalFilteredSchools,
                            key = { school -> school.id }
                        ) { school ->
                            SchoolListItem(
                                school = school,
                                onSchoolClick = { onSchoolClick(school.id) },
                                onFavoriteClick = { onFavoriteClick(school.id) },
                                isFavorite = false
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

            // Refresh indicator overlay
            if (isRefreshing) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    shadowElevation = 8.dp,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Refreshing...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
} 