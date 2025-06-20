package com.kuralist.app.shared.views.filterbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.models.School
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FilterSheetItem(
    val category: String,
    val options: List<String>
)

@OptIn(FlowPreview::class)
class SchoolFilterState : ViewModel() {
    
    // Filter categories as per iOS specification
    val filterCategories = listOf("City", "Suburb", "Level", "Authority", "Gender")
    
    // Search text state
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()
    
    // Active category filters (category -> selected value)
    private val _activeFilters = MutableStateFlow<Map<String, String>>(emptyMap())
    val activeFilters: StateFlow<Map<String, String>> = _activeFilters.asStateFlow()
    
    // Toggle filters
    private val _activeUEFilter = MutableStateFlow(false)
    val activeUEFilter: StateFlow<Boolean> = _activeUEFilter.asStateFlow()
    
    private val _activeInternationalFilter = MutableStateFlow(false)
    val activeInternationalFilter: StateFlow<Boolean> = _activeInternationalFilter.asStateFlow()
    
    // Filter sheet state
    private val _filterSheetItem = MutableStateFlow<FilterSheetItem?>(null)
    val filterSheetItem: StateFlow<FilterSheetItem?> = _filterSheetItem.asStateFlow()
    
    // All schools data
    private val _allSchools = MutableStateFlow<List<School>>(emptyList())
    val allSchools: StateFlow<List<School>> = _allSchools.asStateFlow()
    
    // Cached unique values for performance
    private val uniqueValuesCache = mutableMapOf<String, List<String>>()
    
    // Debounced search text
    private val debouncedSearchText = _searchText
        .debounce(300)
        .distinctUntilChanged()
    
    // Filtered schools (reactive combination of all filters)
    val filteredSchools: StateFlow<List<School>> = combine(
        debouncedSearchText,
        _activeFilters,
        _activeUEFilter,
        _activeInternationalFilter,
        _allSchools
    ) { searchText, filters, ueFilter, internationalFilter, schools ->
        filteredSchoolsImpl(searchText, filters, ueFilter, internationalFilter, schools)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun bindFiltering(schoolsFlow: StateFlow<List<School>>) {
        viewModelScope.launch {
            schoolsFlow.collect { schools ->
                _allSchools.value = schools
                cacheAllUniqueValues(schools)
            }
        }
    }
    
    fun updateSearchText(text: String) {
        _searchText.value = text
    }
    
    fun toggleCategoryFilter(category: String) {
        val currentFilters = _activeFilters.value
        if (currentFilters.containsKey(category)) {
            // Remove filter if already active
            _activeFilters.value = currentFilters - category
        } else {
            // Show filter options sheet
            val options = getUniqueValues(category, _allSchools.value)
            _filterSheetItem.value = FilterSheetItem(category, options)
        }
    }
    
    fun selectFilterValue(category: String, value: String) {
        val currentFilters = _activeFilters.value.toMutableMap()
        currentFilters[category] = value
        _activeFilters.value = currentFilters
        dismissFilterSheet()
    }
    
    fun toggleUEFilter() {
        _activeUEFilter.value = !_activeUEFilter.value
    }
    
    fun toggleInternationalFilter() {
        _activeInternationalFilter.value = !_activeInternationalFilter.value
    }
    
    fun dismissFilterSheet() {
        _filterSheetItem.value = null
    }
    
    fun clearAllFilters() {
        _searchText.value = ""
        _activeFilters.value = emptyMap()
        _activeUEFilter.value = false
        _activeInternationalFilter.value = false
    }
    
    private fun filteredSchoolsImpl(
        searchText: String,
        filters: Map<String, String>,
        ueFilter: Boolean,
        internationalFilter: Boolean,
        schools: List<School>
    ): List<School> {
        var result = schools
        
        // Apply text search
        if (searchText.isNotBlank()) {
            result = result.filter { school ->
                school.searchableText.contains(searchText.lowercase())
            }
        }
        
        // Apply category filters
        filters.forEach { (category, value) ->
            result = when (category) {
                "City" -> result.filter { it.townCity?.equals(value, ignoreCase = true) == true }
                "Suburb" -> result.filter { it.suburb?.equals(value, ignoreCase = true) == true }
                "Level" -> result.filter { it.schoolType?.equals(value, ignoreCase = true) == true }
                "Authority" -> result.filter { it.authority?.equals(value, ignoreCase = true) == true }
                "Gender" -> result.filter { it.genderOfStudents?.equals(value, ignoreCase = true) == true }
                else -> result
            }
        }
        
        // Apply UE/NCEA filter (>70% pass rate)
        if (ueFilter) {
            result = result.filter { school ->
                (school.uePassRate2023Year13 ?: 0.0) > 70.0 || 
                (school.nceaPassRate2023Year13 ?: 0.0) > 70.0
            }
        }
        
        // Apply international students filter (>0 international students)
        if (internationalFilter) {
            result = result.filter { (it.internationalStudents ?: 0) > 0 }
        }
        
        return result.sortedBy { it.schoolName }
    }
    
    private fun getUniqueValues(category: String, schools: List<School>): List<String> {
        return uniqueValuesCache[category] ?: run {
            val values = when (category) {
                "City" -> schools.mapNotNull { it.townCity }
                "Suburb" -> schools.mapNotNull { it.suburb }
                "Level" -> schools.mapNotNull { it.schoolType }
                "Authority" -> schools.mapNotNull { it.authority }
                "Gender" -> schools.mapNotNull { it.genderOfStudents }
                else -> emptyList()
            }.distinct()
                .filter { it.isNotBlank() }
                .sorted()
            
            uniqueValuesCache[category] = values
            values
        }
    }
    
    private fun cacheAllUniqueValues(schools: List<School>) {
        uniqueValuesCache.clear()
        filterCategories.forEach { category ->
            getUniqueValues(category, schools)
        }
    }
} 