package com.kuralist.app.core.services

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import com.kuralist.app.core.config.SupabaseConfig

object SupabaseManager {
    
    private const val TAG = "SupabaseManager"
    
    val client: SupabaseClient by lazy {
        Log.d(TAG, "Initializing Supabase client...")
        Log.d(TAG, "URL: ${SupabaseConfig.SUPABASE_URL}")
        Log.d(TAG, "Key: ${SupabaseConfig.SUPABASE_ANON_KEY.take(20)}...")
        
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.SUPABASE_URL,
            supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }.also {
            Log.d(TAG, "Supabase client initialized successfully")
        }
    }
} 