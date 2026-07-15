package com.example.campuseyeai.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campuseyeai.components.CameraCard

data class CameraItem(
    val name: String,
    val location: String,
    val route: String
)

@Composable
fun DashboardScreen(
    navController: NavController
) {

    val cameras = listOf(

        CameraItem(
            "Register Student",
            "Enroll New Student",
            "register"
        ),

        CameraItem(
            "Students",
            "Registered Students",
            "students"
        ),

        CameraItem(
            "Hall Camera",
            "Main Hall",
            "camera"
        ),

        CameraItem(
            "Gate Camera",
            "Main Entrance",
            "camera"
        ),

        CameraItem(
            "Library Camera",
            "School Library",
            "camera"
        ),

        CameraItem(
            "ICT Lab Camera",
            "Computer Lab",
            "camera"
        ),

        CameraItem(
            "Cafeteria Camera",
            "Dining Hall",
            "camera"
        ),

        CameraItem(
            "Parking Camera",
            "School Parking",
            "camera"
        )

    )

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "CampusEye AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Smart School Surveillance",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                items(cameras) { camera ->

                    CameraCard(
                        title = camera.name,
                        location = camera.location
                    ) {

                        navController.navigate(camera.route)

                    }

                }

            }

        }

    }

}