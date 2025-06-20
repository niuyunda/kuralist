package com.kuralist.app.features.schooldetail.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schooldetail.components.InfoRow
import com.kuralist.app.features.schooldetail.components.SectionCard

@Composable
fun AddressLocationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Address & Location",
        icon = Icons.Default.LocationOn,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            school.street?.let { street ->
                InfoRow("Street", street)
            }
            school.suburb?.let { suburb ->
                InfoRow("Suburb", suburb)
            }
            school.townCity?.let { city ->
                InfoRow("City", city)
            }
            school.postalCode?.let { code ->
                InfoRow("Postal Code", code.toString())
            }
            school.urbanRural?.let { type ->
                InfoRow("Area Type", type)
            }
            
            if (school.coordinates != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Open in Maps */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open in Maps",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}