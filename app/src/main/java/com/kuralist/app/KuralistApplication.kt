package com.kuralist.app

import android.app.Application
import com.kuralist.app.core.services.SupabaseManager
// import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp
class KuralistApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Supabase client early
        SupabaseManager.client
    }
} 