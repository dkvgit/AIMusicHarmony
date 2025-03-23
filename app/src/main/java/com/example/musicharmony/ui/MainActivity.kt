package com.example.musicharmony.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIMusicHarmonyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HarmonizerScreen()
                }
            }
        }
    }
}

@Composable
fun AIMusicHarmonyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@Composable
fun HarmonizerScreen() {
    // Состояние для результата анализа (заглушка)
    val analysisResult = remember { mutableStateOf("Не загружено") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = "Музыкальный ИИ-Хармонизатор",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Кнопка для загрузки аудио (заглушка)
        Button(
            onClick = { /* Позже добавим логику загрузки аудио */ },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Загрузить аудио", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Результат анализа
        Text(
            text = "Результат анализа: ${analysisResult.value}",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопка для генерации гармонии (заглушка)
        Button(
            onClick = { /* Позже добавим логику генерации */ },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = analysisResult.value != "Не загружено" // Кнопка активна только после загрузки
        ) {
            Text(text = "Сгенерировать гармонию", fontSize = 18.sp)
        }
    }
}