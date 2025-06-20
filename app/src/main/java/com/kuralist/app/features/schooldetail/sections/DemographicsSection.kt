package com.kuralist.app.features.schooldetail.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schooldetail.components.InfoRow
import com.kuralist.app.features.schooldetail.components.SectionCard

@Composable
fun DemographicsSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Demographics",
        icon = Icons.Default.Person,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            school.totalSchoolRoll?.let { total ->
                InfoRow("Total Students", total.toString())
                
                // Ethnicity breakdown
                val ethnicityData = listOfNotNull(
                    school.europeanPakehaStudents?.let { "European/Pākehā" to it },
                    school.maoriStudents?.let { "Māori" to it },
                    school.pacificStudents?.let { "Pacific" to it },
                    school.asianStudents?.let { "Asian" to it },
                    school.melaaStudents?.let { "MELAA" to it },
                    school.otherStudents?.let { "Other" to it },
                    school.internationalStudents?.let { "International" to it }
                )
                
                if (ethnicityData.isNotEmpty()) {
                    Text(
                        text = "Ethnicity Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    ethnicityData.forEach { (ethnicity, count) ->
                        val percentage = (count.toDouble() / total * 100).toInt()
                        InfoRow(ethnicity, "$count ($percentage%)")
                    }
                }
            }
        }
    }
}