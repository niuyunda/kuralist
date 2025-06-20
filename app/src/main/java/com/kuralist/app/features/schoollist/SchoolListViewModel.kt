package com.kuralist.app.features.schoollist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
// import javax.inject.Inject
// import dagger.hilt.android.lifecycle.HiltViewModel

@OptIn(FlowPreview::class)
// @HiltViewModel
class SchoolListViewModel constructor(
    private val schoolService: SchoolService
) : ViewModel() {
    
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedFilters: StateFlow<Map<String, String>> = _selectedFilters.asStateFlow()

    // Debounced search text
    private val debouncedSearchText = _searchText
        .debounce(300)
        .distinctUntilChanged()

    // Filtered schools based on search and filters
    val filteredSchools: StateFlow<List<School>> = combine(
        debouncedSearchText,
        _selectedFilters,
        schoolService.schools
    ) { searchText, filters, allSchools ->
        var result = allSchools
        
        // Apply text search
        if (searchText.isNotBlank()) {
            result = result.filter { school ->
                school.searchableText.contains(searchText.lowercase())
            }
        }
        
        // Apply filters
        filters.forEach { (filterType, filterValue) ->
            result = when (filterType) {
                "city" -> result.filter { it.townCity == filterValue }
                "suburb" -> result.filter { it.suburb == filterValue }
                "schoolType" -> result.filter { it.schoolType == filterValue }
                "authority" -> result.filter { it.authority == filterValue }
                "gender" -> result.filter { it.genderOfStudents == filterValue }
                "highAchievers" -> result.filter { school ->
                    (school.uePassRate2023AllLeavers ?: 0.0) > 70.0 || 
                    (school.nceaPassRate2023AllLeavers ?: 0.0) > 70.0
                }
                "international" -> result.filter { (it.internationalStudents ?: 0) > 0 }
                "boarding" -> result.filter { it.boardingFacilities == true }
                else -> result
            }
        }
        
        result.sortedBy { it.schoolName }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isLoading: StateFlow<Boolean> = schoolService.isLoading
    val errorMessage: StateFlow<String?> = schoolService.errorMessage

    init {
        loadSchools()
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun addFilter(filterType: String, filterValue: String) {
        val currentFilters = _selectedFilters.value.toMutableMap()
        currentFilters[filterType] = filterValue
        _selectedFilters.value = currentFilters
    }

    fun removeFilter(filterType: String) {
        val currentFilters = _selectedFilters.value.toMutableMap()
        currentFilters.remove(filterType)
        _selectedFilters.value = currentFilters
    }

    fun clearAllFilters() {
        _selectedFilters.value = emptyMap()
    }

    fun refreshSchools() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                schoolService.syncSchoolsFromSupabase()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun loadSchools() {
        viewModelScope.launch {
            schoolService.checkAndUpdateSchoolsIfNeeded()
        }
    }
} 