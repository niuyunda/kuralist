package com.kuralist.app.application

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuralist.app.features.authentication.AuthViewModel
import com.kuralist.app.features.favorites.FavoritesScreen
import com.kuralist.app.features.profile.ProfileScreen
import com.kuralist.app.features.schoollist.SchoolListScreen
import com.kuralist.app.features.schoollist.SchoolListViewModel
import com.kuralist.app.features.schoolmap.MapScreen
import com.kuralist.app.core.services.SchoolService
import com.kuralist.app.core.services.FavoritesManager
import com.kuralist.app.core.services.database.SchoolDatabase
import com.kuralist.app.shared.views.SchoolListItem
import kotlinx.coroutines.launch

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel? = null
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedSchoolId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Manual dependency injection since Hilt is disabled
    val database = remember { SchoolDatabase.getDatabase(context) }
    val schoolDao = remember { database.schoolDao() }
    val schoolService = remember { SchoolService(schoolDao) }
    val favoritesManager = remember { FavoritesManager(context) }
    val schoolListViewModel: SchoolListViewModel = viewModel { SchoolListViewModel(schoolService) }
    
    // Observe favorite school IDs
    val favoriteSchoolIds by favoritesManager.favoriteSchoolIds.collectAsStateWithLifecycle(emptySet())
    
    val bottomNavItems = listOf(
        BottomNavItem("Schools", Icons.Default.Home, "schools"),
        BottomNavItem("Map", Icons.Default.LocationOn, "map"),
        BottomNavItem("Favorites", Icons.Default.Favorite, "favorites"),
        BottomNavItem("Profile", Icons.Default.Person, "profile")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            selectedTabIndex = index
                            selectedSchoolId = null // Reset detail view on tab change
                        },
                        icon = { 
                            Icon(
                                imageVector = item.icon, 
                                contentDescription = item.label
                            ) 
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (selectedSchoolId != null) {
            // Show detail screen
            com.kuralist.app.features.schooldetail.SchoolDetailScreen(
                schoolId = selectedSchoolId!!,
                onBack = { selectedSchoolId = null },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            when (selectedTabIndex) {
                0 -> SchoolListScreenWithFavorites(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewModel = schoolListViewModel,
                    favoritesManager = favoritesManager,
                    favoriteSchoolIds = favoriteSchoolIds,
                    onSchoolClick = { schoolId ->
                        selectedSchoolId = schoolId
                    }
                )
                1 -> MapScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onSchoolClick = { schoolId ->
                        selectedSchoolId = schoolId
                    }
                )
                2 -> FavoritesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onSchoolClick = { schoolId ->
                        selectedSchoolId = schoolId
                    }
                )
                3 -> ProfileScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    authViewModel = authViewModel ?: remember { AuthViewModel() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolListScreenWithFavorites(
    modifier: Modifier = Modifier,
    viewModel: SchoolListViewModel,
    favoritesManager: FavoritesManager,
    favoriteSchoolIds: Set<String>,
    onSchoolClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val filteredSchools by viewModel.filteredSchools.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = viewModel::updateSearchText,
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
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchText("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true
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
                isLoading && filteredSchools.isEmpty() -> {
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
                filteredSchools.isEmpty() && !isLoading -> {
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
                                text = if (searchText.isNotEmpty()) {
                                    "No schools found matching \"$searchText\""
                                } else {
                                    "No schools available"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchText.isNotEmpty()) {
                                    "Try adjusting your search terms"
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
                                text = "${filteredSchools.size} ${if (filteredSchools.size == 1) "school" else "schools"} found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        
                        items(
                            items = filteredSchools,
                            key = { school -> school.id }
                        ) { school ->
                            SchoolListItem(
                                school = school,
                                onSchoolClick = { onSchoolClick(school.id) },
                                onFavoriteClick = { 
                                    scope.launch {
                                        favoritesManager.toggleFavorite(school)
                                    }
                                },
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
                            text = "Refreshing...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}