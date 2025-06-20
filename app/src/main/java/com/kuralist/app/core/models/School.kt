package com.kuralist.app.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "schools")
data class School(
    @PrimaryKey 
    @SerialName("school_number")
    val id: Int, // maps to school_number in DB
    
    @SerialName("school_name")
    val schoolName: String,
    
    val telephone: String? = null,
    val email: String? = null,
    val principal: String? = null,
    
    @SerialName("school_website")
    val schoolWebsite: String? = null,
    
    val street: String? = null,
    val suburb: String? = null,
    
    @SerialName("town_city")
    val townCity: String? = null,
    
    @SerialName("postal_address")
    val postalAddress: String? = null,
    
    @SerialName("postal_address_suburb")
    val postalAddressSuburb: String? = null,
    
    @SerialName("postal_address_city")
    val postalAddressCity: String? = null,
    
    @SerialName("postal_code")
    val postalCode: Int? = null,
    
    @SerialName("urban_rural")
    val urbanRural: String? = null,
    
    @SerialName("school_type")
    val schoolType: String? = null,
    
    val definition: String? = null,
    val authority: String? = null, // e.g., "State", "Private", "Integrated"
    val donations: String? = null,
    
    @SerialName("gender_of_students")
    val genderOfStudents: String? = null, // "Coeducational", "Boys", "Girls"
    
    @SerialName("kme_peak_body")
    val kmePeakBody: String? = null,
    
    val takiwa: String? = null,
    
    @SerialName("territorial_authority")
    val territorialAuthority: String? = null,
    
    @SerialName("regional_council")
    val regionalCouncil: String? = null,
    
    @SerialName("local_office")
    val localOffice: String? = null,
    
    @SerialName("education_region")
    val educationRegion: String? = null,
    
    @SerialName("general_electorate")
    val generalElectorate: String? = null,
    
    @SerialName("maori_electorate")
    val maoriElectorate: String? = null,
    
    @SerialName("neighbourhood_sa2_code")
    val neighbourhoodSa2Code: Int? = null,
    
    @SerialName("neighbourhood_sa2_name")
    val neighbourhoodSa2Name: String? = null,
    
    val ward: String? = null,
    
    @SerialName("col_id")
    val colId: String? = null, // Community of Learning ID
    
    @SerialName("col_name")
    val colName: String? = null, // Community of Learning Name
    
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    @SerialName("enrolment_scheme")
    val enrolmentScheme: String? = null,
    
    val eqi: Int? = null, // Equity Index
    
    @SerialName("total_school_roll")
    val totalSchoolRoll: Int? = null,
    
    @SerialName("european_pakeha_students")
    val europeanPakehaStudents: Int? = null,
    
    @SerialName("maori_students")
    val maoriStudents: Int? = null,
    
    @SerialName("pacific_students")
    val pacificStudents: Int? = null,
    
    @SerialName("asian_students")
    val asianStudents: Int? = null,
    
    @SerialName("melaa_students")
    val melaaStudents: Int? = null,
    
    @SerialName("other_students")
    val otherStudents: Int? = null,
    
    @SerialName("international_students")
    val internationalStudents: Int? = null,
    
    @SerialName("isolation_index")
    val isolationIndex: Double? = null,
    
    @SerialName("language_of_instruction")
    val languageOfInstruction: String? = null,
    
    @SerialName("boarding_facilities")
    val boardingFacilities: Boolean? = null,
    
    @SerialName("cohort_entry")
    val cohortEntry: Boolean? = null,
    
    val status: String? = null,
    
    @SerialName("ue_pass_rate_2023_all_leavers")
    val uePassRate2023AllLeavers: Double? = null,
    
    @SerialName("ncea_pass_rate_2023_all_leavers")
    val nceaPassRate2023AllLeavers: Double? = null,
    
    @SerialName("ue_pass_rate_2023_year_13")
    val uePassRate2023Year13: Double? = null,
    
    @SerialName("ncea_pass_rate_2023_year_13")
    val nceaPassRate2023Year13: Double? = null,
    
    @SerialName("eqi_rank")
    val eqiRank: Double? = null,
    
    @SerialName("eqi_deciles")
    val eqiDeciles: Int? = null,
    
    @SerialName("european_percentage")
    val europeanPercentage: Double? = null,
    
    @SerialName("maori_percentage")
    val maoriPercentage: Double? = null,
    
    @SerialName("pacific_percentage")
    val pacificPercentage: Double? = null,
    
    @SerialName("asian_percentage")
    val asianPercentage: Double? = null,
    
    @SerialName("melaa_percentage")
    val melaaPercentage: Double? = null,
    
    @SerialName("other_percentage")
    val otherPercentage: Double? = null,
    
    @SerialName("international_percentage")
    val internationalPercentage: Double? = null,
    
    @SerialName("total_ue_ncea")
    val totalUeNcea: Int? = null,
    
    @SerialName("total_ue_ncea_y13")
    val totalUeNceaY13: Int? = null,
    
    @SerialName("created_at")
    val createdAt: String,
    
    @SerialName("updated_at")
    val updatedAt: String
) {
    // Computed properties
    val location: String 
        get() = listOfNotNull(townCity, suburb).joinToString(", ")

    val coordinates: LatLng? 
        get() = if (latitude != null && longitude != null) LatLng(latitude, longitude) else null

    val searchableText: String 
        get() = "$schoolName ${suburb ?: ""} ${townCity ?: ""} ${authority ?: ""} ${schoolType ?: ""}".lowercase()
} 