package com.kuralist.app.core.models

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class User(
    val id: String,
    var email: String,
    var displayName: String,
    var profileImageURL: String? = null,
    var subscriptionTier: SubscriptionTier,
    var favoriteSchools: List<String> = emptyList(), // School IDs
    var searchHistory: List<String> = emptyList(), // School IDs
    var preferences: UserPreferences,
    val createdAt: String,
    var lastLoginAt: String,
    var updatedAt: String
)

@Serializable
data class UserPreferences(
    var preferredRegions: List<String> = emptyList(),
    var preferredSchoolTypes: List<String> = emptyList(),
    var notificationsEnabled: Boolean = true,
    var darkModeEnabled: Boolean = false
)

enum class SubscriptionTier {
    FREE, PREMIUM
}

enum class Feature {
    BASIC_SCHOOL_INFO, 
    SEARCH, 
    FAVORITES, 
    MAP_VIEW,
    DETAILED_STATS, 
    DATA_EXPORT, 
    SCHOOL_COMPARISON,
    AI_ASSISTANT, 
    OFFLINE_ACCESS
} 