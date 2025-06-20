# Kuralist iOS App - Complete Android Development Guide

## Project Overview

**Kuralist** is a comprehensive school information app for New Zealand schools. The iOS version is built with SwiftUI and follows an MVVM architecture pattern. This document provides complete specifications for creating an identical Android version using modern Android development practices.

## 1. App Architecture & Structure

### 1.1 Overall Architecture

- **Pattern**: MVVM (Model-View-ViewModel)
- **Framework**: iOS uses SwiftUI, Android should use Jetpack Compose
- **Backend**: Supabase (PostgreSQL database with REST API)
- **Local Storage**: Core Data (iOS) → Room Database (Android)
- **State Management**: ObservableObject/@Published (iOS) → StateFlow/LiveData (Android)

### 1.2 Project Structure

```
kuralist/
├── Application/           # App entry point and main navigation
├── Core/                 # Core functionality and services
│   ├── Models/          # Data models
│   ├── Services/        # Business logic and API services
│   ├── Config/          # Configuration settings
│   ├── Utils/           # Utility functions
│   └── Protocols/       # Interfaces (Protocols in iOS)
├── Features/            # Feature modules
│   ├── Authentication/ # User login/signup
│   ├── SchoolList/      # Main school listing
│   ├── SchoolDetail/    # Individual school details
│   ├── SchoolMap/       # Map view with school markers
│   ├── SchoolFilters/   # Filtering functionality
│   ├── SchoolSearchBar/ # Search functionality
│   ├── Favorites/       # User favorites management
│   └── Profile/         # User profile and settings
├── Shared/              # Shared UI components
│   ├── Views/          # Reusable UI components
│   └── Extensions/     # Extension functions
└── Resources/           # Assets, localizations, etc.
```

## 2. Configuration Information

### 2.1 Backend Configuration

```kotlin
// Supabase Configuration
object SupabaseConfig {
    const val SUPABASE_URL = "https://wdtdmzptgfmhzdsmnxnl.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkdGRtenB0Z2ZtaHpkc21ueG5sIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc0Mjk1MTEsImV4cCI6MjA2MzAwNTUxMX0.9ARqDWyHxIqiQkdil"
    const val GOOGLE_MAPS_API_KEY = "AIzaSyBiinjIzrXUEy7c7tyzanjneCU0t1EgDFg"
}
```

### 2.2 Database Configuration

- **Table Name**: `schools`
- **Primary Key**: `school_number` (Int)
- **Records**: ~2500 New Zealand schools
- **Update Mechanism**: Daily check for data updates

## 3. Data Models

### 3.1 Primary School Model

```kotlin
@Entity(tableName = "schools")
data class School(
    @PrimaryKey val id: Int, // maps to school_number in DB
    val schoolName: String,
    val telephone: String?,
    val email: String?,
    val principal: String?,
    val schoolWebsite: String?,
    val street: String?,
    val suburb: String?,
    val townCity: String?,
    val postalAddress: String?,
    val postalAddressSuburb: String?,
    val postalAddressCity: String?,
    val postalCode: Int?,
    val urbanRural: String?,
    val schoolType: String?,
    val definition: String?,
    val authority: String?, // e.g., "State", "Private", "Integrated"
    val donations: String?,
    val genderOfStudents: String?, // "Coeducational", "Boys", "Girls"
    val kmePeakBody: String?,
    val takiwa: String?,
    val territorialAuthority: String?,
    val regionalCouncil: String?,
    val localOffice: String?,
    val educationRegion: String?,
    val generalElectorate: String?,
    val maoriElectorate: String?,
    val neighbourhoodSa2Code: Int?,
    val neighbourhoodSa2Name: String?,
    val ward: String?,
    val colId: String?, // Community of Learning ID
    val colName: String?, // Community of Learning Name
    val latitude: Double?,
    val longitude: Double?,
    val enrolmentScheme: String?,
    val eqi: Int?, // Equity Index
    val totalSchoolRoll: Int?,
    val europeanPakehaStudents: Int?,
    val maoriStudents: Int?,
    val pacificStudents: Int?,
    val asianStudents: Int?,
    val melaaStudents: Int?,
    val otherStudents: Int?,
    val internationalStudents: Int?,
    val isolationIndex: Double?,
    val languageOfInstruction: String?,
    val boardingFacilities: Boolean?,
    val cohortEntry: Boolean?,
    val status: String?,
    val uePassRate2023AllLeavers: Double?,
    val nceaPassRate2023AllLeavers: Double?,
    val uePassRate2023Year13: Double?,
    val nceaPassRate2023Year13: Double?,
    val eqiRank: Double?,
    val eqiDeciles: Int?,
    val europeanPercentage: Double?,
    val maoriPercentage: Double?,
    val pacificPercentage: Double?,
    val asianPercentage: Double?,
    val melaaPercentage: Double?,
    val otherPercentage: Double?,
    val internationalPercentage: Double?,
    val totalUeNcea: Int?,
    val totalUeNceaY13: Int?,
    val createdAt: Date,
    val updatedAt: Date
) {
    // Computed properties
    val location: String get() = listOfNotNull(townCity, suburb).joinToString(", ")

    val coordinates: LatLng? get() =
        if (latitude != null && longitude != null) LatLng(latitude, longitude) else null

    val searchableText: String get() =
        "$schoolName ${suburb ?: ""} ${townCity ?: ""} ${authority ?: ""} ${schoolType ?: ""}".lowercase()
}
```

