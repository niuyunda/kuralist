package com.kuralist.app.features.schools.list.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kuralist.app.R
import com.kuralist.app.core.models.School

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchoolLabels(
    school: School,
    modifier: Modifier = Modifier
) {
    val labels = generateSchoolLabels(school)
    
    if (labels.isNotEmpty()) {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            labels.forEach { labelData ->
                ModernSchoolChip(
                    text = labelData.text,
                    type = labelData.type,
                    backgroundColor = labelData.backgroundColor,
                    textColor = labelData.textColor
                )
            }
        }
    }
}

@Composable
private fun ModernSchoolChip(
    text: String,
    type: SchoolLabelType,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

data class SchoolLabelData(
    val text: String,
    val type: SchoolLabelType,
    val backgroundColor: Color,
    val textColor: Color
)

enum class SchoolLabelType {
    GENDER,
    SCHOOL_TYPE,
    AUTHORITY,
    SPECIAL_FEATURE,
    PERFORMANCE
}

@Composable
private fun generateSchoolLabels(school: School): List<SchoolLabelData> {
    return buildList {
        // Priority 1: Gender (if not coeducational)
        school.genderOfStudents?.let { gender ->
            if (!gender.contains("Coeducational", ignoreCase = true)) {
                val (backgroundColor, textColor) = when {
                    gender.contains("Boys", ignoreCase = true) -> 
                        Color(0xFF6750A4) to Color.White
                    gender.contains("Girls", ignoreCase = true) -> 
                        Color(0xFFD946EF) to Color.White
                    else -> Color(0xFF6750A4) to Color.White
                }
                add(SchoolLabelData(gender, SchoolLabelType.GENDER, backgroundColor, textColor))
            }
        }
        
        // Priority 2: School level/type
        school.schoolType?.let { type ->
            val displayType = when {
                type.contains("Primary", ignoreCase = true) -> {
                    when {
                        type.contains("1-6") -> "Primary 1-6"
                        type.contains("1-8") -> "Primary 1-8"
                        else -> "Primary"
                    }
                }
                type.contains("Secondary", ignoreCase = true) -> {
                    when {
                        type.contains("7-15") -> "Sec 7-15"
                        type.contains("9-15") -> "Sec 9-15"
                        type.contains("7-13") -> "Sec 7-13"
                        type.contains("9-13") -> "Sec 9-13"
                        else -> "Secondary"
                    }
                }
                type.contains("Composite", ignoreCase = true) -> {
                    when {
                        type.contains("1-15") -> "Comp 1-15"
                        type.contains("1-13") -> "Comp 1-13"
                        else -> "Composite"
                    }
                }
                else -> type
            }
            add(SchoolLabelData(
                displayType, 
                SchoolLabelType.SCHOOL_TYPE, 
                Color(0xFF2563EB), 
                Color.White
            ))
        }
        
        // Priority 3: Authority type
        school.authority?.let { authority ->
            val (backgroundColor, textColor) = when {
                authority.contains("State", ignoreCase = true) -> 
                    Color(0xFF059669) to Color.White
                authority.contains("Private", ignoreCase = true) -> 
                    Color(0xFF7C3AED) to Color.White
                authority.contains("Integrated", ignoreCase = true) -> 
                    Color(0xFF0891B2) to Color.White
                else -> Color(0xFF64748B) to Color.White
            }
            add(SchoolLabelData(authority, SchoolLabelType.AUTHORITY, backgroundColor, textColor))
        }
        
        // Priority 4: Special features
        if (school.boardingFacilities == true) {
            add(SchoolLabelData(
                stringResource(R.string.boarding), 
                SchoolLabelType.SPECIAL_FEATURE, 
                Color(0xFF92400E), 
                Color.White
            ))
        }
        
        school.internationalStudents?.let { intl ->
            if (intl > 0) {
                add(SchoolLabelData(
                    stringResource(R.string.international), 
                    SchoolLabelType.SPECIAL_FEATURE, 
                    Color(0xFF7C2D12), 
                    Color.White
                ))
            }
        }
        
        // Priority 5: Academic performance (only show if exceptional)
        school.uePassRate2023AllLeavers?.let { rate ->
            if (rate >= 80) {
                val backgroundColor = when {
                    rate >= 95 -> Color(0xFF15803D)
                    rate >= 90 -> Color(0xFF16A34A)
                    rate >= 85 -> Color(0xFF22C55E)
                    else -> Color(0xFF65A30D)
                }
                add(SchoolLabelData(
                    "UE ${String.format("%.0f", rate)}%", 
                    SchoolLabelType.PERFORMANCE, 
                    backgroundColor, 
                    Color.White
                ))
            }
        }
        
        school.nceaPassRate2023AllLeavers?.let { rate ->
            if (rate >= 85) {
                val backgroundColor = when {
                    rate >= 95 -> Color(0xFF15803D)
                    rate >= 90 -> Color(0xFF16A34A)
                    else -> Color(0xFF22C55E)
                }
                add(SchoolLabelData(
                    "NCEA ${String.format("%.0f", rate)}%", 
                    SchoolLabelType.PERFORMANCE, 
                    backgroundColor, 
                    Color.White
                ))
            }
        }
    }
} 