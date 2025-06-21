package com.kuralist.app.features.schools.map.presentation

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.SchoolService
import com.kuralist.app.core.services.database.SchoolDatabase
import com.kuralist.app.shared.ui.components.PermissionHandler
import com.kuralist.app.features.schools.list.presentation.components.search.UnifiedSearchAndFilterBar
import com.kuralist.app.shared.ui.components.filter.SchoolFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// Default location - Wellington, New Zealand
private val WELLINGTON_LOCATION = LatLng(-41.2865, 174.7762)
private val NEW_ZEALAND_BOUNDS = LatLngBounds(
    LatLng(-47.0, 166.0), // Southwest corner
    LatLng(-34.0, 179.0)  // Northeast corner
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    sharedFilterState: SchoolFilterState? = null,
    onSchoolClick: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Manual dependency injection
    val database = remember { SchoolDatabase.getDatabase(context) }
    val schoolDao = remember { database.schoolDao() }
    val schoolService = remember { SchoolService(schoolDao) }
    val mapViewModel: MapViewModel = viewModel { MapViewModel(schoolService) }
    
    // Use shared filter state if provided, otherwise create local one
    val filterState: SchoolFilterState = sharedFilterState ?: viewModel { SchoolFilterState() }
    
    // Collect state
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val filteredSchools by mapViewModel.filteredSchools.collectAsStateWithLifecycle()
    val selectedSchool by mapViewModel.selectedSchool.collectAsStateWithLifecycle()
    
    // Bind filter state to school data
    LaunchedEffect(filteredSchools) {
        filterState.bindFiltering(MutableStateFlow(filteredSchools))
    }
    
    // Use filtered schools from filter state
    val finalFilteredSchools by filterState.filteredSchools.collectAsStateWithLifecycle()
    
    // Map state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(WELLINGTON_LOCATION, 6f)
    }
    
    // Location permissions
    var hasLocationPermission by remember { mutableStateOf(false) }
    
    // Load schools on first composition
    LaunchedEffect(Unit) {
        mapViewModel.loadSchools()
    }
    
    // Request location permission
    PermissionHandler(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionGranted = { hasLocationPermission = true },
        onPermissionDenied = { hasLocationPermission = false }
    )
    
    Box(modifier = modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // We'll create our own
                zoomControlsEnabled = false,
                compassEnabled = true,
                mapToolbarEnabled = false
            ),
            onMapClick = {
                // Deselect school when clicking empty area
                mapViewModel.selectSchool(null)
            }
        ) {
            // School markers (use filtered schools from filter state)
            finalFilteredSchools.filter { it.coordinates != null }.take(50).forEach { school ->
                SchoolMarker(
                    school = school,
                    isSelected = selectedSchool?.id == school.id,
                    onClick = { mapViewModel.selectSchool(school) }
                )
            }
        }
        
        // Search and Filter overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            UnifiedSearchAndFilterBar(
                filterState = filterState,
                searchPlaceholder = "Search schools on map...",
                isElevated = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // My Location button
        if (hasLocationPermission) {
            FloatingActionButton(
                onClick = {
                    // TODO: Get current location and move camera
                    // For now, just center on Wellington
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(WELLINGTON_LOCATION, 10f)
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "My Location",
                    tint = Color.White
                )
            }
        }
        
        // Results count overlay
        if (finalFilteredSchools.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "${finalFilteredSchools.count { it.coordinates != null }} schools with locations",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        // Loading indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading schools...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        // Error message
        uiState.errorMessage?.let { error ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Selected school info
        selectedSchool?.let { school ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                SchoolInfoCard(
                    school = school,
                    onSchoolClick = { onSchoolClick?.invoke(school.id) },
                    onDismiss = { mapViewModel.selectSchool(null) }
                )
            }
        }
    }
}

@Composable
private fun SchoolInfoCard(
    school: School,
    onSchoolClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = school.schoolName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Close"
                    )
                }
            }
            
            if (school.location.isNotEmpty()) {
                Text(
                    text = school.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (school.authority != null) {
                Text(
                    text = school.authority!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onSchoolClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Details")
            }
        }
    }
}

@Composable
private fun SchoolMarker(
    school: School,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val coordinates = school.coordinates ?: return
    
    Marker(
        state = MarkerState(position = coordinates),
        title = school.schoolName,
        snippet = school.location,
        onClick = {
            onClick()
            false // Don't show default info window
        },
        icon = if (isSelected) {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        } else {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
    )
} 