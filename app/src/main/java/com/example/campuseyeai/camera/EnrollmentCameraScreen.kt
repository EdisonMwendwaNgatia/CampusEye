package com.example.campuseyeai.camera

import android.Manifest
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController

// ── Design tokens (shared language across every CampusEye AI screen) ─────
private val BgDeep = Color(0xFF060B14)
private val Surface = Color(0xFF0F1729)
private val SurfaceRaised = Color(0xFF16213B)
private val SurfaceLine = Color(0xFF1E2A47)
private val Emerald = Color(0xFF22C55E)
private val TextPrimary = Color(0xFFE5E9F0)
private val TextMuted = Color(0xFF64748B)
private val Mono = FontFamily.Monospace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentCameraScreen(
    navController: NavController,
    admissionNo: String
) {

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(false)
    }

    var faceDetected by remember {
        mutableStateOf(false)
    }

    var capturedImages by remember {
        mutableIntStateOf(0)
    }

    // Step 2: Add success dialog state
    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    // Define image names for each capture
    val imageNames = listOf(
        "center",
        "left",
        "right"
    )

    val instructions = listOf(
        "Center your face",
        "Turn face LEFT",
        "Turn face RIGHT"
    )

    var instruction by remember {
        mutableStateOf(instructions[0])
    }

    // Remember CameraManager instance
    var cameraManager by remember {
        mutableStateOf<CameraManager?>(null)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasPermission = granted

        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val reticleColor = if (faceDetected) Emerald else TextMuted

    Scaffold(

        containerColor = BgDeep,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        "FACE ENROLLMENT",
                        fontFamily = Mono,
                        fontSize = 15.sp,
                        letterSpacing = 1.5.sp,
                        color = TextPrimary
                    )

                },

                navigationIcon = {

                    IconButton(
                        onClick = {

                            navController.popBackStack()

                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Emerald
                        )

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

            if (hasPermission) {

                AndroidView(

                    modifier = Modifier.fillMaxSize(),

                    factory = { ctx ->

                        val previewView = PreviewView(ctx)

                        // Create and store CameraManager instance
                        cameraManager = CameraManager(
                            context = ctx,
                            lifecycleOwner = lifecycleOwner
                        )

                        // Start camera with face analyzer
                        cameraManager!!.startCamera(
                            previewView = previewView,
                            analyzer = FaceAnalyzer { detected ->
                                faceDetected = detected
                            }
                        )

                        previewView

                    }

                )

                // Faint scanline texture, matching the other camera screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind { drawScanlines() }
                )

                // Face-targeting reticle — turns emerald once a face is detected
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 220.dp, height = 280.dp)
                        .drawBehind { drawCornerBrackets(reticleColor) }
                )

            } else {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(color = Emerald)

                }

            }

            // ── Status / capture card ──
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(Surface, RoundedCornerShape(8.dp))
                    .border(1.dp, SurfaceLine, RoundedCornerShape(8.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Live face-lock indicator, mirrors the reticle color
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(reticleColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (faceDetected) "FACE LOCKED" else "SEARCHING",
                        color = reticleColor,
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = instruction.uppercase(),
                    color = TextPrimary,
                    fontFamily = Mono,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Capture progress — one dot per shot, same pattern as RegisterStudentScreen
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(9.dp)
                                .background(
                                    if (index < capturedImages) Emerald else SurfaceLine,
                                    CircleShape
                                )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "CAPTURED $capturedImages / 3",
                        color = TextMuted,
                        fontFamily = Mono,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = faceDetected && capturedImages < 3,
                    onClick = {
                        cameraManager?.let { manager ->
                            ImageCaptureManager(
                                context = context,
                                imageCapture = manager.imageCapture
                            ).captureImage(
                                admissionNo = admissionNo,
                                fileName = imageNames[capturedImages],
                                onSaved = { filePath ->
                                    // Success - file saved at filePath
                                    capturedImages++

                                    when (capturedImages) {
                                        1 -> instruction = instructions[1]
                                        2 -> instruction = instructions[2]
                                        // Step 2: Show dialog on completion
                                        3 -> {
                                            instruction = "Enrollment Complete"
                                            showSuccessDialog = true
                                        }
                                    }

                                    println("Image saved at: $filePath")
                                },
                                onError = { error ->
                                    error.printStackTrace()
                                    // You could show a Snackbar here
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald,
                        contentColor = BgDeep,
                        disabledContainerColor = SurfaceRaised,
                        disabledContentColor = TextMuted
                    )
                ) {
                    Text(
                        if (capturedImages == 3) "DONE" else "CAPTURE",
                        fontFamily = Mono,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        fontSize = 13.sp
                    )
                }

            }

            // Step 3: Success Dialog
            if (showSuccessDialog) {

                AlertDialog(

                    onDismissRequest = {},

                    containerColor = Surface,
                    titleContentColor = TextPrimary,
                    textContentColor = TextMuted,

                    title = {

                        Text(
                            "ENROLLMENT COMPLETE",
                            fontFamily = Mono,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )

                    },

                    text = {

                        Text(
                            "The student's three facial images have been captured successfully.",
                            fontFamily = Mono,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                    },

                    confirmButton = {

                        Button(

                            onClick = {

                                // Step 1: Return result to previous screen
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("capturedImages", 3)

                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("imagesCaptured", true)

                                navController.popBackStack()

                            },

                            shape = RoundedCornerShape(4.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Emerald,
                                contentColor = BgDeep
                            )

                        ) {

                            Text(
                                "CONTINUE",
                                fontFamily = Mono,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                fontSize = 13.sp
                            )

                        }

                    }

                )

            }

        }

    }

}

/** Viewfinder-style corner brackets — same motif as CameraScreen / RegisterStudentScreen. */
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

/** Faint horizontal scanlines, matching CameraScreen's texture. */
private fun DrawScope.drawScanlines() {
    val lineColor = Color.White.copy(alpha = 0.02f)
    var y = 0f
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += 4f
    }
}