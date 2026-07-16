package com.example.campuseyeai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.campuseyeai.di.AppContainer
import com.example.campuseyeai.viewmodel.RegisterStudentViewModel
import com.example.campuseyeai.viewmodel.RegisterStudentViewModelFactory
import java.io.File

// ── Design tokens (shared language with LoginScreen / CameraScreen / StudentsScreen) ──
private val BgDeep = Color(0xFF060B14)
private val Surface = Color(0xFF0F1729)
private val SurfaceRaised = Color(0xFF16213B)
private val SurfaceLine = Color(0xFF1E2A47)
private val Emerald = Color(0xFF22C55E)
private val Alert = Color(0xFFEF4444)
private val TextPrimary = Color(0xFFE5E9F0)
private val TextMuted = Color(0xFF64748B)
private val Mono = FontFamily.Monospace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStudentScreen(
    navController: NavController
) {

    val context = LocalContext.current

    var admission by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }

    // Track how many images have been captured
    var capturedImages by remember { mutableIntStateOf(0) }

    // Step 4: Observe the returned values from camera
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow("capturedImages", 0)
            ?.collect { count ->
                capturedImages = count
            }
    }

    // Step 5: Create the ViewModel
    val viewModel: RegisterStudentViewModel = viewModel(
        factory = RegisterStudentViewModelFactory(
            AppContainer.repository
        )
    )

    // Step 7: Observe the ViewModel state
    val isSaving by viewModel.isSaving.collectAsState()
    val message by viewModel.message.collectAsState()
    val registrationSuccess by viewModel.registrationSuccess.collectAsState()

    // Navigate when registration is successful
    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            navController.navigate("students") {
                popUpTo("register") {
                    inclusive = true
                }
            }
            viewModel.resetRegistrationSuccess()
        }
    }

    Scaffold(
        containerColor = BgDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NEW ENROLLMENT",
                        fontFamily = Mono,
                        fontSize = 15.sp,
                        letterSpacing = 1.5.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Emerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep)
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            FieldLabel("ADMISSION NUMBER")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = admission,
                onValueChange = { admission = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = consoleFieldColors(),
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(18.dp))

            FieldLabel("FULL NAME")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullname,
                onValueChange = { fullname = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = consoleFieldColors(),
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(18.dp))

            FieldLabel("CLASS")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = consoleFieldColors(),
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Face capture frame — reticle motif, matching CameraScreen/StudentCard ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Surface, RoundedCornerShape(4.dp))
                    .border(1.dp, SurfaceLine, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(140.dp)
                        .drawBehind {
                            drawCornerBrackets(
                                if (capturedImages == 3) Emerald else TextMuted
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (capturedImages == 3) Emerald else TextMuted,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Capture progress — 3 face angles, one dot per shot ──
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
                    "$capturedImages/3 FACE IMAGES CAPTURED",
                    color = if (capturedImages == 3) Emerald else TextMuted,
                    fontFamily = Mono,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    // Navigate to camera with admission number
                    navController.navigate("enroll_camera/$admission")
                },
                enabled = admission.isNotBlank() && !isSaving,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Emerald,
                    contentColor = BgDeep,
                    disabledContainerColor = SurfaceRaised,
                    disabledContentColor = TextMuted
                )
            ) {
                Text(
                    "CAPTURE FACE",
                    fontFamily = Mono,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Save Student button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = admission.isNotBlank() &&
                        fullname.isNotBlank() &&
                        className.isNotBlank() &&
                        capturedImages == 3 &&
                        !isSaving,
                onClick = {
                    // Get the absolute path for the image folder
                    val imageFolderPath = File(
                        context.filesDir,
                        "students/$admission"
                    ).absolutePath

                    viewModel.registerStudent(
                        admissionNo = admission,
                        fullName = fullname,
                        className = className,
                        imageFolder = imageFolderPath
                    )
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
                    "SAVE STUDENT",
                    fontFamily = Mono,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    fontSize = 13.sp
                )
            }

            // Step 7: Display feedback below the Save button
            Spacer(modifier = Modifier.height(18.dp))

            if (isSaving) {
                CircularProgressIndicator(color = Emerald)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("successfully")) Emerald else Alert,
                    fontFamily = Mono,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Add spacing at the bottom
            Spacer(modifier = Modifier.height(20.dp))

        }

    }

}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        color = TextMuted,
        fontFamily = Mono,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun consoleFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    disabledTextColor = TextMuted,
    focusedBorderColor = Emerald,
    unfocusedBorderColor = SurfaceLine,
    disabledBorderColor = SurfaceLine,
    cursorColor = Emerald,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent
)

/** Viewfinder-style corner brackets — same motif as CameraScreen/StudentCard. */
private fun DrawScope.drawCornerBrackets(
    color: Color,
    length: Float = 22f,
    strokeWidth: Float = 2.5f
) {
    val w = size.width
    val h = size.height

    drawLine(color, Offset(0f, 0f), Offset(length, 0f), strokeWidth)
    drawLine(color, Offset(0f, 0f), Offset(0f, length), strokeWidth)
    drawLine(color, Offset(w, 0f), Offset(w - length, 0f), strokeWidth)
    drawLine(color, Offset(w, 0f), Offset(w, length), strokeWidth)
    drawLine(color, Offset(0f, h), Offset(length, h), strokeWidth)
    drawLine(color, Offset(0f, h), Offset(0f, h - length), strokeWidth)
    drawLine(color, Offset(w, h), Offset(w - length, h), strokeWidth)
    drawLine(color, Offset(w, h), Offset(w, h - length), strokeWidth)
}