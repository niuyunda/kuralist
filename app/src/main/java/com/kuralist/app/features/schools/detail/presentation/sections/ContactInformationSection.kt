package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kuralist.app.R
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun ContactInformationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = stringResource(R.string.contact_info),
        icon = Icons.Default.Phone,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            school.telephone?.let { phone ->
                ContactRow(
                    icon = Icons.Default.Phone,
                    label = stringResource(R.string.phone),
                    value = phone
                )
            }
            school.email?.let { email ->
                ContactRow(
                    icon = Icons.Default.Email,
                    label = stringResource(R.string.email),
                    value = email
                )
            }
            school.schoolWebsite?.let { website ->
                ContactRow(
                    icon = Icons.Default.Info,
                    label = stringResource(R.string.website),
                    value = website
                )
            }
            school.street?.let { street ->
                ContactRow(
                    icon = Icons.Default.LocationOn,
                    label = stringResource(R.string.address),
                    value = street
                )
            }
            
            if (school.telephone == null && school.email == null && 
                school.schoolWebsite == null && school.street == null) {
                Text(
                    text = stringResource(R.string.no_contact_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}