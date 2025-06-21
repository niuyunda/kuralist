package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kuralist.app.R
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun GeneralInformationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = stringResource(R.string.general_information),
        icon = Icons.Default.Info,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            school.urbanRural?.let { type ->
                InfoRow(stringResource(R.string.urban_rural), type)
            }
            school.authority?.let { authority ->
                InfoRow(stringResource(R.string.authority), authority)
            }
            school.donations?.let { donations ->
                InfoRow(stringResource(R.string.donations), donations)
            }
            school.genderOfStudents?.let { gender ->
                InfoRow(stringResource(R.string.gender_of_students), gender)
            }
            school.enrolmentScheme?.let { scheme ->
                InfoRow(stringResource(R.string.enrolment_scheme), if (scheme == "No") stringResource(R.string.no) else stringResource(R.string.yes))
            }
            school.status?.let { status ->
                InfoRow(stringResource(R.string.status), status)
            }
            school.boardingFacilities?.let { hasBoarding ->
                InfoRow(stringResource(R.string.boarding_facilities), if (hasBoarding) stringResource(R.string.yes) else stringResource(R.string.no))
            }
            school.cohortEntry?.let { hasCohort ->
                InfoRow(stringResource(R.string.cohort_entry), if (hasCohort) stringResource(R.string.yes) else stringResource(R.string.no))
            }
            school.languageOfInstruction?.let { language ->
                InfoRow(stringResource(R.string.language_of_instruction), language)
            }
            school.isolationIndex?.let { index ->
                InfoRow(stringResource(R.string.isolation_index), index.toString())
            }
        }
    }
}