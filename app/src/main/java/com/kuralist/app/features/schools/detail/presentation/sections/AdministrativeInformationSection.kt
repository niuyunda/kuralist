package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kuralist.app.R
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun AdministrativeInformationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = stringResource(R.string.admin_region),
        icon = Icons.Default.Settings,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            school.takiwa?.let { takiwa ->
                InfoRow(stringResource(R.string.takiwa), takiwa)
            }
            school.territorialAuthority?.let { authority ->
                InfoRow(stringResource(R.string.territorial_authority), authority)
            }
            school.regionalCouncil?.let { council ->
                InfoRow(stringResource(R.string.regional_council), council)
            }
            school.localOffice?.let { office ->
                InfoRow(stringResource(R.string.local_office), office)
            }
            school.educationRegion?.let { region ->
                InfoRow(stringResource(R.string.education_region), region)
            }
            school.generalElectorate?.let { electorate ->
                InfoRow(stringResource(R.string.general_electorate), electorate)
            }
            school.maoriElectorate?.let { electorate ->
                InfoRow(stringResource(R.string.maori_electorate), electorate)
            }
            school.neighbourhoodSa2Name?.let { neighbourhood ->
                InfoRow(stringResource(R.string.neighbourhood_sa2), neighbourhood)
            }
            school.ward?.let { ward ->
                InfoRow(stringResource(R.string.ward), ward)
            }
            school.colName?.let { col ->
                InfoRow(stringResource(R.string.community_of_learning), col)
            }
        }
    }
}