package com.kuralist.app.core.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kuralist.app.core.models.School
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoritesManager(private val context: Context) {
    private val favoriteSchoolIdsKey = stringSetPreferencesKey("favorite_school_ids")
    
    val favoriteSchoolIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[favoriteSchoolIdsKey] ?: emptySet()
        }
    
    suspend fun isFavorite(school: School): Boolean {
        return context.dataStore.data.map { preferences ->
            val favoriteIds = preferences[favoriteSchoolIdsKey] ?: emptySet()
            favoriteIds.contains(school.id.toString())
        }.let { flow ->
            // Get the first emission from the flow
            var result = false
            flow.collect { result = it }
            result
        }
    }
    
    suspend fun toggleFavorite(school: School) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[favoriteSchoolIdsKey]?.toMutableSet() ?: mutableSetOf()
            val schoolId = school.id.toString()
            
            if (currentFavorites.contains(schoolId)) {
                currentFavorites.remove(schoolId)
            } else {
                currentFavorites.add(schoolId)
            }
            
            preferences[favoriteSchoolIdsKey] = currentFavorites
        }
    }
    
    suspend fun addToFavorites(school: School) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[favoriteSchoolIdsKey]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.add(school.id.toString())
            preferences[favoriteSchoolIdsKey] = currentFavorites
        }
    }
    
    suspend fun removeFromFavorites(school: School) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[favoriteSchoolIdsKey]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.remove(school.id.toString())
            preferences[favoriteSchoolIdsKey] = currentFavorites
        }
    }
    
    fun getFavoriteSchools(allSchools: List<School>): Flow<List<School>> {
        return favoriteSchoolIds.map { favoriteIds ->
            allSchools.filter { school ->
                favoriteIds.contains(school.id.toString())
            }
        }
    }
    
    suspend fun clearAllFavorites() {
        context.dataStore.edit { preferences ->
            preferences[favoriteSchoolIdsKey] = emptySet()
        }
    }
} 