### 3.2 User Model

```kotlin
data class User(
    val id: String,
    var email: String,
    var displayName: String,
    var profileImageURL: String?,
    var subscriptionTier: SubscriptionTier,
    var favoriteSchools: List<String>, // School IDs
    var searchHistory: List<String>, // School IDs
    var preferences: UserPreferences,
    val createdAt: Date,
    var lastLoginAt: Date,
    var updatedAt: Date
)

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
    BASIC_SCHOOL_INFO, SEARCH, FAVORITES, MAP_VIEW,
    DETAILED_STATS, DATA_EXPORT, SCHOOL_COMPARISON,
    AI_ASSISTANT, OFFLINE_ACCESS
}
```

## 4. Core Services

### 4.1 Supabase Manager

```kotlin
object SupabaseManager {
    private val supabase = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }

    val client: SupabaseClient = supabase
}
```

### 4.2 School Service

```kotlin
class SchoolService {
    private val _schools = MutableStateFlow<List<School>>(emptyList())
    val schools: StateFlow<List<School>> = _schools.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Load schools from local database
    suspend fun loadSchoolsFromLocal(): List<School>

    // Sync schools from Supabase with pagination
    suspend fun syncSchoolsFromSupabase()

    // Check if data needs updating (daily check)
    suspend fun checkAndUpdateSchoolsIfNeeded()

    // Search schools by text
    suspend fun searchSchools(query: String): List<School>

    // Filter schools by criteria
    suspend fun filterSchools(
        region: String? = null,
        schoolType: String? = null,
        hasBoarding: Boolean? = null,
        minUEPassRate: Double? = null
    ): List<School>

    // Get schools near location
    suspend fun getSchoolsNearby(
        location: LatLng,
        radiusKm: Double
    ): List<School>

    // Fetch single school by ID
    suspend fun fetchSchoolById(id: Int): School?
}
```

### 4.3 Authentication Service

```kotlin
class AuthViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    suspend fun signUp(email: String, password: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    suspend fun resetPassword(email: String)
    fun listenToAuthState()
}
```

## 5. Main Features

### 5.1 Authentication Flow

**Screens**: LoginScreen, SignUpScreen
**Navigation**: ContentScreen decides between MainApp or AuthScreens based on auth state

```kotlin
@Composable
fun ContentScreen() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    if (isAuthenticated) {
        MainAppScreen()
    } else {
        LoginScreen()
    }
}
```

### 5.2 Main Tab Navigation

**Tabs**: Schools, Map, Favorites, Profile
**Architecture**: Bottom navigation with 4 main sections

```kotlin
@Composable
fun MainTabScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Schools") },
                    label = { Text("Schools") }
                )
                // ... other tabs
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> SchoolListScreen()
            1 -> MapScreen()
            2 -> FavoritesScreen()
            3 -> ProfileScreen()
        }
    }
}
```

### 5.3 School List Screen

**Components**:

- Search bar with debounced text input
- Filter chips (City, Suburb, Level, Authority, Gender)
- Lazy column of school cards
- Pull-to-refresh functionality

**Key Features**:

