package com.kuralist.app.shared.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SchoolFilterBar(
    // Parameters to pass to FilterChipView
    filterCategories: List<String>,
    activeFilters: Map<String, String>,
    onSelectCategory: (String) -> Unit,
    activeUEFilter: Boolean,
    onUEFilterToggle: () -> Unit,
    activeInternationalFilter: Boolean,
    onInternationalFilterToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        // As per spec: VStack with no spacing, padding for FilterChipView
        modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        FilterChipView(
            filterCategories = filterCategories,
            activeFilters = activeFilters,
            onSelectCategory = onSelectCategory,
            activeUEFilter = activeUEFilter,
            onUEFilterToggle = onUEFilterToggle,
            activeInternationalFilter = activeInternationalFilter,
            onInternationalFilterToggle = onInternationalFilterToggle
            // No specific modifier needed here unless further styling is required for FilterChipView itself
        )
    }
}
