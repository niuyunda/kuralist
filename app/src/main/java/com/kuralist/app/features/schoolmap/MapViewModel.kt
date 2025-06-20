package com.kuralist.app.features.schoolmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class MapViewModel(
    private val schoolService: SchoolService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()
    
    private val _selectedSchool = MutableStateFlow<School?>(null)
    val selectedSchool: StateFlow<School?> = _selectedSchool.asStateFlow()
    
    private val _allSchools = MutableStateFlow<List<School>>(emptyList())
    
    // Debounced search with filtering
    val filteredSchools: StateFlow<List<School>> = searchText
        .debounce(300) // Wait 300ms after user stops typing
        .combine(_allSchools) { query, schools ->
            if (query.isBlank()) {
                schools
            } else {
                schools.filter { school ->
                    school.searchableText.contains(query.trim().lowercase())
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun loadSchools() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // First try to load from local database
                val localSchools = schoolService.loadSchoolsFromLocal()
                _allSchools.value = localSchools
                
                // If no local data, sync from server
                if (localSchools.isEmpty()) {
                    schoolService.syncSchoolsFromSupabase()
                    _allSchools.value = schoolService.schools.value
                }
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load schools: ${e.message}"
                )
            }
        }
    }
    
    fun updateSearchText(text: String) {
        _searchText.value = text
    }
    
    fun selectSchool(school: School?) {
        _selectedSchool.value = school
    }
    
    fun refreshSchools() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                schoolService.syncSchoolsFromSupabase()
                _allSchools.value = schoolService.schools.value
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to refresh schools: ${e.message}"
                )
            }
        }
    }
} 