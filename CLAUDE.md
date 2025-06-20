# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK  
- `./gradlew test` - Run unit tests
- `./gradlew connectedAndroidTest` - Run instrumentation tests
- `./gradlew clean` - Clean build artifacts

## Architecture

This is a **Kuralist** Android app - a New Zealand school information platform using offline-first MVVM architecture.

### Tech Stack
- **UI**: Jetpack Compose with Material 3
- **Backend**: Supabase (auth, database, realtime)
- **Local Database**: Room for offline-first data
- **Dependency Injection**: Manual DI (Hilt disabled)
- **Maps**: Google Maps Compose
- **Navigation**: Navigation Compose

### Core Components
- **SupabaseManager**: Handles remote data sync and authentication
- **SchoolDatabase**: Room database for local school data storage  
- **SchoolService**: Business logic layer coordinating local/remote data
- **Feature modules**: Each major screen (auth, school list, map, etc.) has dedicated ViewModel

### Data Flow
1. **Offline-first**: App works entirely offline using Room database
2. **Background sync**: SupabaseManager syncs with remote when online
3. **StateFlow**: ViewModels expose UI state via StateFlow
4. **Repository pattern**: Services abstract data layer from ViewModels

### Key Configuration
- **Package**: `com.kuralist.app`
- **Min SDK**: 29 (Android 10)
- **Supabase URL**: https://wdtdmzptgfmhzdsmnxnl.supabase.co
- **Database**: ~2500 NZ schools pre-populated

### Feature Structure
Each feature follows consistent pattern:
```
features/[feature]/
├── [Feature]Screen.kt     # Compose UI
├── [Feature]ViewModel.kt  # State management
```

Core business logic lives in `core/services/` with models in `core/models/`.