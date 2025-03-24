package com.example.musicharmony

import android.util.Log

class HarmonyGenerator {

    // Поддерживаемые тональности и их аккорды (I-IV-V-I прогрессия)
    private val chordProgressions = mapOf(
        "C Major" to listOf("C", "F", "G", "C"),
        "G Major" to listOf("G", "C", "D", "G"),
        "A Minor" to listOf("Am", "Dm", "E", "Am"),
        "E Minor" to listOf("Em", "Am", "B", "Em")
    )

    // Простая заглушка для генерации гармонии на основе тональности
    fun generateHarmony(key: String): String {
        try {
            // Проверяем, поддерживается ли тональность
            val baseKey = chordProgressions.keys.find { key.contains(it) } ?: "Unknown Key"

            // Генерируем последовательность аккордов
            val chords = chordProgressions[baseKey] ?: listOf("Unknown", "Unknown", "Unknown", "Unknown")

            // Добавляем вариативность: иногда меняем прогрессию (например, I-VI-IV-V)
            val progression = if (Math.random() > 0.5) {
                when (baseKey) {
                    "C Major" -> listOf("C", "Am", "F", "G")
                    "G Major" -> listOf("G", "Em", "C", "D")
                    "A Minor" -> listOf("Am", "F", "Dm", "E")
                    "E Minor" -> listOf("Em", "C", "Am", "B")
                    else -> chords
                }
            } else {
                chords
            }

            return buildString {
                append("Generated harmony for $baseKey:\n")
                append(progression.joinToString(" -> "))
            }
        } catch (e: Exception) {
            Log.e("HarmonyGenerator", "Error generating harmony: ${e.message}")
            return "Error generating harmony: ${e.message}"
        }
    }
}