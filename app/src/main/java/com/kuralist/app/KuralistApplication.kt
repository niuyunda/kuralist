package com.kuralist.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.analytics
import com.kuralist.app.core.services.SupabaseManager
// import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp
class KuralistApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Firebase Analytics
        Firebase.analytics
        
        // Initialize Supabase client early
        SupabaseManager.client
    }
} 