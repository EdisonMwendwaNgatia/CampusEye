package com.example.campuseyeai.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Design tokens ────────────────────────────────────────────────────────
private val BgDeep = Color(0xFF060B14)
private val Surface = Color(0xFF0F1729)
private val SurfaceLine = Color(0xFF1E2A47)
private val Emerald = Color(0xFF22C55E)
private val TextPrimary = Color(0xFFE5E9F0)
private val TextMuted = Color(0xFF64748B)
private val Alert = Color(0xFFEF4444)
private val Mono = FontFamily.Monospace

private fun currentTime(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

@Composable
fun LoginScreen(
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    var clock by remember { mutableStateOf(currentTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            clock = currentTime()
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .drawBehind { drawScanlines() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .padding(20.dp)
                .drawBehind { drawCornerBrackets(Emerald.copy(alpha = 0.85f)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Surface, RoundedCornerShape(4.dp))
                    .border(1.dp, SurfaceLine, RoundedCornerShape(4.dp))
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {

                // ── Brand mark ──────────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Emerald,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "CAMPUSEYE AI",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "SCHOOL SURVEILLANCE SYSTEM",
                    color = TextMuted,
                    fontFamily = Mono,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ── Status readout ───────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Emerald.copy(alpha = pulseAlpha), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SYSTEM ONLINE",
                        color = Emerald,
                        fontFamily = Mono,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        clock,
                        color = TextMuted,
                        fontFamily = Mono,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = SurfaceLine, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // ── Operator ID ───────────────────────────────────────
                FieldLabel("OPERATOR ID")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = consoleFieldColors()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ── Access code ───────────────────────────────────────
                FieldLabel("ACCESS CODE")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    colors = consoleFieldColors()
                )

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "! $error",
                        color = Alert,
                        fontFamily = Mono,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // ── Authenticate ───────────────────────────────────────
                Button(
                    onClick = {
                        if (username == "admin" && password == "1234") {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            error = "Invalid operator ID or access code"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald,
                        contentColor = BgDeep
                    )
                ) {
                    Text(
                        "AUTHENTICATE",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                }
            }
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
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun consoleFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = Emerald,
    unfocusedBorderColor = SurfaceLine,
    cursorColor = Emerald,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)

// ── Signature visual details ──────────────────────────────────────────────

/** Viewfinder-style corner brackets, framing the card like a camera reticle. */
private fun DrawScope.drawCornerBrackets(
    color: Color,
    length: Float = 26f,
    strokeWidth: Float = 3f,
    inset: Float = 2f
) {
    val w = size.width
    val h = size.height

    // top-left
    drawLine(color, Offset(inset, inset), Offset(inset + length, inset), strokeWidth)
    drawLine(color, Offset(inset, inset), Offset(inset, inset + length), strokeWidth)
    // top-right
    drawLine(color, Offset(w - inset, inset), Offset(w - inset - length, inset), strokeWidth)
    drawLine(color, Offset(w - inset, inset), Offset(w - inset, inset + length), strokeWidth)
    // bottom-left
    drawLine(color, Offset(inset, h - inset), Offset(inset + length, h - inset), strokeWidth)
    drawLine(color, Offset(inset, h - inset), Offset(inset, h - inset - length), strokeWidth)
    // bottom-right
    drawLine(color, Offset(w - inset, h - inset), Offset(w - inset - length, h - inset), strokeWidth)
    drawLine(color, Offset(w - inset, h - inset), Offset(w - inset, h - inset - length), strokeWidth)
}

/** Faint horizontal scanlines across the whole background for CRT/monitor texture. */
private fun DrawScope.drawScanlines() {
    val lineColor = Color.White.copy(alpha = 0.015f)
    var y = 0f
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += 4f
    }
}