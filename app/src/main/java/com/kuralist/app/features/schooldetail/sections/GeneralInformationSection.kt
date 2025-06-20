package com.kuralist.app.features.schooldetail.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schooldetail.components.InfoRow
import com.kuralist.app.features.schooldetail.components.SectionCard

@Composable
fun GeneralInformationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "General Information",
        icon = Icons.Default.Info,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            school.authority?.let { authority ->
                InfoRow("Authority", authority)
            }
            school.genderOfStudents?.let { gender ->
                InfoRow("Gender Composition", gender)
            }
            school.boardingFacilities?.let { hasBoarding ->
                InfoRow("Boarding Facilities", if (hasBoarding) "Available" else "Not Available")
            }
            school.languageOfInstruction?.let { language ->
                InfoRow("Language of Instruction", language)
            }
            school.enrolmentScheme?.let { scheme ->
                InfoRow("Enrolment Scheme", scheme)
            }
        }
    }
}