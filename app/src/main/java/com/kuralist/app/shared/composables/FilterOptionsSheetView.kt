package com.kuralist.app.shared.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.FilterSheetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOptionsSheetView(
    item: FilterSheetItem,
    onOptionSelected: (category: String, option: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    val filteredOptions = remember(searchText, item.options) {
        if (searchText.isBlank()) {
            item.options
        } else {
            item.options.filter {
                it.contains(searchText, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding() // Add padding for system navigation bars
    ) {
        // Header with Title and Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select ${item.categoryName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDismiss()
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close filter options")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search TextField
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search ${item.categoryName}...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Options List
        if (filteredOptions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                 Text("No options found for \"${searchText}\"")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(filteredOptions, key = { it }) { option ->
                    val isSelected = item.selectedOption == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onOptionSelected(item.categoryName, option)
                                // onDismiss() // Dismissal should be handled by SchoolFilterManager after setting filter
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
         Spacer(modifier = Modifier.height(8.dp))
    }
}
