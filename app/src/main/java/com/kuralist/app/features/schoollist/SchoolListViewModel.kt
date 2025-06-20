package com.kuralist.app.features.schoollist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.managers.SchoolFilterManager
import com.kuralist.app.core.models.FilterSheetItem
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// Removed combine, debounce, distinctUntilChanged, stateIn as they are now in SchoolFilterManager
import kotlinx.coroutines.launch
// import javax.inject.Inject
// import dagger.hilt.android.lifecycle.HiltViewModel

@OptIn(FlowPreview::class)
// @HiltViewModel
class SchoolListViewModel constructor(
    private val schoolService: SchoolService,
    private val schoolFilterManager: SchoolFilterManager // Added
) : ViewModel() {

    // Delegated to SchoolFilterManager
    val searchText: StateFlow<String> = schoolFilterManager.searchText
    val filteredSchools: StateFlow<List<School>> = schoolFilterManager.filteredSchools
    val activeFilters: StateFlow<Map<String, String>> = schoolFilterManager.activeFilters
    val activeUEFilter: StateFlow<Boolean> = schoolFilterManager.activeUEFilter
    val activeInternationalFilter: StateFlow<Boolean> = schoolFilterManager.activeInternationalFilter
    val filterSheetItem: StateFlow<FilterSheetItem?> = schoolFilterManager.filterSheetItem
    val filterCategories: List<String> = schoolFilterManager.filterCategories

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val isLoading: StateFlow<Boolean> = schoolService.isLoading
    val errorMessage: StateFlow<String?> = schoolService.errorMessage

    init {
        loadSchools()
        schoolFilterManager.cacheAllUniqueValues() // Added
    }

    fun updateSearchText(text: String) {
        schoolFilterManager.updateSearchText(text) // Delegated
    }

    // Removed addFilter, removeFilter, clearAllFilters

    // Added methods to interact with SchoolFilterManager
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