package com.kuralist.app.features.schoolmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.managers.SchoolFilterManager
import com.kuralist.app.core.models.FilterSheetItem
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.flow.* // FlowPreview, debounce, combine, stateIn are no longer directly used here
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// @OptIn(FlowPreview::class) // FlowPreview not needed if debounce is removed
class MapViewModel(
    private val schoolService: SchoolService,
    private val schoolFilterManager: SchoolFilterManager // Added
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _selectedSchool = MutableStateFlow<School?>(null)
    val selectedSchool: StateFlow<School?> = _selectedSchool.asStateFlow()
    
    // _allSchools is used by SchoolFilterManager via its constructor argument (allSchoolsFlow)
    private val _allSchools = MutableStateFlow<List<School>>(emptyList())
    
    // Delegated to SchoolFilterManager
    val searchText: StateFlow<String> = schoolFilterManager.searchText
    val filteredSchools: StateFlow<List<School>> = schoolFilterManager.filteredSchools
    val activeFilters: StateFlow<Map<String, String>> = schoolFilterManager.activeFilters
    val activeUEFilter: StateFlow<Boolean> = schoolFilterManager.activeUEFilter
    val activeInternationalFilter: StateFlow<Boolean> = schoolFilterManager.activeInternationalFilter
    val filterSheetItem: StateFlow<FilterSheetItem?> = schoolFilterManager.filterSheetItem
    val filterCategories: List<String> = schoolFilterManager.filterCategories

    init {
        loadSchools()
    }
    
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
                    // Assuming schoolService.schools is a StateFlow or similar that gets updated
                    _allSchools.value = schoolService.schools.value
                }
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                // Data is loaded, now cache unique values if we have any
                if (_allSchools.value.isNotEmpty()) {
                    schoolFilterManager.cacheAllUniqueValues()
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load schools: ${e.message}"
                )
            }
        }
    }
    
    fun updateSearchText(text: String) {
        schoolFilterManager.updateSearchText(text) // Delegated
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
                // Data is re-loaded, re-cache unique values if we have any
                if (_allSchools.value.isNotEmpty()) {
                     schoolFilterManager.cacheAllUniqueValues()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to refresh schools: ${e.message}"
                )
            }
        }
    }

    // Methods to delegate to SchoolFilterManager
    fun onSelectCategory(category: String) {
        schoolFilterManager.showFilterOptionsSheet(category)
    }

    fun onDismissFilterSheet() {
        schoolFilterManager.dismissFilterOptionsSheet()
    }

    fun onSetCategoryFilter(category: String, option: String) {
        schoolFilterManager.setCategoryFilter(category, option)
    }

    fun onClearCategoryFilter(category: String) {
        schoolFilterManager.clearCategoryFilter(category)
    }

    fun onToggleUEFilter() {
        schoolFilterManager.toggleUEFilter()
    }

    fun onToggleInternationalFilter() {
        schoolFilterManager.toggleInternationalFilter()
    }
}