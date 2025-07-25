package com.kuralist.app.features.schools.list.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kuralist.app.R
import com.kuralist.app.features.schools.list.presentation.components.SchoolListItem
import com.kuralist.app.features.schools.list.presentation.components.search.UnifiedSearchAndFilterBar
import com.kuralist.app.shared.ui.components.filter.SchoolFilterState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolListScreen(
    modifier: Modifier = Modifier,
    viewModel: SchoolListViewModel,
    sharedFilterState: SchoolFilterState? = null,
    favoriteSchoolIds: Set<String> = emptySet(),
    onSchoolClick: (Int) -> Unit = {},
    onFavoriteClick: (Int) -> Unit = {}
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val filteredSchools by viewModel.filteredSchools.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    
    // Use shared filter state if provided, otherwise create local one
    val filterState: SchoolFilterState = sharedFilterState ?: viewModel { SchoolFilterState() }
    
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
        // Search and Filter Bar
        UnifiedSearchAndFilterBar(
            filterState = filterState,
            searchPlaceholder = stringResource(R.string.search_schools_placeholder),
            isElevated = false,
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
                                text = stringResource(R.string.loading_schools),
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
                                    stringResource(R.string.no_schools_matching_search, filterSearchText)
                                } else {
                                    stringResource(R.string.no_schools_available)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (filterSearchText.isNotEmpty()) {
                                    stringResource(R.string.adjust_search_terms)
                                } else {
                                    stringResource(R.string.pull_to_refresh)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.refreshSchools() }
                            ) {
                                Text(stringResource(R.string.refresh))
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
                            val schoolCount = finalFilteredSchools.size
                            val schoolText = if (schoolCount == 1) {
                                stringResource(R.string.school_singular)
                            } else {
                                stringResource(R.string.schools_plural)
                            }
                            Text(
                                text = "$schoolCount $schoolText ${stringResource(R.string.found)}",
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
                                isFavorite = favoriteSchoolIds.contains(school.id.toString())
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
                            text = stringResource(R.string.refreshing),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
} 