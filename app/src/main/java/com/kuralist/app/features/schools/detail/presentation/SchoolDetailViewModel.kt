package com.kuralist.app.features.schools.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.AnalyticsService
import com.kuralist.app.core.services.FavoritesManager
import com.kuralist.app.core.services.SchoolService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolDetailUiState(
    val school: School? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false
)

class SchoolDetailViewModel(
    private val schoolService: SchoolService,
    private val favoritesManager: FavoritesManager,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SchoolDetailUiState())
    val uiState: StateFlow<SchoolDetailUiState> = _uiState.asStateFlow()
    
    fun loadSchool(schoolId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val schoolData = schoolService.fetchSchoolById(schoolId)
                val isFavorite = schoolData?.let { favoritesManager.isFavorite(it) } ?: false
                
                _uiState.value = _uiState.value.copy(
                    school = schoolData,
                    isLoading = false,
                    isFavorite = isFavorite
                )
                
                // Track school view
                schoolData?.let { school ->
                    analyticsService.trackSchoolViewed(
                        schoolId = school.id,
                        schoolName = school.schoolName
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load school details: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun toggleFavorite() {
        val currentSchool = _uiState.value.school ?: return
        viewModelScope.launch {
            try {
                val wasFavorite = _uiState.value.isFavorite
                favoritesManager.toggleFavorite(currentSchool)
                val newFavoriteState = !wasFavorite
                
                _uiState.value = _uiState.value.copy(
                    isFavorite = newFavoriteState
                )
                
                // Track favorite action
                analyticsService.trackSchoolFavorited(
                    schoolId = currentSchool.id,
                    schoolName = currentSchool.schoolName,
                    isFavorited = newFavoriteState
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update favorites: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}