package com.example.campuseyeai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.campuseyeai.components.StudentCard
import com.example.campuseyeai.database.Student
import com.example.campuseyeai.di.AppContainer
import com.example.campuseyeai.viewmodel.StudentsViewModel
import com.example.campuseyeai.viewmodel.StudentsViewModelFactory

// ── Design tokens (shared language with LoginScreen / CameraScreen) ──────
private val BgDeep = Color(0xFF060B14)
private val Surface = Color(0xFF0F1729)
private val TextPrimary = Color(0xFFE5E9F0)
private val TextMuted = Color(0xFF64748B)
private val Emerald = Color(0xFF22C55E)
private val Mono = FontFamily.Monospace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: StudentsViewModel = viewModel(
        factory = StudentsViewModelFactory(
            AppContainer.repository
        )
    )
    val students by viewModel.students.collectAsState(
        initial = emptyList()
    )

    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Delete Student", fontFamily = Mono) },
            text = { Text("Are you sure you want to delete ${studentToDelete?.fullName}?", fontFamily = Mono) },
            confirmButton = {
                TextButton(
                    onClick = {
                        studentToDelete?.let { viewModel.deleteStudent(it.admissionNo) }
                        studentToDelete = null
                    }
                ) {
                    Text("DELETE", color = Color.Red, fontFamily = Mono)
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("CANCEL", fontFamily = Mono)
                }
            },
            containerColor = Surface,
            titleContentColor = TextPrimary,
            textContentColor = TextMuted
        )
    }

    Scaffold(
        containerColor = BgDeep,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "STUDENT REGISTRY",
                            fontFamily = Mono,
                            fontSize = 15.sp,
                            letterSpacing = 1.5.sp,
                            color = TextPrimary
                        )
                        if (students.isNotEmpty()) {
                            Text(
                                "${students.size} ENROLLED",
                                fontFamily = Mono,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = TextMuted
                            )
                        }
                    }
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
        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgDeep)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "NO STUDENTS REGISTERED",
                        color = TextPrimary,
                        fontFamily = Mono,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Enroll a student to begin building the registry",
                        color = TextMuted,
                        fontFamily = Mono,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgDeep)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(students) { student ->
                    StudentCard(
                        student = student,
                        onGenerateEmbedding = {
                            viewModel.generateEmbeddings(
                                context = context,
                                student = student
                            )
                        },
                        onDelete = {
                            studentToDelete = student
                        }
                    )
                }
            }
        }
    }
}