- Real-time search with 300ms debounce
- Multiple filter categories
- Infinite scroll/pagination
- Sort by name (alphabetical)

### 5.4 School Detail Screen

**Sections**:

1. Quick Stats (roll, type, decile)
2. Contact Information (phone, email, website)
3. Address & Location
4. Academic Performance (UE/NCEA pass rates)
5. Demographics (ethnicity breakdown)
6. General Information (boarding, language, etc.)
7. Administrative Information (authority, region)
8. Map view (if coordinates available)

**Actions**:

- Add/remove from favorites (with haptic feedback)
- Share school information
- Open in Maps app

### 5.5 Map Screen

**Features**:

- Google Maps integration
- School markers (custom icons)
- Cluster support for dense areas
- Filter integration (same as list view)
- Tap marker to show school details
- Search bar overlay
- Current location support

**Technical**:

- Limit visible markers to 50 for performance
- Only show schools in visible map region
- Custom marker icons based on school type

### 5.6 Filtering System

**Filter Categories**:

- **City**: List of all unique cities
- **Suburb**: List of all unique suburbs
- **Level**: School types (Primary, Secondary, etc.)
- **Authority**: State, Private, Integrated
- **Gender**: Coeducational, Boys, Girls

**Special Filters**:

- **High Achievers**: UE pass rate > 70% OR NCEA pass rate > 70%
- **International Students**: Schools with international students > 0
- **Boarding Schools**: Schools with boarding facilities

**Implementation**:

```kotlin
class SchoolFilterState : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _activeFilters = MutableStateFlow<Map<String, String>>(emptyMap())
    val activeFilters: StateFlow<Map<String, String>> = _activeFilters.asStateFlow()

    private val _filteredSchools = MutableStateFlow<List<School>>(emptyList())
    val filteredSchools: StateFlow<List<School>> = _filteredSchools.asStateFlow()

    // Combine all filter criteria and apply to school list
    private fun applyFilters(schools: List<School>): List<School>
}
```

### 5.7 Favorites Management

**Storage**: SharedPreferences/DataStore
**Features**:

- Add/remove schools from favorites
- Persist across app sessions
- Favorites-only view
- School comparison (up to 3 schools)
- Export favorites list

```kotlin
class FavoritesManager {
    private val _favoriteSchoolIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteSchoolIds: StateFlow<Set<String>> = _favoriteSchoolIds.asStateFlow()

    fun isFavorite(school: School): Boolean
    fun toggleFavorite(school: School)
    fun getFavoriteSchools(allSchools: List<School>): List<School>
}
```

## 6. UI Components & Design

### 6.1 Design System

**Colors**:

