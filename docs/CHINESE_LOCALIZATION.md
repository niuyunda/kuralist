# Chinese Localization Implementation

This document describes the implementation of Chinese language support for the Kuralist Android app.

## What Was Added

### 1. Chinese String Resources

- Created `app/src/main/res/values-zh/strings.xml` with Chinese translations for all app strings
- Added comprehensive translations covering:
  - General UI elements (buttons, navigation, etc.)
  - Authentication screens
  - School information and details
  - Filters and search functionality (including filter categories)
  - Error messages and feedback
  - Profile and settings screens

### 2. Filter System Localization

- Added localized filter category names:
  - `filter_city` (城市) - City
  - `filter_suburb` (郊区) - Suburb
  - `filter_level` (级别) - Level
  - `filter_authority` (管理机构) - Authority
  - `filter_gender` (性别) - Gender
- Updated `FilterChipView.kt` to use localized category names
- Updated `FilterOptionsSheetView.kt` to display localized titles and search placeholders
- Added helper functions to map English category keys to localized display strings

### 3. Language Utility Class

- Created `app/src/main/java/com/kuralist/app/core/utils/LanguageUtils.kt`
- Provides functions for:
  - Saving and retrieving language preferences using DataStore
  - Getting available languages
  - Updating app language context
  - Getting display names for languages

### 4. Updated UI Components

- **SearchBar**: Updated to use string resources instead of hardcoded strings
- **AuthScreen**: Converted all hardcoded text to use string resources
- **FavoritesScreen**: Updated with localized strings
- **ProfileScreen**: Added language selection feature and localized all text
- **FilterChipView**: Added Chinese support for filter categories
- **FilterOptionsSheetView**: Added Chinese support for filter selection interface

### 5. Build Configuration

- Added locale configuration in `app/build.gradle.kts`:
  ```kotlin
  resConfigs("en", "zh")
  ```

## How to Use

### For Users

1. Open the app and navigate to the Profile tab
2. Look for the "Language" section
3. Tap on the language option to open the selection dialog
4. Choose between English and 中文 (Chinese)
5. The language preference is saved automatically
6. Filter categories and options will now display in the selected language

### For Developers

1. **Adding New Strings**: Always add new strings to both `values/strings.xml` and `values-zh/strings.xml`
2. **Using String Resources**: Use `stringResource(R.string.your_string_key)` in Compose functions
3. **Context Strings**: Use `context.getString(R.string.your_string_key)` when you need strings outside of Compose
4. **Filter Categories**: Use the `getLocalizedCategoryName()` helper function in filter components to ensure proper localization

## Supported Languages

- **English (en)**: Default language
- **Chinese (zh)**: Simplified Chinese

## Filter Localization Details

The filter system uses a mapping approach where:

1. Internal logic uses English keys ("City", "Suburb", etc.) for consistency
2. UI components use `getLocalizedCategoryName()` helper functions to display localized names
3. Filter options (actual school data) remain in their original language as they represent real data
4. Filter interface elements (titles, placeholders, buttons) are fully localized

This approach ensures data integrity while providing a localized user experience.

## File Structure

```
app/src/main/res/
├── values/
│   └── strings.xml          # English strings
└── values-zh/
    └── strings.xml          # Chinese strings

app/src/main/java/com/kuralist/app/
├── core/utils/
│   └── LanguageUtils.kt     # Language management utility
└── features/profile/presentation/
    └── ProfileScreen.kt     # Language selection UI
```

## Key Features

### Dynamic Language Switching

- Language preferences are stored using DataStore
- Changes are applied immediately to new screens
- No app restart required for most text changes

### Comprehensive Coverage

- All user-facing text has been localized
- Error messages and feedback are translated
- Navigation and UI elements support both languages

### Developer-Friendly

- Clear utility functions for language management
- Consistent approach using Android's standard localization system
- Easy to extend for additional languages

## Adding More Languages

To add support for additional languages:

1. Create a new `values-{language_code}` directory (e.g., `values-es` for Spanish)
2. Copy `strings.xml` from `values` directory and translate all strings
3. Add the language code to `LanguageUtils.kt`:
   ```kotlin
   const val LANGUAGE_SPANISH = "es"
   ```
4. Update the `getAvailableLanguages()` function
5. Add the language to build.gradle.kts resConfigs
6. Update the language selection UI if needed

## Testing

The implementation has been tested with:

- ✅ Build compilation
- ✅ String resource validation
- ✅ No duplicate resource errors
- ✅ Language switching functionality

## Notes

- The app uses simplified Chinese (zh) as the Chinese variant
- Language changes are persisted across app sessions
- The default fallback language is English
- All text in the app should now support both languages

## Future Improvements

- Add traditional Chinese support (zh-TW)
- Implement automatic language detection based on system locale
- Add RTL language support for Arabic/Hebrew if needed
- Consider using more advanced localization libraries for complex pluralization rules
