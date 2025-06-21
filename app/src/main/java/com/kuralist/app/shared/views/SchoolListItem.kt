package com.kuralist.app.shared.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School

@Composable
fun SchoolListItem(
    school: School,
    onSchoolClick: (School) -> Unit,
    onFavoriteClick: (School) -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSchoolClick(school) },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // School name
                Text(
                    text = school.schoolName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Location and details row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Location
                    if (school.location.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = school.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    // Student count
                    school.totalSchoolRoll?.let { roll ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$roll",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // School labels as chips - organized by priority
                val labels = buildList<Pair<String, Color>> {
                    // Priority 1: Gender (if not coeducational)
                    school.genderOfStudents?.let { gender ->
                        if (!gender.contains("Coeducational", ignoreCase = true)) {
                            val color = when {
                                gender.contains("Boys", ignoreCase = true) -> Color(0xFFB39DDB) // Light Purple
                                gender.contains("Girls", ignoreCase = true) -> Color(0xFFFF8A95) // Light Pink
                                else -> Color(0xFFB39DDB)
                            }
                            add(Pair(gender, color))
                        }
                    }
                    
                    // Priority 2: School level/type
                    school.schoolType?.let { type ->
                        val displayType = when {
                            type.contains("Primary", ignoreCase = true) -> {
                                val years = when {
                                    type.contains("1-6") -> "Primary 1-6"
                                    type.contains("1-8") -> "Primary 1-8"
                                    else -> "Primary"
                                }
                                years
                            }
                            type.contains("Secondary", ignoreCase = true) -> {
                                val years = when {
                                    type.contains("7-15") -> "Sec 7-15"
                                    type.contains("9-15") -> "Sec 9-15"
                                    type.contains("7-13") -> "Sec 7-13"
                                    type.contains("9-13") -> "Sec 9-13"
                                    else -> "Secondary"
                                }
                                years
                            }
                            type.contains("Composite", ignoreCase = true) -> {
                                val years = when {
                                    type.contains("1-15") -> "Comp 1-15"
                                    type.contains("1-13") -> "Comp 1-13"
                                    else -> "Composite"
                                }
                                years
                            }
                            else -> type
                        }
                        val color = Color(0xFF64B5F6) // Light Blue
                        add(Pair(displayType, color))
                    }
                    
                    // Priority 3: Authority type
                    school.authority?.let { authority ->
                        val color = when {
                            authority.contains("State", ignoreCase = true) -> Color(0xFFA5D6A7) // Light Green
                            authority.contains("Private", ignoreCase = true) -> Color(0xFFCE93D8) // Light Purple
                            authority.contains("Integrated", ignoreCase = true) -> Color(0xFF80CBC4) // Light Teal
                            else -> Color(0xFFB0BEC5) // Light Blue Grey
                        }
                        add(Pair(authority, color))
                    }
                    
                    // Priority 4: Special features
                    if (school.boardingFacilities == true) {
                        add(Pair("Boarding", Color(0xFFD7CCC8))) // Light Brown
                    }
                    
                    school.internationalStudents?.let { intl ->
                        if (intl > 0) {
                            add(Pair("International", Color(0xFF9C27B0))) // Purple
                        }
                    }
                    
                    // Priority 5: Academic performance (only show if exceptional)
                    school.uePassRate2023AllLeavers?.let { rate ->
                        if (rate >= 80) { // Only show high performing schools
                            val color = when {
                                rate >= 95 -> Color(0xFF2E7D32) // Dark Green
                                rate >= 90 -> Color(0xFF4CAF50) // Green
                                rate >= 85 -> Color(0xFF8BC34A) // Light Green
                                else -> Color(0xFFCDDC39) // Lime Green
                            }
                            add(Pair("UE ${String.format("%.0f", rate)}%", color))
                        }
                    }
                    
                    school.nceaPassRate2023AllLeavers?.let { rate ->
                        if (rate >= 85) { // Only show high performing schools
                            val color = when {
                                rate >= 95 -> Color(0xFF2E7D32) // Dark Green
                                rate >= 90 -> Color(0xFF4CAF50) // Green
                                else -> Color(0xFF8BC34A) // Light Green
                            }
                            add(Pair("NCEA ${String.format("%.0f", rate)}%", color))
                        }
                    }
                }
                
                if (labels.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(labels.take(4)) { (label, color) -> // Limit to 4 chips to avoid overcrowding
                            SchoolInfoChip(
                                text = label,
                                backgroundColor = color.copy(alpha = 0.15f),
                                textColor = color.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
            
            // Favorite button
            IconButton(
                onClick = { onFavoriteClick(school) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SchoolInfoChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = 1.dp,
        tonalElevation = 2.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}