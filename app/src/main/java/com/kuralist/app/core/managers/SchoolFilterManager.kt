package com.kuralist.app.core.managers

import android.util.Log
import com.kuralist.app.core.models.FilterSheetItem
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SchoolFilterManager(
    private val schoolService: SchoolService,
    private val scope: CoroutineScope,
    allSchoolsFlow: StateFlow<List<School>>
) {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _activeFilters = MutableStateFlow<Map<String, String>>(emptyMap())
    val activeFilters: StateFlow<Map<String, String>> = _activeFilters.asStateFlow()

    private val _activeUEFilter = MutableStateFlow(false)
    val activeUEFilter: StateFlow<Boolean> = _activeUEFilter.asStateFlow()

    private val _activeInternationalFilter = MutableStateFlow(false)
    val activeInternationalFilter: StateFlow<Boolean> = _activeInternationalFilter.asStateFlow()

    private val _filterSheetItem = MutableStateFlow<FilterSheetItem?>(null)
    val filterSheetItem: StateFlow<FilterSheetItem?> = _filterSheetItem.asStateFlow()

    val filterCategories = listOf("City", "Suburb", "Level", "Authority", "Gender")

    private val uniqueValuesCache = mutableMapOf<String, List<String>>()

    val filteredSchools: StateFlow<List<School>> = combine(
        searchText.debounce(300L),
        activeFilters,
        activeUEFilter,
        activeInternationalFilter,
        allSchoolsFlow
    ) { query, filters, ueActive, internationalActive, schools ->
        filteredSchoolsImpl(schools, query, filters, ueActive, internationalActive)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun filteredSchoolsImpl(
        schools: List<School>,
        query: String,
        filters: Map<String, String>,
        ueFilterActive: Boolean,
        internationalFilterActive: Boolean
    ): List<School> {
        var currentList = schools

        if (query.isNotBlank()) {
            currentList = currentList.filter {
                it.searchableText.contains(query.trim(), ignoreCase = true)
            }
        }

        filters.forEach { (category, value) ->
            currentList = currentList.filter { school ->
                when (category) {
                    "City" -> school.townCity?.equals(value, ignoreCase = true) == true
                    "Suburb" -> school.suburb?.equals(value, ignoreCase = true) == true
                    "Level" -> school.schoolType?.equals(value, ignoreCase = true) == true
                    "Authority" -> school.authority?.equals(value, ignoreCase = true) == true
                    "Gender" -> school.genderOfStudents?.equals(value, ignoreCase = true) == true
                    else -> true
                }
            }
        }

        if (ueFilterActive) {
            currentList = currentList.filter {
                (it.uePassRate2023Year13 ?: 0.0) > 0.70 || (it.nceaPassRate2023Year13 ?: 0.0) > 0.70
            }
        }

        if (internationalFilterActive) {
            currentList = currentList.filter {
                (it.internationalStudents ?: 0) > 0
            }
        }
        return currentList
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun setCategoryFilter(category: String, value: String) {
        _activeFilters.value = _activeFilters.value.toMutableMap().apply {
            this[category] = value
        }
        dismissFilterOptionsSheet()
    }

    fun clearCategoryFilter(category: String) {
        _activeFilters.value = _activeFilters.value.toMutableMap().apply {
            remove(category)
        }
    }

    fun toggleUEFilter() {
        _activeUEFilter.value = !_activeUEFilter.value
    }

    fun toggleInternationalFilter() {
        _activeInternationalFilter.value = !_activeInternationalFilter.value
    }

    fun showFilterOptionsSheet(category: String) { // Removed allSchools parameter
        scope.launch {
            val options = getUniqueValues(category) // Removed allSchools argument
            _filterSheetItem.value = FilterSheetItem(
                categoryName = category,
                options = options,
                selectedOption = _activeFilters.value[category]
            )
        }
    }

    fun dismissFilterOptionsSheet() {
        _filterSheetItem.value = null
    }

    // Removed allSchools parameter
    suspend fun getUniqueValues(category: String): List<String> {
        if (uniqueValuesCache.containsKey(category)) {
            return uniqueValuesCache[category]!!
        }

        val values = when (category) {
            "City" -> schoolService.getAllCities()
            "Suburb" -> schoolService.getAllSuburbs()
            "Level" -> schoolService.getAllSchoolTypes()
            "Authority" -> schoolService.getAllAuthorities()
            "Gender" -> schoolService.getAllGenderTypes()
            else -> {
                Log.w("SchoolFilterManager", "Unknown category for unique values: $category")
                emptyList()
            }
        }

        val processedValues = values.filterNot { it.isNullOrBlank() }.distinct().sorted()
        uniqueValuesCache[category] = processedValues
        return processedValues
    }

    fun cacheAllUniqueValues() { // Removed allSchools parameter
        filterCategories.forEach { category ->
            scope.launch {
                getUniqueValues(category) // Removed allSchools argument
            }
        }
    }
}
