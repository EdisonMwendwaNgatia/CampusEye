package com.example.campuseyeai.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// ── Design tokens (shared language with LoginScreen) ─────────────────────
private val BgDeep = Color(0xFF060B14)
private val Surface = Color(0xFF0F1729)
private val SurfaceLine = Color(0xFF1E2A47)
private val Emerald = Color(0xFF22C55E)
private val Amber = Color(0xFFF59E0B)
private val Alert = Color(0xFFEF4444)
private val TextPrimary = Color(0xFFE5E9F0)
private val TextMuted = Color(0xFF64748B)
private val Mono = FontFamily.Monospace

private enum class ScanState { WAITING, RECOGNIZED, UNKNOWN, ERROR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    studentRepository: StudentRepository
) {

    var permissionGranted by remember { mutableStateOf(false) }

    // UI State
    var scanState by remember { mutableStateOf(ScanState.WAITING) }
    var recognitionStatus by remember { mutableStateOf("SCANNING") }
    var recognitionSubtext by remember { mutableStateOf("Looking for a face") }
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
                        scanState = ScanState.RECOGNIZED
                        recognitionStatus = "RECOGNIZED"
                        recognitionSubtext = "${result.studentName} (${result.admissionNo})"
                        showResultCard = true

                        Log.d("CameraScreen", "Recognized: ${result.studentName} with confidence ${result.similarity}")

                        // Hide result card after 3 seconds
                        delay(3000)
                        showResultCard = false
                        scanState = ScanState.WAITING
                        recognitionStatus = "SCANNING"
                        recognitionSubtext = "Looking for a face"
                    } else {
                        // Not recognized
                        isRecognized = false
                        studentName = null
                        studentAdmissionNo = null
                        confidence = result.similarity
                        scanState = ScanState.UNKNOWN
                        recognitionStatus = "NOT RECOGNIZED"
                        recognitionSubtext = "Unknown person — ${(result.similarity * 100).toInt()}% match"
                        showResultCard = true

                        Log.d("CameraScreen", "Not recognized, similarity: ${result.similarity}")

                        // Hide result card after 2 seconds
                        delay(2000)
                        showResultCard = false
                        scanState = ScanState.WAITING
                        recognitionStatus = "SCANNING"
                        recognitionSubtext = "Looking for a face"
                    }
                }
            },
            onError = { error ->
                scope.launch {
                    // Handle error
                    isProcessing = false
                    scanState = ScanState.ERROR
                    recognitionStatus = "ERROR"
                    recognitionSubtext = error.message ?: "Unknown error"
                    Log.e("CameraScreen", "Error: ${error.message}", error)

                    // Reset after 2 seconds
                    delay(2000)
                    scanState = ScanState.WAITING
                    recognitionStatus = "SCANNING"
                    recognitionSubtext = "Looking for a face"
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

    val stateColor = when (scanState) {
        ScanState.RECOGNIZED -> Emerald
        ScanState.UNKNOWN -> Amber
        ScanState.ERROR -> Alert
        ScanState.WAITING -> TextMuted
    }

    Scaffold(
        containerColor = BgDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FACE RECOGNITION",
                        fontFamily = Mono,
                        fontSize = 15.sp,
                        letterSpacing = 1.5.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Emerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep)
                .padding(padding)
        ) {
            if (permissionGranted) {
                // Camera Preview
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)

                        cameraManager.startCamera(
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            analyzer = cameraAnalyzer
                        )

                        previewView
                    }
                )

                // Faint scanline texture over the live feed, for continuity with LoginScreen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind { drawScanlines() }
                )

                // Face-targeting reticle — where to line up a face
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 220.dp, height = 280.dp)
                        .drawBehind { drawCornerBrackets(stateColor.copy(alpha = 0.9f)) }
                )

                // Status readout at bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(Surface, RoundedCornerShape(8.dp))
                        .border(1.dp, SurfaceLine, RoundedCornerShape(8.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        when (scanState) {
                            ScanState.RECOGNIZED -> Icons.Default.CheckCircle
                            ScanState.ERROR, ScanState.UNKNOWN -> Icons.Default.Error
                            ScanState.WAITING -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = stateColor
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            recognitionStatus,
                            color = TextPrimary,
                            fontFamily = Mono,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            recognitionSubtext,
                            color = TextMuted,
                            fontFamily = Mono,
                            fontSize = 11.sp
                        )
                    }
                }

                // Recognition Result Card (overlay)
                if (showResultCard) {
                    val cardColor = if (isRecognized) Emerald else Amber
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                            .fillMaxWidth(0.9f)
                            .background(Surface, RoundedCornerShape(8.dp))
                            .border(1.dp, cardColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isRecognized) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = cardColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                if (isRecognized) (studentName ?: "Student") else "Unknown person",
                                color = TextPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (isRecognized) {
                            Text(
                                "ADMISSION NO  ${studentAdmissionNo ?: "N/A"}",
                                color = TextMuted,
                                fontFamily = Mono,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            "CONFIDENCE  ${(confidence * 100).toInt()}%",
                            color = cardColor,
                            fontFamily = Mono,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Processing indicator
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .background(Surface, CircleShape)
                            .border(1.dp, SurfaceLine, CircleShape)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(36.dp),
                            color = Emerald,
                            strokeWidth = 3.dp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Emerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "CAMERA ACCESS REQUIRED",
                            color = TextPrimary,
                            fontFamily = Mono,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Needed to run face recognition",
                            color = TextMuted,
                            fontFamily = Mono,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { launcher.launch(Manifest.permission.CAMERA) },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Emerald,
                                contentColor = BgDeep
                            )
                        ) {
                            Text(
                                "GRANT PERMISSION",
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Signature visual details (shared language with LoginScreen) ──────────

/** Viewfinder-style corner brackets — reused here as the face-targeting reticle. */
private fun DrawScope.drawCornerBrackets(
    color: Color,
    length: Float = 32f,
    strokeWidth: Float = 3f,
    inset: Float = 2f
) {
    val w = size.width
    val h = size.height

    drawLine(color, Offset(inset, inset), Offset(inset + length, inset), strokeWidth)
    drawLine(color, Offset(inset, inset), Offset(inset, inset + length), strokeWidth)
    drawLine(color, Offset(w - inset, inset), Offset(w - inset - length, inset), strokeWidth)
    drawLine(color, Offset(w - inset, inset), Offset(w - inset, inset + length), strokeWidth)
    drawLine(color, Offset(inset, h - inset), Offset(inset + length, h - inset), strokeWidth)
    drawLine(color, Offset(inset, h - inset), Offset(inset, h - inset - length), strokeWidth)
    drawLine(color, Offset(w - inset, h - inset), Offset(w - inset - length, h - inset), strokeWidth)
    drawLine(color, Offset(w - inset, h - inset), Offset(w - inset, h - inset - length), strokeWidth)
}

/** Faint horizontal scanlines, matching LoginScreen's texture. */
private fun DrawScope.drawScanlines() {
    val lineColor = Color.White.copy(alpha = 0.02f)
    var y = 0f
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += 4f
    }
}