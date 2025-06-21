package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kuralist.app.R
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun AcademicPerformanceSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = stringResource(R.string.academic_performance),
        icon = Icons.Default.Star,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            school.uePassRate2023AllLeavers?.let { rate ->
                InfoRow(stringResource(R.string.ue_pass_rate_all_leavers), "${rate.toInt()}%")
            }
            school.nceaPassRate2023AllLeavers?.let { rate ->
                InfoRow(stringResource(R.string.ncea_pass_rate_all_leavers), "${rate.toInt()}%")
            }
            school.uePassRate2023Year13?.let { rate ->
                InfoRow(stringResource(R.string.ue_pass_rate_year_13), "${rate.toInt()}%")
            }
            school.nceaPassRate2023Year13?.let { rate ->
                InfoRow(stringResource(R.string.ncea_pass_rate_year_13), "${rate.toInt()}%")
            }
        }
    }
}