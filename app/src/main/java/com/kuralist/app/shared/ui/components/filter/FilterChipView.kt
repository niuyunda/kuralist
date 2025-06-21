package com.kuralist.app.shared.ui.components.filter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kuralist.app.R

@Composable
private fun getLocalizedCategoryName(category: String): String {
    return when (category) {
        "City" -> stringResource(R.string.filter_city)
        "Suburb" -> stringResource(R.string.filter_suburb)
        "Level" -> stringResource(R.string.filter_level)
        "Authority" -> stringResource(R.string.filter_authority)
        "Gender" -> stringResource(R.string.filter_gender)
        else -> category
    }
}

@Composable
fun FilterChipView(
    filterCategories: List<String>,
    activeFilters: Map<String, String>,
    onSelectCategory: (String) -> Unit,
    onUEFilterToggle: () -> Unit,
    activeUEFilter: Boolean,
    onInternationalFilterToggle: () -> Unit,
    activeInternationalFilter: Boolean,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dynamic category chips
        items(filterCategories) { category ->
            val isActive = activeFilters.containsKey(category)
            val displayText = if (isActive) {
                activeFilters[category] ?: getLocalizedCategoryName(category)
            } else {
                getLocalizedCategoryName(category)
            }
            
            FilterChipButton(
                category = displayText,
                isActive = isActive,
                onTap = { onSelectCategory(category) }
            )
        }
        
        // International students toggle chip
        item {
            FilterChipButton(
                category = stringResource(R.string.international),
                isActive = activeInternationalFilter,
                onTap = onInternationalFilterToggle
            )
        }
        
        // UE/NCEA toggle chip
        item {
            FilterChipButton(
                category = stringResource(R.string.ue_ncea),
                isActive = activeUEFilter,
                onTap = onUEFilterToggle
            )
        }
    }
} 