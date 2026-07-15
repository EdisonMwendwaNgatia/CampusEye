package com.example.campuseyeai.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campuseyeai.components.CameraCard
import com.example.campuseyeai.ui.theme.*

data class CameraItem(
    val name: String,
    val location: String,
    val route: String,
    val status: String = "ONLINE"
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
            "camera",
            status = "OFFLINE"
        ),
        CameraItem(
            "Library Camera",
            "School Library",
            "camera"
        ),
        CameraItem(
            "ICT Lab Camera",
            "Computer Lab",
            "camera",
            status = "OFFLINE"
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

    ConsoleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(ConsoleTheme.SpaceMD)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ConsoleTitle(text = "CAMPUSEYE AI")
                    ConsoleSubtitle(text = "SMART SCHOOL SURVEILLANCE")
                }
            }

            Spacer(modifier = Modifier.height(ConsoleTheme.SpaceMD))
            ConsoleDivider()
            Spacer(modifier = Modifier.height(ConsoleTheme.SpaceMD))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(ConsoleTheme.SpaceMD),
                horizontalArrangement = Arrangement.spacedBy(ConsoleTheme.SpaceMD),
                modifier = Modifier.weight(1f)
            ) {

                items(cameras) { camera ->

                    CameraCard(
                        title = camera.name,
                        location = camera.location,
                        status = camera.status
                    ) {

                        navController.navigate(camera.route)

                    }

                }

            }

        }
    }

}