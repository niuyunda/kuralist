package com.kuralist.app.core.services

import com.google.android.gms.maps.model.LatLng
import com.kuralist.app.core.models.School
import com.kuralist.app.core.services.database.SchoolDao
import com.kuralist.app.core.services.SupabaseManager
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// import javax.inject.Inject
// import javax.inject.Singleton

// @Singleton
class SchoolService constructor(
    private val schoolDao: SchoolDao
) {
    private val supabaseClient = SupabaseManager.client

    private val _schools = MutableStateFlow<List<School>>(emptyList())
    val schools: StateFlow<List<School>> = _schools.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Load schools from local database
    suspend fun loadSchoolsFromLocal(): List<School> {
        return try {
            val schools = schoolDao.getAllSchools()
            _schools.value = schools
            schools
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load schools from database: ${e.message}"
            emptyList()
        }
    }

    // Sync schools from Supabase with pagination
    suspend fun syncSchoolsFromSupabase() {
        _isLoading.value = true
        _errorMessage.value = null
        
        try {
            val allSchools = mutableListOf<School>()
            var offset = 0L
            val batchSize = 1000L
            
            // Fetch data in batches until no more data is available
            do {
                val batch = supabaseClient
                    .from("schools")
                    .select {
                        range(offset until (offset + batchSize))
                    }
                    .decodeList<School>()
                
                allSchools.addAll(batch)
                offset += batchSize
                
                // Continue if we got a full batch (meaning there might be more data)
            } while (batch.size == batchSize.toInt())
            
            // Save to local database
            schoolDao.deleteAllSchools()
            schoolDao.insertSchools(allSchools)
            
            _schools.value = allSchools
            
        } catch (e: Exception) {
            _errorMessage.value = "Failed to sync schools from server: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    // Check if data needs updating (daily check)
    suspend fun checkAndUpdateSchoolsIfNeeded() {
        val localUpdateTime = schoolDao.getLatestUpdateTime()
        
        // For now, sync if no data exists or implement timestamp comparison logic
        if (_schools.value.isEmpty()) {
            loadSchoolsFromLocal()
            if (_schools.value.isEmpty()) {
                syncSchoolsFromSupabase()
            }
        }
    }

    // Search schools by text
    suspend fun searchSchools(query: String): List<School> {
        return try {
            if (query.isBlank()) {
                schoolDao.getAllSchools()
            } else {
                schoolDao.searchSchools(query)
            }
        } catch (e: Exception) {
            _errorMessage.value = "Failed to search schools: ${e.message}"
            emptyList()
        }
    }

    // Filter schools by criteria
    suspend fun filterSchools(
        region: String? = null,
        schoolType: String? = null,
        hasBoarding: Boolean? = null,
        minUEPassRate: Double? = null
    ): List<School> {
        return try {
            schoolDao.filterSchools(region, schoolType, hasBoarding, minUEPassRate)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to filter schools: ${e.message}"
            emptyList()
        }
    }

    // Get schools near location
    suspend fun getSchoolsNearby(
        location: LatLng,
        radiusKm: Double
    ): List<School> {
        return try {
            // Calculate bounding box for rough filtering
            val latDelta = radiusKm / 111.0 // Rough conversion: 1 degree latitude ≈ 111 km
            val lngDelta = radiusKm / (111.0 * kotlin.math.cos(kotlin.math.PI * location.latitude / 180.0))
            
            schoolDao.getSchoolsInBounds(
                location.latitude - latDelta,
                location.latitude + latDelta,
                location.longitude - lngDelta,
                location.longitude + lngDelta
            )
        } catch (e: Exception) {
            _errorMessage.value = "Failed to get nearby schools: ${e.message}"
            emptyList()
        }
    }

    // Fetch single school by ID
    suspend fun fetchSchoolById(id: Int): School? {
        return try {
            schoolDao.getSchoolById(id)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to fetch school: ${e.message}"
            null
        }
    }

    // Get filter options
    suspend fun getAllCities(): List<String> = schoolDao.getAllCities()
    suspend fun getAllSuburbs(): List<String> = schoolDao.getAllSuburbs()
    suspend fun getAllSchoolTypes(): List<String> = schoolDao.getAllSchoolTypes()
    suspend fun getAllAuthorities(): List<String> = schoolDao.getAllAuthorities()
    suspend fun getAllGenderTypes(): List<String> = schoolDao.getAllGenderTypes()
} 