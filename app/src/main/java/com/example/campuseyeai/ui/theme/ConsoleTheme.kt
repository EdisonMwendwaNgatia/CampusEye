package com.example.campuseyeai.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared design tokens for the CampusEye AI "monitoring console" visual language.
 * Every screen pulls from here so the app reads as one system.
 */
object ConsoleTheme {
    // ── Colors ──────────────────────────────────────────────────────────
    val BgDeep = Color(0xFF060B14)
    val Surface = Color(0xFF0F1729)
    val SurfaceLine = Color(0xFF1E2A47)
    val SurfaceElevated = Color(0xFF1A2744)
    val Emerald = Color(0xFF22C55E)
    val EmeraldDim = Color(0xFF166B3A)
    val TextPrimary = Color(0xFFE5E9F0)
    val TextSecondary = Color(0xFF94A3B8)
    val TextMuted = Color(0xFF64748B)
    val Alert = Color(0xFFEF4444)
    val AlertDim = Color(0xFF7F1D1D)
    val Warning = Color(0xFFF59E0B)
    val WarningDim = Color(0xFF78350F)
    val Success = Color(0xFF22C55E)
    val Info = Color(0xFF3B82F6)
    val InfoDim = Color(0xFF1E3A5F)

    // ── Typography ─────────────────────────────────────────────────────
    val Mono: FontFamily = FontFamily.Monospace
    val SansSerif: FontFamily = FontFamily.SansSerif

    // ── Spacing ────────────────────────────────────────────────────────
    val SpaceXXS = 4.dp
    val SpaceXS = 8.dp
    val SpaceSM = 12.dp
    val SpaceMD = 16.dp
    val SpaceLG = 24.dp
    val SpaceXL = 32.dp
    val SpaceXXL = 48.dp

    val CardPaddingHorizontal = 28.dp
    val CardPaddingVertical = 32.dp

    // ── Shapes ─────────────────────────────────────────────────────────
    val CornerRadiusSM = 4.dp
    val CornerRadiusMD = 8.dp
    val CornerRadiusLG = 12.dp

    // ── Sizes ──────────────────────────────────────────────────────────
    val ButtonHeight = 48.dp
    val IconSize = 22.dp
    val DotSize = 7.dp
    val MaxCardWidth = 420.dp

    // ── Animation ──────────────────────────────────────────────────────
    val PulseDuration = 1100
    val FadeDuration = 300
    val SlideDuration = 400
}

// ── Background ──────────────────────────────────────────────────────────

/**
 * Console background with scanlines and optional gradient overlay.
 */
@Composable
fun ConsoleBackground(
    modifier: Modifier = Modifier,
    showScanlines: Boolean = true,
    gradient: Brush? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                gradient ?: Brush.verticalGradient(
                    colors = listOf(
                        ConsoleTheme.BgDeep,
                        Color(0xFF0A1628)
                    )
                )
            )
            .then(
                if (showScanlines) Modifier.drawBehind { drawScanlines() }
                else Modifier
            )
    ) {
        content()
    }
}

/**
 * Centered container for login/dialog screens.
 */
@Composable
fun ConsoleCenteredContent(
    modifier: Modifier = Modifier,
    maxWidth: Dp = ConsoleTheme.MaxCardWidth,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .padding(ConsoleTheme.SpaceMD)
        ) {
            content()
        }
    }
}

// ── Cards ──────────────────────────────────────────────────────────────

/**
 * Console card surface with corner brackets.
 */
@Composable
fun ConsoleCard(
    modifier: Modifier = Modifier,
    showBrackets: Boolean = true,
    elevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (showBrackets) {
        modifier.drawBehind { drawCornerBrackets() }
    } else {
        modifier
    }

    Surface(
        modifier = cardModifier,
        shape = RoundedCornerShape(ConsoleTheme.CornerRadiusSM),
        color = ConsoleTheme.Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleTheme.SurfaceLine),
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = ConsoleTheme.CardPaddingHorizontal,
                    vertical = ConsoleTheme.CardPaddingVertical
                ),
            content = content
        )
    }
}

/**
 * Compact card for dashboards and list items.
 */
@Composable
fun ConsoleCompactCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ConsoleTheme.CornerRadiusSM),
        color = ConsoleTheme.Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleTheme.SurfaceLine)
    ) {
        Row(
            modifier = Modifier.padding(ConsoleTheme.SpaceMD),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

// ── Typography ──────────────────────────────────────────────────────────

@Composable
fun ConsoleTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ConsoleTheme.TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

@Composable
fun ConsoleSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ConsoleTheme.TextMuted,
        fontFamily = ConsoleTheme.Mono,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        modifier = modifier
    )
}

@Composable
fun ConsoleLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ConsoleTheme.TextMuted,
        fontFamily = ConsoleTheme.Mono,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        modifier = modifier
    )
}

