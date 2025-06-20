package com.kuralist.app.shared.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipView(
    filterCategories: List<String>,
    activeFilters: Map<String, String>,
    onSelectCategory: (String) -> Unit,
    activeUEFilter: Boolean,
    onUEFilterToggle: () -> Unit,
    activeInternationalFilter: Boolean,
    onInternationalFilterToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp), // Padding for the content within the scrollable row
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Category Chips
        filterCategories.forEach { category ->
            val isActive = activeFilters.containsKey(category)
            val chipText = if (isActive) activeFilters[category] ?: category else category
            FilterChipButton(
                text = chipText,
                isActive = isActive,
                onClick = { onSelectCategory(category) }
            )
        }

        // International Students Toggle Chip
        FilterChipButton(
            text = "International",
            isActive = activeInternationalFilter,
            onClick = onInternationalFilterToggle
        )

        // UE / NCEA High Performers Toggle Chip
        FilterChipButton(
            text = "UE/NCEA >70%",
            isActive = activeUEFilter,
            onClick = onUEFilterToggle
        )
    }
}
