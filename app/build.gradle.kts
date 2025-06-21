plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // alias(libs.plugins.hilt)
    id("kotlin-kapt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.kuralist.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kuralist.app"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Supported locales
        resConfigs("en", "zh")

        // Build config fields
        buildConfigField("String", "SUPABASE_URL", "\"https://wdtdmzptgfmhzdsmnxnl.supabase.co\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkdGRtenB0Z2ZtaHpkc21ueG5sIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc0Mjk1MTEsImV4cCI6MjA2MzAwNTUxMX0.9ARqDWyHxIqiQkdilJOslPZH9anSCOx15qjMGEX3T1I\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyBiinjIzrXUEy7c7tyzanjneCU0t1EgDFg\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "SUPABASE_URL", "\"https://wdtdmzptgfmhzdsmnxnl.supabase.co\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkdGRtenB0Z2ZtaHpkc21ueG5sIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc0Mjk1MTEsImV4cCI6MjA2MzAwNTUxMX0.9ARqDWyHxIqiQkdilJOslPZH9anSCOx15qjMGEX3T1I\"")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyBiinjIzrXUEy7c7tyzanjneCU0t1EgDFg\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SUPABASE_URL", "\"https://wdtdmzptgfmhzdsmnxnl.supabase.co\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkdGRtenB0Z2ZtaHpkc21ueG5sIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc0Mjk1MTEsImV4cCI6MjA2MzAwNTUxMX0.9ARqDWyHxIqiQkdilJOslPZH9anSCOx15qjMGEX3T1I\"")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyBiinjIzrXUEy7c7tyzanjneCU0t1EgDFg\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM and UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.android)
    
    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    
    // Dependency Injection
    // implementation(libs.hilt.android)
    // implementation(libs.hilt.navigation.compose)
    // kapt(libs.hilt.compiler)
    
    // Image Loading
    implementation(libs.coil.compose)
    
    // Data Store
    implementation(libs.androidx.datastore.preferences)
    
    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}