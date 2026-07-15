package com.example.campuseyeai.screens

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.campuseyeai.di.AppContainer
import com.example.campuseyeai.viewmodel.RegisterStudentViewModel
import com.example.campuseyeai.viewmodel.RegisterStudentViewModelFactory
import java.io.File

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

        topBar = {

            TopAppBar(

                title = {

                    Text("Register Student")

                },

                navigationIcon = {

                    IconButton(
                        onClick = {

                            navController.popBackStack()

                        }
                    ) {

                        Icon(Icons.Default.ArrowBack, null)

                    }

                }

            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            OutlinedTextField(
                value = admission,
                onValueChange = {
                    admission = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Admission Number")
                },
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullname,
                onValueChange = {
                    fullname = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Full Name")
                },
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = className,
                onValueChange = {
                    className = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Class")
                },
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            // Show captured images status
            if (capturedImages > 0) {
                Text(
                    text = "📸 $capturedImages/3 face images captured",
                    color = if (capturedImages == 3)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // Navigate to camera with admission number
                    navController.navigate("enroll_camera/$admission")
                },
                enabled = admission.isNotBlank() && !isSaving
            ) {

                Text("Capture Face")

            }

            Spacer(modifier = Modifier.height(15.dp))

            // Save Student button
            Button(
                modifier = Modifier.fillMaxWidth(),
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
                }
            ) {

                Text("Save Student")

            }

            // Step 7: Display feedback below the Save button
            Spacer(modifier = Modifier.height(16.dp))

            if (isSaving) {
                CircularProgressIndicator()
            }

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("successfully"))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Add spacing at the bottom
            Spacer(modifier = Modifier.height(20.dp))

        }

    }

}