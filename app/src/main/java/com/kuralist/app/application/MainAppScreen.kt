package com.kuralist.app.application

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
    
    // Shared filter state for both Schools and Map tabs
    val sharedFilterState: com.kuralist.app.shared.views.filterbar.SchoolFilterState = viewModel { com.kuralist.app.shared.views.filterbar.SchoolFilterState() }
    
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
                0 -> SchoolListScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewModel = schoolListViewModel,
                    sharedFilterState = sharedFilterState,
                    favoriteSchoolIds = favoriteSchoolIds,
                    onSchoolClick = { schoolId ->
                        selectedSchoolId = schoolId
                    },
                    onFavoriteClick = { schoolId ->
                        scope.launch {
                            // Find the school by ID to toggle favorite
                            val school = schoolListViewModel.filteredSchools.value.find { it.id == schoolId }
                            school?.let { favoritesManager.toggleFavorite(it) }
                        }
                    }
                )
                1 -> MapScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    sharedFilterState = sharedFilterState,
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

