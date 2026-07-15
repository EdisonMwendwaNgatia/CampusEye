package com.example.campuseyeai.camera

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController

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

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Student Face Enrollment")

                },

                navigationIcon = {

                    IconButton(
                        onClick = {

                            navController.popBackStack()

                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )

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

            if (hasPermission) {

                AndroidView(

                    modifier = Modifier.fillMaxSize(),

                    factory = { context ->

                        val previewView = PreviewView(context)

                        // Create and store CameraManager instance
                        cameraManager = CameraManager(
                            context = context,
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

            } else {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()

                }

            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(.75f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = instruction,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Captured : $capturedImages / 3",
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Button(
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
                        }
                    ) {
                        Text(
                            if (capturedImages == 3) "DONE" else "CAPTURE"
                        )
                    }

                }

            }

            // Step 3: Success Dialog
            if (showSuccessDialog) {

                AlertDialog(

                    onDismissRequest = {},

                    title = {

                        Text("Enrollment Complete")

                    },

                    text = {

                        Text(
                            "The student's three facial images have been captured successfully."
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

                            }

                        ) {

                            Text("Continue")

                        }

                    }

                )

            }

        }

    }

}