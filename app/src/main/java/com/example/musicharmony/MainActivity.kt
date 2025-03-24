package com.example.musicharmony

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.musicharmony.data.AnalysisResult
import com.example.musicharmony.data.AppDatabase
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AudioAnalysisScreen(audioAnalyzer = AudioAnalyzer(this))
                }
            }
        }
    }
}

@Composable
fun AudioAnalysisScreen(audioAnalyzer: AudioAnalyzer) {
    var analysisResult by remember { mutableStateOf("No analysis yet") }
    var harmonyResult by remember { mutableStateOf("No harmony generated yet") }
    val harmonyGenerator = remember { HarmonyGenerator() }

    // Получаем Context в composable-контексте
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Инициализируем базу данных и DAO
    val database = remember { AppDatabase.getDatabase(context) }
    val dao = database.analysisResultDao()

    // Получаем все сохранённые результаты
    val savedResults by dao.getAllResults().collectAsState(initial = emptyList())

    // Определяем, какое разрешение запрашивать в зависимости от версии API
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Лаунчер для выбора аудиофайла
    val pickAudioLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        Log.d("AudioAnalysisScreen", "pickAudioLauncher result: uri=$uri")
        uri?.let {
            val filePath = getFilePathFromUri(context, it)
            Log.d("AudioAnalysisScreen", "File path: $filePath")
            filePath?.let { path ->
                val audioFile = File(path)
                if (audioFile.exists()) {
                    analysisResult = audioAnalyzer.analyzeAudio(audioFile)
                    Log.d("AudioAnalysisScreen", "Analysis result: $analysisResult")

                    // Извлекаем тональность из результата анализа (пока это заглушка)
                    val key = if (analysisResult.contains("Detected key: (.*)".toRegex())) {
                        "C Major" // Пока фиксированная тональность
                    } else {
                        "Unknown Key"
                    }

                    // Генерируем гармонию
                    harmonyResult = harmonyGenerator.generateHarmony(key)

                    // Сохраняем результат в базу данных
                    coroutineScope.launch {
                        dao.insert(
                            AnalysisResult(
                                filePath = path,
                                analysis = analysisResult,
                                harmony = harmonyResult
                            )
                        )
                    }
                } else {
                    analysisResult = "Audio file does not exist: $path"
                    Log.e("AudioAnalysisScreen", "Audio file does not exist: $path")
                }
            } ?: run {
                analysisResult = "Failed to get file path from URI"
                Log.e("AudioAnalysisScreen", "Failed to get file path from URI")
            }
        } ?: run {
            analysisResult = "No file selected"
            Log.d("AudioAnalysisScreen", "No file selected")
        }
    }

    // Лаунчер для запроса разрешения
    val requestPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("AudioAnalysisScreen", "Permission request result: isGranted=$isGranted")
        if (isGranted) {
            Log.d("AudioAnalysisScreen", "Permission granted, launching pickAudioLauncher")
            pickAudioLauncher.launch("*/*")
        } else {
            analysisResult = "Storage permission denied"
            harmonyResult = "Cannot generate harmony without file access"
            Log.e("AudioAnalysisScreen", "Storage permission denied")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Кнопка "Pick Audio File" в центре экрана
        Button(
            onClick = {
                Log.d("AudioAnalysisScreen", "Pick Audio File button clicked")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("AudioAnalysisScreen", "API >= 23, checking permission: $permissionToRequest")
                    if (ContextCompat.checkSelfPermission(context, permissionToRequest) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Log.d("AudioAnalysisScreen", "Permission granted, launching pickAudioLauncher")
                        pickAudioLauncher.launch("*/*")
                    } else {
                        Log.d("AudioAnalysisScreen", "Permission not granted, launching requestPermissionLauncher")
                        requestPermissionLauncher.launch(permissionToRequest)
                    }
                } else {
                    Log.d("AudioAnalysisScreen", "API < 23, launching pickAudioLauncher directly")
                    pickAudioLauncher.launch("*/*")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.Center) // Центрируем кнопку в Box
        ) {
            Text("Pick Audio File")
        }

        // Содержимое (результаты анализа и история) внизу экрана
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart) // Размещаем Column внизу
                .heightIn(max = 300.dp) // Ограничиваем высоту, чтобы не перекрывать кнопку
        ) {
            Text(
                text = "Latest Analysis Result:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = analysisResult,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Latest Harmony Result:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = harmonyResult,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "History of Analyses:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // LazyColumn занимает оставшееся пространство в Column
            ) {
                items(savedResults) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "File: ${result.filePath}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Time: ${
                                    SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        Locale.getDefault()
                                    ).format(Date(result.timestamp))
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Analysis:\n${result.analysis}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Harmony:\n${result.harmony}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

// Обычная функция, а не @Composable
fun getFilePathFromUri(context: Context, uri: android.net.Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
            if (columnIndex >= 0) {
                it.getString(columnIndex)
            } else {
                null
            }
        } else {
            null
        }
    }
}