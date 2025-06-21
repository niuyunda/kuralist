package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kuralist.app.R
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schools.detail.presentation.components.InfoRow
import com.kuralist.app.features.schools.detail.presentation.components.SectionCard

@Composable
fun AddressLocationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Address Section
        SectionCard(
            title = stringResource(R.string.address),
            icon = Icons.Default.LocationOn,
            modifier = Modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                school.townCity?.let { city ->
                    InfoRow(stringResource(R.string.city), city)
                }
                school.street?.let { street ->
                    InfoRow(stringResource(R.string.address), street)
                }
                school.postalCode?.let { code ->
                    InfoRow(stringResource(R.string.postal_code), code.toString())
                }
            }
        }
        
        // Location Map Section (matching iOS)
        if (school.coordinates != null) {
            SectionCard(
                title = stringResource(R.string.location),
                icon = Icons.Default.Place,
                modifier = Modifier
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Simple map placeholder (matches iOS design)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = school.schoolName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Get Directions Button
                    Button(
                        onClick = { /* TODO: Open in Maps */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.get_directions),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}