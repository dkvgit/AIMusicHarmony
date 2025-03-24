package com.example.musicharmony.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_results")
data class AnalysisResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val analysis: String,
    val harmony: String,
    val timestamp: Long = System.currentTimeMillis()
)