package com.kuralist.app.features.schoolmap

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import com.kuralist.app.shared.components.PermissionHandler
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
    onSchoolClick: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Manual dependency injection
    val database = remember { SchoolDatabase.getDatabase(context) }
    val schoolDao = remember { database.schoolDao() }
    val schoolService = remember { SchoolService(schoolDao) }
    val mapViewModel: MapViewModel = viewModel { MapViewModel(schoolService) }
    
    // Collect state
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val searchText by mapViewModel.searchText.collectAsStateWithLifecycle()
    val filteredSchools by mapViewModel.filteredSchools.collectAsStateWithLifecycle()
    val selectedSchool by mapViewModel.selectedSchool.collectAsStateWithLifecycle()
    
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
            // School markers (without clustering for now)
            filteredSchools.filter { it.coordinates != null }.take(50).forEach { school ->
                SchoolMarker(
                    school = school,
                    isSelected = selectedSchool?.id == school.id,
                    onClick = { mapViewModel.selectSchool(school) }
                )
            }
        }
        
        // Search bar overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = mapViewModel::updateSearchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                placeholder = { Text("Search schools on map...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { mapViewModel.updateSearchText("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
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
        if (filteredSchools.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "${filteredSchools.count { it.coordinates != null }} schools with locations",
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
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
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
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
                    .padding(top = 80.dp),
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
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
            SchoolInfoBottomSheet(
                school = school,
                onDismiss = { mapViewModel.selectSchool(null) },
                onNavigateToDetail = { schoolId ->
                    onSchoolClick?.invoke(schoolId)
                }
            )
        }
    }
}

@Composable
private fun SchoolMarker(
    school: School,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    school.coordinates?.let { coordinates ->
        Marker(
            state = MarkerState(position = coordinates),
            title = school.schoolName,
            snippet = "${school.location} • ${school.schoolType ?: "School"}",
            onClick = {
                onClick()
                true // Consume the click
            },
            icon = getMarkerIcon(school.schoolType, isSelected)
        )
    }
}

@Composable
private fun SchoolInfoBottomSheet(
    school: School,
    onDismiss: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // School name
            Text(
                text = school.schoolName,
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Location and type
            Text(
                text = "${school.location} • ${school.schoolType ?: "School"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (school.totalSchoolRoll != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Roll: ${school.totalSchoolRoll} students",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close")
                }
                
                Button(
                    onClick = { onNavigateToDetail(school.id) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Details")
                }
            }
        }
    }
}

private fun getMarkerIcon(schoolType: String?, isSelected: Boolean): BitmapDescriptor {
    return when {
        isSelected -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        schoolType?.contains("Primary", ignoreCase = true) == true -> 
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        schoolType?.contains("Secondary", ignoreCase = true) == true -> 
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        schoolType?.contains("Composite", ignoreCase = true) == true -> 
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    }
} 