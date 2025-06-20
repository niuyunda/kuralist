package com.kuralist.app.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.FavoritesManager
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesManager: FavoritesManager,
    private val schoolService: SchoolService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Combine favorite IDs with all schools to get favorite schools
    val favoriteSchools: StateFlow<List<School>> = combine(
        favoritesManager.favoriteSchoolIds,
        schoolService.schools
    ) { favoriteIds, allSchools ->
        allSchools.filter { school ->
            favoriteIds.contains(school.id.toString())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteSchoolIds: StateFlow<Set<String>> = favoritesManager.favoriteSchoolIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    init {
        loadSchools()
    }

    private fun loadSchools() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                schoolService.loadSchoolsFromLocal()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load schools: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(school: School) {
        viewModelScope.launch {
            try {
                favoritesManager.toggleFavorite(school)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update favorites: ${e.message}"
            }
        }
    }

    fun removeFromFavorites(school: School) {
        viewModelScope.launch {
            try {
                favoritesManager.removeFromFavorites(school)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove from favorites: ${e.message}"
            }
        }
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            try {
                favoritesManager.clearAllFavorites()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to clear favorites: ${e.message}"
            }
        }
    }

    fun isFavorite(school: School): Boolean {
        return favoriteSchoolIds.value.contains(school.id.toString())
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshFavorites() {
        loadSchools()
    }
} 