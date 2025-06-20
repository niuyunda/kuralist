package com.kuralist.app.features.schooldetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schooldetail.sections.*

@Composable
fun SchoolDetailContent(
    school: School,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Quick Stats Section
        item {
            QuickStatsSection(school = school)
        }
        
        // Contact Information Section
        item {
            ContactInformationSection(school = school)
        }
        
        // Address & Location Section
        item {
            AddressLocationSection(school = school)
        }
        
        // Academic Performance Section
        if (school.uePassRate2023AllLeavers != null || school.nceaPassRate2023AllLeavers != null) {
            item {
                AcademicPerformanceSection(school = school)
            }
        }
        
        // Demographics Section
        if (school.totalSchoolRoll != null) {
            item {
                DemographicsSection(school = school)
            }
        }
        
        // General Information Section
        item {
            GeneralInformationSection(school = school)
        }
        
        // Administrative Information Section
        item {
            AdministrativeInformationSection(school = school)
        }
    }
}