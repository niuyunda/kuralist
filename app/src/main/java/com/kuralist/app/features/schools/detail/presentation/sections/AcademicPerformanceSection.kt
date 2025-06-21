package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun AcademicPerformanceSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Academic Performance",
        icon = Icons.Default.Star,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            school.uePassRate2023AllLeavers?.let { rate ->
                InfoRow("UE Pass Rate (All Leavers)", "${rate.toInt()}%")
            }
            school.nceaPassRate2023AllLeavers?.let { rate ->
                InfoRow("NCEA Pass Rate (All Leavers)", "${rate.toInt()}%")
            }
            school.uePassRate2023Year13?.let { rate ->
                InfoRow("UE Pass Rate (Year 13)", "${rate.toInt()}%")
            }
            school.nceaPassRate2023Year13?.let { rate ->
                InfoRow("NCEA Pass Rate (Year 13)", "${rate.toInt()}%")
            }
        }
    }
}