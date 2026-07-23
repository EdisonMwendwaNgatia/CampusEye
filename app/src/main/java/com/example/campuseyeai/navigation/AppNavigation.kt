package com.example.campuseyeai.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.campuseyeai.screens.DashboardScreen
import com.example.campuseyeai.screens.LoginScreen
import com.example.campuseyeai.screens.CameraScreen
import com.example.campuseyeai.screens.RegisterStudentScreen
import com.example.campuseyeai.screens.StudentsScreen
import com.example.campuseyeai.camera.EnrollmentCameraScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.campuseyeai.repository.StudentRepository

@Composable
fun AppNavigation(studentRepository: StudentRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("camera") {
            CameraScreen(navController, studentRepository)
        }

        composable("register") {
            RegisterStudentScreen(navController)
        }

        composable("register_visitor") {
            RegisterStudentScreen(navController, isVisitorMode = true)
        }

        composable("students") {
            StudentsScreen(navController)
        }

        composable("visitors") {
            StudentsScreen(navController, isVisitorMode = true)
        }

        composable(
            route = "enroll_camera/{admissionNo}"
        ) { backStackEntry ->
            val admissionNo = backStackEntry.arguments?.getString("admissionNo") ?: ""
            EnrollmentCameraScreen(
                navController = navController,
                admissionNo = admissionNo
            )
        }
    }
}