- Primary: Blue (#007AFF - iOS system blue)
- Secondary: Gray shades
- Success: Green
- Error: Red
- Background: Dynamic (light/dark mode)

**Typography**:

- Large Title: 34sp
- Title 1: 28sp
- Title 2: 22sp
- Title 3: 20sp
- Headline: 17sp (medium weight)
- Body: 17sp
- Caption: 12sp

### 6.2 Key Reusable Components

**SearchBar**:

```kotlin
@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    placeholder: String = "Search schools"
)
```

**SchoolCard**:

```kotlin
@Composable
fun SchoolCard(
    school: School,
    onSchoolClick: (School) -> Unit,
    onFavoriteClick: (School) -> Unit,
    isFavorite: Boolean
)
```

**FilterChip**:

```kotlin
@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
)
```

**StatView**:

```kotlin
@Composable
fun StatView(
    label: String,
    value: String,
    icon: ImageVector? = null
)
```

## 7. Data Synchronization

### 7.1 Offline-First Architecture

1. **App Launch**: Load from local database immediately
2. **Background Sync**: Check for updates daily
3. **Manual Refresh**: Pull-to-refresh triggers immediate sync
4. **Incremental Updates**: Compare timestamps for efficiency

### 7.2 Sync Strategy

```kotlin
suspend fun checkAndUpdateSchoolsIfNeeded() {
    // 1. Check if we've already checked today
    if (!shouldCheckToday()) return

    // 2. Get latest update timestamp from server
    val serverUpdateTime = fetchServerLatestUpdateTime()
    val localUpdateTime = getLocalUpdateTime()

    // 3. Compare timestamps
    if (serverUpdateTime > localUpdateTime) {
        // 4. Fetch all schools in batches of 1000
        fetchAllSchoolsAndSaveToLocal()
        setLocalUpdateTime(serverUpdateTime)
    }

    setLastCheckToday()
}
```

### 7.3 Database Schema (Room)

```kotlin
@Database(
    entities = [SchoolEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
}

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY schoolName ASC")
    suspend fun getAllSchools(): List<SchoolEntity>

    @Query("SELECT * FROM schools WHERE id = :id")
    suspend fun getSchoolById(id: Int): SchoolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<SchoolEntity>)

    @Query("DELETE FROM schools")
    suspend fun deleteAllSchools()
}
```

## 8. Localization

### 8.1 Supported Languages

- **Primary**: English (en)
- **Secondary**: Chinese Simplified (zh-Hans)

### 8.2 Key Localizable Strings

```xml
<!-- strings.xml -->
<string name="app_name">Kuralist</string>
<string name="welcome">Welcome to Kuralist</string>
<string name="search_schools">Search schools</string>
<string name="schools">Schools</string>
<string name="map">Map</string>
<string name="favorites">Favorites</string>
<string name="profile">Profile</string>
<string name="academic_performance">Academic Performance</string>
<string name="demographics">Demographics</string>
<string name="contact_info">Contact Information</string>
<string name="address">Address</string>
<string name="boarding_facilities">Boarding Facilities</string>
<string name="login">Login</string>
<string name="sign_up">Sign Up</string>
<string name="email">Email</string>
<string name="password">Password</string>
<string name="confirm_password">Confirm Password</string>
<!-- ... many more -->
```

## 9. Performance Considerations

### 9.1 List Performance

- Use LazyColumn with key() for efficient rendering
- Implement item view recycling
- Limit initial load to visible items
- Add loading states and skeleton screens

### 9.2 Map Performance

- Cluster markers when zoomed out
- Limit markers to 50 visible schools
- Use marker pooling for smooth panning
- Implement viewport-based filtering

### 9.3 Memory Management

- Use Paging 3 for large datasets
- Implement proper image caching
- Clear unused view models and states
- Monitor memory usage in large lists

## 10. Testing Strategy

### 10.1 Unit Tests

- Data models and transformations
- Business logic in ViewModels
- Repository layer functions
- Utility functions

### 10.2 Integration Tests

- Database operations
- Network requests
- Authentication flows
- End-to-end user journeys

### 10.3 UI Tests

- Navigation flows
- Search and filter functionality
- Authentication screens
- Critical user paths

## 11. Deployment Configuration

### 11.1 Build Variants

```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("String", "SUPABASE_URL", "\"https://wdtdmzptgfmhzdsmnxnl.supabase.co\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyBiinjIzrXUEy7c7tyzanjneCU0t1EgDFg\"")
        }
        release {
            // Same configuration for production
            minifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### 11.2 Required Permissions

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 12. Dependencies

### 12.1 Core Dependencies

```kotlin
// build.gradle (app)
dependencies {
    // Jetpack Compose
    implementation "androidx.compose.ui:ui:$compose_version"
    implementation "androidx.compose.ui:ui-tooling-preview:$compose_version"
    implementation "androidx.compose.material3:material3:$material3_version"
    implementation "androidx.activity:activity-compose:$activity_compose_version"

    // Navigation
    implementation "androidx.navigation:navigation-compose:$nav_version"

    // ViewModel
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"

    // State Flow
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

    // Room Database
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"

    // Supabase
    implementation "io.github.jan-tennert.supabase:postgrest-kt:$supabase_version"
    implementation "io.github.jan-tennert.supabase:auth-kt:$supabase_version"
    implementation "io.github.jan-tennert.supabase:realtime-kt:$supabase_version"

    // Google Maps
    implementation "com.google.maps.android:maps-compose:$maps_compose_version"
    implementation "com.google.android.gms:play-services-maps:$gms_maps_version"
    implementation "com.google.android.gms:play-services-location:$gms_location_version"

    // Networking
    implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
    implementation "com.squareup.retrofit2:converter-gson:$retrofit_version"
    implementation "com.squareup.okhttp3:logging-interceptor:$okhttp_version"

    // Dependency Injection
    implementation "com.google.dagger:hilt-android:$hilt_version"
    implementation "androidx.hilt:hilt-navigation-compose:$hilt_navigation_compose_version"
    kapt "com.google.dagger:hilt-compiler:$hilt_version"

    // Image Loading
    implementation "io.coil-kt:coil-compose:$coil_version"

    // Data Store
    implementation "androidx.datastore:datastore-preferences:$datastore_version"

    // Paging
    implementation "androidx.paging:paging-runtime:$paging_version"
    implementation "androidx.paging:paging-compose:$paging_version"
}
```

## 13. Development Phases

### Phase 1: Core Infrastructure (Week 1-2)

- Set up project structure and dependencies
- Implement data models and Room database
- Create Supabase manager and basic API calls
- Set up navigation structure

### Phase 2: Authentication (Week 2-3)

- Implement authentication ViewModels
- Create login/signup screens
- Set up auth state management
- Integrate with Supabase Auth

### Phase 3: School List & Search (Week 3-4)

- Create school list screen with search
- Implement filtering system
- Add data synchronization logic
- Create school detail screen

### Phase 4: Map Integration (Week 4-5)

- Integrate Google Maps
- Add school markers and clustering
- Implement map-based filtering
- Add location services

### Phase 5: Favorites & Profile (Week 5-6)

- Implement favorites management
- Create profile screen
- Add school comparison feature
- Implement settings and preferences

### Phase 6: Polish & Testing (Week 6-7)

- Add animations and transitions
- Implement proper error handling
- Write comprehensive tests
- Optimize performance
- Add accessibility features

### Phase 7: Localization & Deployment (Week 7-8)

- Add Chinese localization
- Final testing and bug fixes
- Prepare for Play Store deployment
- Documentation and handover

This document provides a complete blueprint for developing an Android version of the Kuralist iOS app. The Android version should maintain feature parity while following Android-specific design patterns and best practices.

## app database and database columns

id = "school_number" // Swift 'id' property now maps to 'school_number' column
schoolName = "school_name"
telephone
email
principal
schoolWebsite = "school_website"
street
suburb
townCity = "town_city"
postalAddress = "postal_address"
postalAddressSuburb = "postal_address_suburb"
postalAddressCity = "postal_address_city"
postalCode = "postal_code"
urbanRural = "urban_rural"
schoolType = "school_type"
definition
authority
donations
genderOfStudents = "gender_of_students"
kmePeakBody = "kme_peak_body"
takiwa // takiwa in Supabase
territorialAuthority = "territorial_authority"
regionalCouncil = "regional_council"
localOffice = "local_office"
educationRegion = "education_region"
generalElectorate = "general_electorate"
maoriElectorate = "maori_electorate"
neighbourhoodSa2Code = "neighbourhood_sa2_code"
neighbourhoodSa2Name = "neighbourhood_sa2_name"
ward
colId = "col_id"
colName = "col_name"
latitude
longitude
enrolmentScheme = "enrolment_scheme"
eqi
totalSchoolRoll = "total_school_roll"
europeanPakehaStudents = "european_pakeha_students"
maoriStudents = "maori_students"
pacificStudents = "pacific_students"
asianStudents = "asian_students"
melaaStudents = "melaa_students"
otherStudents = "other_students"
internationalStudents = "international_students"
isolationIndex = "isolation_index"
languageOfInstruction = "language_of_instruction"
boardingFacilities = "boarding_facilities"
cohortEntry = "cohort_entry"
status
uePassRate2023AllLeavers = "ue_pass_rate_2023_all_leavers"
nceaPassRate2023AllLeavers = "ncea_pass_rate_2023_all_leavers"
uePassRate2023Year13 = "ue_pass_rate_2023_year_13"
nceaPassRate2023Year13 = "ncea_pass_rate_2023_year_13"
eqiRank = "eqi_rank"
eqiDeciles = "eqi_deciles"
europeanPercentage = "european_percentage"
maoriPercentage = "maori_percentage"
pacificPercentage = "pacific_percentage"
asianPercentage = "asian_percentage"
melaaPercentage = "melaa_percentage"
otherPercentage = "other_percentage"
internationalPercentage = "international_percentage"
totalUeNcea = "total_ue_ncea"
totalUeNceaY13 = "total_ue_ncea_y13"
createdAt = "created_at"
updatedAt = "updated_at"
