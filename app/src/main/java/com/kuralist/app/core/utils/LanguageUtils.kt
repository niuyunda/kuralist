package com.kuralist.app.core.utils

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

object LanguageUtils {
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_CHINESE = "zh"
    
    suspend fun saveLanguage(context: Context, languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
    
    fun getLanguage(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: LANGUAGE_ENGLISH
        }
    }
    
    fun getCurrentLanguageDisplayName(context: Context, languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_ENGLISH -> "English"
            LANGUAGE_CHINESE -> "中文"
            else -> "English"
        }
    }
    
    fun updateLanguage(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        return context.createConfigurationContext(configuration)
    }
    
    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            LANGUAGE_ENGLISH to "English",
            LANGUAGE_CHINESE to "中文 (Chinese)"
        )
    }
} 