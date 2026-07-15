package com.example.campuseyeai.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.campuseyeai.components.StudentCard
import com.example.campuseyeai.di.AppContainer
import com.example.campuseyeai.viewmodel.StudentsViewModel
import com.example.campuseyeai.viewmodel.StudentsViewModelFactory

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



    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Registered Students")

                },

                navigationIcon = {

                    IconButton(
                        onClick = {

                            navController.popBackStack()

                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            null
                        )

                    }

                }

            )

        }

    ) { padding ->

        if (students.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                Text(
                    "No students registered.",
                    modifier = Modifier.align(
                        androidx.compose.ui.Alignment.Center
                    )
                )

            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
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

                        }

                    )

                }

            }

        }

    }

}