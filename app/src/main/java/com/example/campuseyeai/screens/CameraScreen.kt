package com.example.campuseyeai.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.campuseyeai.aiAnalyzer.FaceRecognizer
import com.example.campuseyeai.aiAnalyzer.RecognitionResult
import com.example.campuseyeai.cameraAnalyzer.CameraAnalyzer
import com.example.campuseyeai.cameraAnalyzer.CameraManager
import com.example.campuseyeai.repository.StudentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    studentRepository: StudentRepository
) {

    var permissionGranted by remember { mutableStateOf(false) }

    // UI State
    var recognitionStatus by remember { mutableStateOf("Waiting...") }
    var recognitionSubtext by remember { mutableStateOf("Looking for face") }
    var studentName by remember { mutableStateOf<String?>(null) }
    var studentAdmissionNo by remember { mutableStateOf<String?>(null) }
    var confidence by remember { mutableStateOf(0f) }
    var isRecognized by remember { mutableStateOf(false) }
    var showResultCard by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Initialize FaceRecognizer
    val faceRecognizer = remember {
        FaceRecognizer(
            context = context,
            repository = studentRepository
        )
    }

    // Initialize CameraManager and CameraAnalyzer
    val cameraManager = remember {
        CameraManager(context)
    }

    // Initialize CameraAnalyzer with callbacks
    val cameraAnalyzer = remember {
        CameraAnalyzer(
            recognizer = faceRecognizer,
            onRecognition = { result ->
                scope.launch {
                    // Handle recognition result
                    isProcessing = false

                    if (result.recognized) {
                        // Student recognized
                        isRecognized = true
                        studentName = result.studentName
                        studentAdmissionNo = result.admissionNo
                        confidence = result.similarity
                        recognitionStatus = "✅ Recognized!"
                        recognitionSubtext = "${result.studentName} (${result.admissionNo})"
                        showResultCard = true

                        Log.d("CameraScreen", "Recognized: ${result.studentName} with confidence ${result.similarity}")

                        // Hide result card after 3 seconds
                        delay(3000)
                        showResultCard = false
                    } else {
                        // Not recognized
                        isRecognized = false
                        studentName = null
                        studentAdmissionNo = null
                        confidence = result.similarity
                        recognitionStatus = "❌ Not Recognized"
                        recognitionSubtext = "Unknown person (${(result.similarity * 100).toInt()}% match)"
                        showResultCard = true

                        Log.d("CameraScreen", "Not recognized, similarity: ${result.similarity}")

                        // Hide result card after 2 seconds
                        delay(2000)
                        showResultCard = false
                    }
                }
            },
            onError = { error ->
                scope.launch {
                    // Handle error
                    isProcessing = false
                    recognitionStatus = "⚠️ Error"
                    recognitionSubtext = error.message ?: "Unknown error"
                    Log.e("CameraScreen", "Error: ${error.message}", error)

                    // Reset after 2 seconds
                    delay(2000)
                    recognitionStatus = "Waiting..."
                    recognitionSubtext = "Looking for face"
                }
            }
        )
    }

    // Dispose resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            faceRecognizer.close()
            cameraAnalyzer.shutdown()
            cameraManager.shutdown()
        }
    }

    // Permission launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        permissionGranted = it
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Face Recognition") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (permissionGranted) {
                // Camera Preview
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        val previewView = PreviewView(context)

                        cameraManager.startCamera(
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            analyzer = cameraAnalyzer
                        )

                        previewView
                    }
                )

                // Status Card at bottom
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            when {
                                recognitionStatus.contains("Recognized") -> Icons.Default.CheckCircle
                                recognitionStatus.contains("Error") -> Icons.Default.Error
                                else -> Icons.Default.Person
                            },
                            null,
                            tint = when {
                                recognitionStatus.contains("Recognized") -> Color.Green
                                recognitionStatus.contains("Error") -> Color.Red
                                else -> Color.Yellow
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                recognitionStatus,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                recognitionSubtext,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Recognition Result Card (overlay)
                if (showResultCard) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp)
                            .fillMaxWidth(0.9f)
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isRecognized)
                                Color(0xFF4CAF50) else
                                Color(0xFFFF9800)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (isRecognized) {
                                Text(
                                    "✅ ${studentName ?: "Student"}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    "Admission: ${studentAdmissionNo ?: "N/A"}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Confidence: ${(confidence * 100).toInt()}%",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                Text(
                                    "❌ Unknown Person",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    "Match: ${(confidence * 100).toInt()}%",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Processing indicator
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(50))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(36.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Camera Permission Required")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { launcher.launch(Manifest.permission.CAMERA) }
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
        }
    }
}