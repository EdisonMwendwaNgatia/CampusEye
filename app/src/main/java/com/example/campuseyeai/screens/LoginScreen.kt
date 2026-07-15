package com.example.campuseyeai.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campuseyeai.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun currentTime(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var clock by remember { mutableStateOf(currentTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            clock = currentTime()
        }
    }

    ConsoleBackground {
        ConsoleCenteredContent {
            ConsoleCard {
                // ── Header ──────────────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ConsoleTheme.Emerald,
                        modifier = Modifier.size(ConsoleTheme.IconSize)
                    )
                    Spacer(modifier = Modifier.width(ConsoleTheme.SpaceSM))
                    ConsoleTitle(text = "CAMPUSEYE AI")
                }

                Spacer(modifier = Modifier.height(ConsoleTheme.SpaceXS))
                ConsoleSubtitle(text = "SCHOOL SURVEILLANCE SYSTEM")
                Spacer(modifier = Modifier.height(ConsoleTheme.SpaceLG))

                // ── Status ──────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConsoleStatusText(text = "SYSTEM ONLINE")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        clock,
                        color = ConsoleTheme.TextMuted,
                        fontFamily = ConsoleTheme.Mono,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(ConsoleTheme.SpaceMD))
                ConsoleDivider()
                Spacer(modifier = Modifier.height(ConsoleTheme.SpaceLG))

                // ── Form ────────────────────────────────────────────────
                ConsoleTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "OPERATOR ID"
                )

                Spacer(modifier = Modifier.height(ConsoleTheme.SpaceMD))

                ConsoleTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "ACCESS CODE",
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword }
                )

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(ConsoleTheme.SpaceSM))
                    Text(
                        "! $error",
                        color = ConsoleTheme.Alert,
                        fontFamily = ConsoleTheme.Mono,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(ConsoleTheme.SpaceLG))

                // ── Actions ─────────────────────────────────────────────
                ConsoleButton(
                    onClick = {
                        isLoading = true
                        // Simulate network request
                        // In real app, you'd call your auth service here
                        if (username == "admin" && password == "1234") {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            error = "Invalid operator ID or access code"
                        }
                        isLoading = false
                    },
                    text = "AUTHENTICATE",
                    isLoading = isLoading
                )
            }
        }
    }
}