package com.kuralist.app.core.services

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsService(private val context: Context) {
    
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
    
    // School-related events
    fun trackSchoolViewed(schoolId: Int, schoolName: String) {
        val bundle = Bundle().apply {
            putInt("school_id", schoolId)
            putString("school_name", schoolName)
        }
        firebaseAnalytics.logEvent("school_viewed", bundle)
    }
    
    fun trackSchoolFavorited(schoolId: Int, schoolName: String, isFavorited: Boolean) {
        val bundle = Bundle().apply {
            putInt("school_id", schoolId)
            putString("school_name", schoolName)
            putBoolean("is_favorited", isFavorited)
        }
        firebaseAnalytics.logEvent("school_favorited", bundle)
    }
    
    fun trackSchoolSearch(query: String, resultCount: Int) {
        val bundle = Bundle().apply {
            putString("search_query", query)
            putInt("result_count", resultCount)
        }
        firebaseAnalytics.logEvent("school_search", bundle)
    }
    
    fun trackFilterApplied(filterType: String, filterValue: String) {
        val bundle = Bundle().apply {
            putString("filter_type", filterType)
            putString("filter_value", filterValue)
        }
        firebaseAnalytics.logEvent("filter_applied", bundle)
    }
    
    // Navigation events
    fun trackScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
    
    // User engagement events
    fun trackFavoritesCleared(count: Int) {
        val bundle = Bundle().apply {
            putInt("favorites_count", count)
        }
        firebaseAnalytics.logEvent("favorites_cleared", bundle)
    }
    
    fun trackMapViewed(schoolCount: Int) {
        val bundle = Bundle().apply {
            putInt("visible_schools", schoolCount)
        }
        firebaseAnalytics.logEvent("map_viewed", bundle)
    }
    
    // Authentication events
    fun trackAuthAction(action: String, success: Boolean) {
        val bundle = Bundle().apply {
            putString("auth_action", action)
            putBoolean("success", success)
        }
        firebaseAnalytics.logEvent("auth_action", bundle)
    }
    
    // General purpose event tracking
    fun trackCustomEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        val bundle = Bundle()
        parameters.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
    
    // User properties
    fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }
    
    fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }
} 