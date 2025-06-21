package com.kuralist.app.shared.ui.components.filter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolFilterBar(
    filterState: SchoolFilterState,
    modifier: Modifier = Modifier
) {
    val activeFilters by filterState.activeFilters.collectAsState()
    val activeUEFilter by filterState.activeUEFilter.collectAsState()
    val activeInternationalFilter by filterState.activeInternationalFilter.collectAsState()
    val filterSheetItem by filterState.filterSheetItem.collectAsState()
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Filter chips
        FilterChipView(
            filterCategories = filterState.filterCategories,
            activeFilters = activeFilters,
            onSelectCategory = { category -> 
                filterState.toggleCategoryFilter(category)
            },
            onUEFilterToggle = { 
                filterState.toggleUEFilter()
            },
            activeUEFilter = activeUEFilter,
            onInternationalFilterToggle = { 
                filterState.toggleInternationalFilter()
            },
            activeInternationalFilter = activeInternationalFilter,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
    
    // Filter options bottom sheet
    filterSheetItem?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { filterState.dismissFilterSheet() },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            FilterOptionsSheetView(
                item = item,
                activeFilters = activeFilters,
                onSelectValue = { category, value ->
                    filterState.selectFilterValue(category, value)
                },
                onDismiss = { filterState.dismissFilterSheet() },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
} 