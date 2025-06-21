package com.kuralist.app.features.schools.detail.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.sections.*

@Composable
fun SchoolDetailContent(
    school: School,
    bottomPadding: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp + bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stats Section - matches iOS top stats
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
        
        // Academic Performance Section
        if (school.uePassRate2023AllLeavers != null || school.nceaPassRate2023AllLeavers != null) {
            item {
                AcademicPerformanceSection(school = school)
            }
        }
    }
}