package com.example.musicharmony

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File

class AudioAnalyzer(private val context: Context) {

    fun analyzeAudio(audioFile: File): String {
        if (!audioFile.exists()) {
            return "Audio file not found"
        }

        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioFile.absolutePath)

            // Извлекаем метаданные
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown Title"

            retriever.release()

            // Формируем результат
            val durationSeconds = duration?.div(1000) ?: 0
            return buildString {
                append("Title: $title\n")
                append("Artist: $artist\n")
                append("Duration: $durationSeconds seconds\n")
                append("Detected key: C Major (placeholder)")
            }
        } catch (e: Exception) {
            Log.e("AudioAnalyzer", "Error analyzing audio: ${e.message}")
            return "Error analyzing audio: ${e.message}"
        }
    }
}