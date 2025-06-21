package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun AdministrativeInformationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Administrative Information",
        icon = Icons.Default.Settings,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            school.educationRegion?.let { region ->
                InfoRow("Education Region", region)
            }
            school.territorialAuthority?.let { authority ->
                InfoRow("Territorial Authority", authority)
            }
            school.regionalCouncil?.let { council ->
                InfoRow("Regional Council", council)
            }
            school.colName?.let { col ->
                InfoRow("Community of Learning", col)
            }
        }
    }
}