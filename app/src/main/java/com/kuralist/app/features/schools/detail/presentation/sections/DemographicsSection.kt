package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard
import androidx.compose.ui.res.stringResource
import com.kuralist.app.R

@Composable
fun DemographicsSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = stringResource(R.string.demographics),
        icon = Icons.Default.Person,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            school.totalSchoolRoll?.let { total ->
                // Ethnicity breakdown - matching iOS order and format
                school.europeanPakehaStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.european_pakeha), stringResource(R.string.percentage_count_format, percentage, count))
                }
                school.maoriStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.maori), stringResource(R.string.percentage_count_format, percentage, count))
                }
                school.pacificStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.pacific), stringResource(R.string.percentage_count_format, percentage, count))
                }
                school.asianStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.asian), stringResource(R.string.percentage_count_format, percentage, count))
                }
                school.melaaStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.melaa), stringResource(R.string.percentage_count_format, percentage, count))
                }
                school.otherStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.other), stringResource(R.string.percentage_count_format, percentage, count))
                }
                school.internationalStudents?.let { count ->
                    val percentage = (count.toDouble() / total * 100).toInt()
                    InfoRow(stringResource(R.string.international), stringResource(R.string.percentage_count_format, percentage, count))
                }
            }
        }
    }
}