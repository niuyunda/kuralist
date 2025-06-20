package com.kuralist.app.core.services.database

import androidx.room.*
import com.kuralist.app.core.models.School
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY schoolName ASC")
    suspend fun getAllSchools(): List<School>

    @Query("SELECT * FROM schools ORDER BY schoolName ASC")
    fun getAllSchoolsFlow(): Flow<List<School>>

    @Query("SELECT * FROM schools WHERE id = :id")
    suspend fun getSchoolById(id: Int): School?

    @Query("""
        SELECT * FROM schools 
        WHERE schoolName LIKE '%' || :query || '%' 
        OR suburb LIKE '%' || :query || '%' 
        OR townCity LIKE '%' || :query || '%' 
        OR authority LIKE '%' || :query || '%'
        ORDER BY schoolName ASC
    """)
    suspend fun searchSchools(query: String): List<School>

    @Query("""
        SELECT * FROM schools 
        WHERE (:region IS NULL OR townCity = :region)
        AND (:schoolType IS NULL OR schoolType = :schoolType)
        AND (:hasBoarding IS NULL OR boardingFacilities = :hasBoarding)
        AND (:minUEPassRate IS NULL OR uePassRate2023AllLeavers >= :minUEPassRate)
        ORDER BY schoolName ASC
    """)
    suspend fun filterSchools(
        region: String? = null,
        schoolType: String? = null,
        hasBoarding: Boolean? = null,
        minUEPassRate: Double? = null
    ): List<School>

    @Query("""
        SELECT * FROM schools 
        WHERE latitude IS NOT NULL AND longitude IS NOT NULL
        AND latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLng AND :maxLng
        ORDER BY schoolName ASC
    """)
    suspend fun getSchoolsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<School>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<School>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: School)

    @Update
    suspend fun updateSchool(school: School)

    @Delete
    suspend fun deleteSchool(school: School)

    @Query("DELETE FROM schools")
    suspend fun deleteAllSchools()

    @Query("SELECT COUNT(*) FROM schools")
    suspend fun getSchoolCount(): Int

    @Query("SELECT DISTINCT townCity FROM schools WHERE townCity IS NOT NULL ORDER BY townCity ASC")
    suspend fun getAllCities(): List<String>

    @Query("SELECT DISTINCT suburb FROM schools WHERE suburb IS NOT NULL ORDER BY suburb ASC")
    suspend fun getAllSuburbs(): List<String>

    @Query("SELECT DISTINCT schoolType FROM schools WHERE schoolType IS NOT NULL ORDER BY schoolType ASC")
    suspend fun getAllSchoolTypes(): List<String>

    @Query("SELECT DISTINCT authority FROM schools WHERE authority IS NOT NULL ORDER BY authority ASC")
    suspend fun getAllAuthorities(): List<String>

    @Query("SELECT DISTINCT genderOfStudents FROM schools WHERE genderOfStudents IS NOT NULL ORDER BY genderOfStudents ASC")
    suspend fun getAllGenderTypes(): List<String>

    @Query("SELECT updatedAt FROM schools ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestUpdateTime(): String?
} 