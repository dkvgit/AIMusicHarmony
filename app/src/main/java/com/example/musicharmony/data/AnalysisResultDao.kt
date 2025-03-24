package com.example.musicharmony.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisResultDao {
    @Insert
    suspend fun insert(result: AnalysisResult)

    @Query("SELECT * FROM analysis_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<AnalysisResult>>
}