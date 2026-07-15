package com.example.campuseyeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.campuseyeai.navigation.AppNavigation
import com.example.campuseyeai.ui.theme.CampusEyeAITheme
import com.example.campuseyeai.di.AppContainer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize your dependency container
        AppContainer.initialize(applicationContext)

        setContent {
            CampusEyeAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(AppContainer.repository)

                }
            }
        }
    }
}
