package com.example.campuseyeai.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuseyeai.ui.theme.ConsoleTheme

@Composable
fun CameraCard(
    title: String,
    location: String,
    status: String = "ONLINE",
    onClick: () -> Unit
) {
    val isOnline = status == "ONLINE"
    val statusColor = if (isOnline) ConsoleTheme.Emerald else ConsoleTheme.Alert

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable(enabled = isOnline) { onClick() },
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(ConsoleTheme.CornerRadiusSM),
        colors = CardDefaults.cardColors(
            containerColor = ConsoleTheme.Surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOnline) ConsoleTheme.SurfaceLine else ConsoleTheme.AlertDim
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ConsoleTheme.SpaceMD),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = if (isOnline) ConsoleTheme.Emerald else ConsoleTheme.TextMuted
                )

                Spacer(modifier = Modifier.width(ConsoleTheme.SpaceSM))

                Text(
                    text = title,
                    color = if (isOnline) ConsoleTheme.TextPrimary else ConsoleTheme.TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

            }

            Text(
                text = location,
                color = ConsoleTheme.TextMuted,
                fontSize = 12.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "●",
                    color = statusColor,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.width(ConsoleTheme.SpaceXS))

                Text(
                    text = status,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

            }

        }

    }

}