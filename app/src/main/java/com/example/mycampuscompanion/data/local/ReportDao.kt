package com.example.mycampuscompanion.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mycampuscompanion.data.model.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert
    suspend fun insert(report: Report)

    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<Report>>
}