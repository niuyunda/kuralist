package com.kuralist.app.features.schools.list.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuralist.app.shared.ui.components.filter.SchoolFilterBar
import com.kuralist.app.shared.ui.components.filter.SchoolFilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchAndFilterBar(
    filterState: SchoolFilterState,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Search schools...",
    isElevated: Boolean = false // For map overlay styling
) {
    val filterSearchText by filterState.searchText.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Search bar
        if (isElevated) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                SearchTextField(
                    value = filterSearchText,
                    onValueChange = { filterState.updateSearchText(it) },
                    placeholder = searchPlaceholder,
                    onClear = { filterState.updateSearchText("") },
                    isElevated = true
                )
            }
        } else {
            SearchTextField(
                value = filterSearchText,
                onValueChange = { filterState.updateSearchText(it) },
                placeholder = searchPlaceholder,
                onClear = { filterState.updateSearchText("") },
                isElevated = false
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Filter Bar
        if (isElevated) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                SchoolFilterBar(
                    filterState = filterState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            SchoolFilterBar(
                filterState = filterState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onClear: () -> Unit,
    isElevated: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isElevated) {
                    Modifier.background(Color.Transparent)
                } else {
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                }
            ),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        colors = if (isElevated) {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        } else {
            OutlinedTextFieldDefaults.colors()
        }
    )
} 