@Composable
fun ConsoleStatusText(
    text: String,
    isOnline: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PulsingDot(
            color = if (isOnline) ConsoleTheme.Emerald else ConsoleTheme.Alert
        )
        Spacer(modifier = Modifier.width(ConsoleTheme.SpaceXS))
        Text(
            text = text,
            color = if (isOnline) ConsoleTheme.Emerald else ConsoleTheme.Alert,
            fontFamily = ConsoleTheme.Mono,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

// ── Status Indicators ──────────────────────────────────────────────────

/**
 * Pulsing dot for live/online status indicators.
 */
@Composable
fun PulsingDot(
    color: Color = ConsoleTheme.Emerald,
    size: Dp = ConsoleTheme.DotSize,
    speed: Int = ConsoleTheme.PulseDuration
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(speed, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Box(
        modifier = Modifier
            .size(size)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

/**
 * Static status indicator (no animation).
 */
@Composable
fun StatusDot(
    color: Color = ConsoleTheme.Emerald,
    size: Dp = ConsoleTheme.DotSize
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape)
    )
}

// ── Form Components ────────────────────────────────────────────────────

/**
 * Console-style text field colors.
 */
@Composable
fun consoleFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ConsoleTheme.TextPrimary,
    unfocusedTextColor = ConsoleTheme.TextPrimary,
    focusedBorderColor = ConsoleTheme.Emerald,
    unfocusedBorderColor = ConsoleTheme.SurfaceLine,
    cursorColor = ConsoleTheme.Emerald,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedLabelColor = ConsoleTheme.TextMuted,
    unfocusedLabelColor = ConsoleTheme.TextMuted
)

/**
 * Console-style text field.
 */
@Composable
fun ConsoleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        ConsoleLabel(text = label)
        Spacer(modifier = Modifier.height(ConsoleTheme.SpaceXS))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ConsoleTheme.CornerRadiusSM),
            visualTransformation = if (isPassword && !showPassword)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (showPassword)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = ConsoleTheme.TextMuted
                        )
                    }
                }
            } else null,
            colors = consoleFieldColors()
        )
    }
}

/**
 * Console-style button.
 */
@Composable
fun ConsoleButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(ConsoleTheme.ButtonHeight),
        shape = RoundedCornerShape(ConsoleTheme.CornerRadiusSM),
        colors = ButtonDefaults.buttonColors(
            containerColor = ConsoleTheme.Emerald,
            contentColor = ConsoleTheme.BgDeep,
            disabledContainerColor = ConsoleTheme.SurfaceLine,
            disabledContentColor = ConsoleTheme.TextMuted
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ConsoleTheme.BgDeep,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Console-style danger button (for destructive actions).
 */
@Composable
fun ConsoleDangerButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ConsoleTheme.ButtonHeight),
        shape = RoundedCornerShape(ConsoleTheme.CornerRadiusSM),
        colors = ButtonDefaults.buttonColors(
            containerColor = ConsoleTheme.Alert,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            fontSize = 14.sp
        )
    }
}

// ── Dividers ────────────────────────────────────────────────────────────

@Composable
fun ConsoleDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        color = ConsoleTheme.SurfaceLine,
        thickness = 1.dp,
        modifier = modifier
    )
}

// ── Drawing Extensions ──────────────────────────────────────────────────

/**
 * Viewfinder-style corner brackets — the recurring signature element.
 */
fun DrawScope.drawCornerBrackets(
    color: Color = ConsoleTheme.Emerald,
    length: Float = 22f,
    strokeWidth: Float = 2.5f,
    inset: Float = 2f,
    alpha: Float = 0.85f
) {
    val w = size.width
    val h = size.height
    val drawColor = color.copy(alpha = alpha)

    // Top-left
    drawLine(drawColor, Offset(inset, inset), Offset(inset + length, inset), strokeWidth)
    drawLine(drawColor, Offset(inset, inset), Offset(inset, inset + length), strokeWidth)

    // Top-right
    drawLine(drawColor, Offset(w - inset, inset), Offset(w - inset - length, inset), strokeWidth)
    drawLine(drawColor, Offset(w - inset, inset), Offset(w - inset, inset + length), strokeWidth)

    // Bottom-left
    drawLine(drawColor, Offset(inset, h - inset), Offset(inset + length, h - inset), strokeWidth)
    drawLine(drawColor, Offset(inset, h - inset), Offset(inset, h - inset - length), strokeWidth)

    // Bottom-right
    drawLine(drawColor, Offset(w - inset, h - inset), Offset(w - inset - length, h - inset), strokeWidth)
    drawLine(drawColor, Offset(w - inset, h - inset), Offset(w - inset, h - inset - length), strokeWidth)
}

/**
 * Faint horizontal scanlines for CRT/monitor background texture.
 */
fun DrawScope.drawScanlines(
    alpha: Float = 0.015f,
    spacing: Float = 4f
) {
    val lineColor = Color.White.copy(alpha = alpha)
    var y = 0f
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += spacing
    }
}

/**
 * Vignette effect for the edges of the screen.
 */
fun DrawScope.drawVignette(
    color: Color = Color.Black,
    alpha: Float = 0.4f
) {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = radius,
        center = center
    )
}