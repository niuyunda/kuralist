package com.kuralist.app.features.schools.detail.presentation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuralist.app.core.models.School
import com.kuralist.app.features.schooldetail.components.ContactItem
import com.kuralist.app.features.schooldetail.components.ContactItemRow
import com.kuralist.app.features.schooldetail.components.SectionCard

@Composable
fun ContactInformationSection(
    school: School,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Contact Information",
        icon = Icons.Default.Phone,
        modifier = modifier
    ) {
        val contactItems = buildList<ContactItem> {
            school.telephone?.let { phone ->
                add(ContactItem("Phone", phone, Icons.Default.Phone))
            }
            school.email?.let { email ->
                add(ContactItem("Email", email, Icons.Default.Email))
            }
            school.schoolWebsite?.let { website ->
                add(ContactItem("Website", website, Icons.Default.Info))
            }
            school.principal?.let { principal ->
                add(ContactItem("Principal", principal, Icons.Default.Person))
            }
        }
        
        if (contactItems.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No contact information available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                contactItems.forEach { item ->
                    ContactItemRow(item = item)
                }
            }
        }
    }
}