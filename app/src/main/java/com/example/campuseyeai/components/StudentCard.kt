package com.example.campuseyeai.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuseyeai.database.Student
import java.io.File
import android.graphics.BitmapFactory

// ── Design tokens (shared language with LoginScreen / CameraScreen) ──────
private val Surface = Color(0xFF0F1729)
private val SurfaceRaised = Color(0xFF16213B)
private val SurfaceLine = Color(0xFF1E2A47)
private val Emerald = Color(0xFF22C55E)
private val Amber = Color(0xFFF59E0B)
private val TextPrimary = Color(0xFFE5E9F0)
private val TextMuted = Color(0xFF64748B)
private val Mono = FontFamily.Monospace

@Composable
fun StudentCard(
    student: Student,
    onGenerateEmbedding: () -> Unit
) {
    val imageFile = File(student.imageFolder, "center.jpg")
    val generated = student.centerEmbedding.isNotBlank()
    val statusColor = if (generated) Emerald else Amber

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, SurfaceLine)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // ── Enrollment photo, framed like a recognition target ──
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .drawBehind { drawCornerTicks(statusColor.copy(alpha = 0.9f)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageFile.exists()) {
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        Image(
                            bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .background(SurfaceRaised, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        student.fullName,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "ID  ${student.admissionNo}",
                        color = TextMuted,
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .background(SurfaceRaised, RoundedCornerShape(4.dp))
                            .border(1.dp, SurfaceLine, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            student.className,
                            color = TextMuted,
                            fontFamily = Mono,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = SurfaceLine, thickness = 1.dp)
            Spacer(Modifier.height(14.dp))

            // ── Biometric enrollment status ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(statusColor, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "BIOMETRIC PROFILE",
                    color = TextMuted,
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    if (generated) "GENERATED" else "PENDING",
                    color = statusColor,
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            if (generated) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* No action needed - already generated */ },
                    enabled = false,
                    colors = ButtonDefaults.outlinedButtonColors(
                        disabledContentColor = Emerald.copy(alpha = 0.8f)
                    ),
                    border = BorderStroke(1.dp, Emerald.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "EMBEDDINGS GENERATED",
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGenerateEmbedding,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald,
                        contentColor = Color(0xFF060B14)
                    )
                ) {
                    Text(
                        "GENERATE EMBEDDINGS",
                        fontFamily = Mono,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/** Small-scale corner ticks around the enrollment photo — echoes the CameraScreen reticle. */
private fun DrawScope.drawCornerTicks(
    color: Color,
    length: Float = 12f,
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