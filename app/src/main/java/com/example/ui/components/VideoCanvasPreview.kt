package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio
import com.example.data.model.ProjectEntity
import com.example.data.model.TimelineClip
import com.example.data.model.TrackType
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCoral
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioObsidianDark
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@Composable
fun VideoCanvasPreview(
    project: ProjectEntity,
    clips: List<TimelineClip>,
    playheadMs: Long,
    isPlaying: Boolean,
    selectedClip: TimelineClip?,
    onTogglePlay: () -> Unit,
    onStepBackward: () -> Unit,
    onStepForward: () -> Unit,
    formatTime: (Long) -> String,
    totalDurationMs: Long,
    modifier: Modifier = Modifier
) {
    var showGrid by remember { mutableStateOf(false) }
    var showSafeZones by remember { mutableStateOf(false) }
    var isFullScreen by remember { mutableStateOf(false) }

    // Interactive scale & offset gestures for the monitor
    var canvasScale by remember { mutableFloatStateOf(1f) }
    var canvasOffsetX by remember { mutableFloatStateOf(0f) }
    var canvasOffsetY by remember { mutableFloatStateOf(0f) }

    val currentRatio = when (project.aspectRatio) {
        AspectRatio.RATIO_9_16.name -> 9f / 16f
        AspectRatio.RATIO_16_9.name -> 16f / 9f
        AspectRatio.RATIO_1_1.name -> 1f
        AspectRatio.RATIO_4_5.name -> 4f / 5f
        AspectRatio.RATIO_4_3.name -> 4f / 3f
        AspectRatio.RATIO_21_9.name -> 21f / 9f
        else -> 9f / 16f
    }

    // Determine active clips at current playhead position
    val activeVideoClips = clips.filter {
        it.trackType == TrackType.VIDEO && playheadMs >= it.startMs && playheadMs <= (it.startMs + it.durationMs)
    }
    val activeTextClips = clips.filter {
        it.trackType == TrackType.TEXT && playheadMs >= it.startMs && playheadMs <= (it.startMs + it.durationMs)
    }
    val activeOverlays = clips.filter {
        it.trackType == TrackType.OVERLAY && playheadMs >= it.startMs && playheadMs <= (it.startMs + it.durationMs)
    }

    val primaryVideoClip = activeVideoClips.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioObsidianDark)
            .testTag("video_canvas_preview_area"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Monitor Canvas Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF070A0F))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val maxH = maxHeight
            val maxW = maxWidth

            // Screen Canvas with aspect ratio
            Box(
                modifier = Modifier
                    .aspectRatio(currentRatio)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .border(1.dp, StudioBorderDark, RoundedCornerShape(8.dp))
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            canvasScale = (canvasScale * zoom).coerceIn(0.8f, 3.0f)
                            canvasOffsetX += pan.x
                            canvasOffsetY += pan.y
                        }
                    }
                    .testTag("video_monitor_screen"),
                contentAlignment = Alignment.Center
            ) {
                // Background video frame visualization
                if (primaryVideoClip != null) {
                    val startCol = Color(primaryVideoClip.colorGradientStart)
                    val endCol = Color(primaryVideoClip.colorGradientEnd)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(startCol, endCol, Color(0xFF1E1B4B))
                                )
                            )
                    ) {
                        // Subtle motion lines simulating dynamic video footage
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val timePhase = (playheadMs % 2000L) / 2000f
                            val stroke = Stroke(width = 2f)
                            val w = size.width
                            val h = size.height
                            drawLine(
                                color = Color.White.copy(alpha = 0.15f),
                                start = Offset(0f, h * timePhase),
                                end = Offset(w, h * ((timePhase + 0.3f) % 1f)),
                                strokeWidth = 2f
                            )
                        }

                        // Clip name badge
                        Text(
                            text = primaryVideoClip.name,
                            color = StudioTextWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        // Active LUT tag
                        if (primaryVideoClip.adjustment.lutName.isNotEmpty()) {
                            Text(
                                text = "LUT: ${primaryVideoClip.adjustment.lutName}",
                                color = StudioCyan,
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    // Empty state / blank frame at playhead
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartDisplay,
                            contentDescription = null,
                            tint = StudioTextMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No clip at playhead",
                            color = StudioTextMuted.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Render Overlays / B-Roll
                activeOverlays.forEach { overlay ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.65f)
                            .align(Alignment.Center)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(overlay.colorGradientEnd).copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                            .border(1.dp, StudioCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = overlay.name,
                            color = StudioTextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Render Active Titles & Captions
                activeTextClips.forEach { textClip ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                            .border(1.dp, StudioViolet.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = textClip.textContent.ifBlank { textClip.name },
                            color = if (textClip.textStyleName.contains("Yellow")) Color(0xFFFFEB3B) else StudioTextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                // Selected Clip Bounding Box / Transform handles
                if (selectedClip != null && selectedClip.trackType == TrackType.VIDEO && primaryVideoClip?.id == selectedClip.id) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = Stroke(
                            width = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                        )
                        drawRoundRect(
                            color = Color(0xFF00F2FE),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                            style = stroke
                        )
                    }
                }

                // Rule-of-thirds Grid Overlay
                if (showGrid) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val stroke = Stroke(width = 1f)
                        val gridColor = Color.White.copy(alpha = 0.35f)
                        // Vertical lines
                        drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1f)
                        drawLine(gridColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1f)
                        // Horizontal lines
                        drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1f)
                        drawLine(gridColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1f)
                    }
                }

                // Safe Zones Overlay (TikTok / Reels UI Safe margins)
                if (showSafeZones) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val safeColor = Color(0xFFFF3366).copy(alpha = 0.3f)
                        // Bottom safe zone (comments/caption zone on TikTok)
                        drawRect(
                            color = safeColor,
                            topLeft = Offset(0f, h * 0.78f),
                            size = androidx.compose.ui.geometry.Size(w, h * 0.22f)
                        )
                        // Right safe zone (like/share buttons)
                        drawRect(
                            color = safeColor,
                            topLeft = Offset(w * 0.85f, h * 0.25f),
                            size = androidx.compose.ui.geometry.Size(w * 0.15f, h * 0.5f)
                        )
                    }
                }
            }
        }

        // Monitor Transport Bar (Timecode, Play/Pause, Frame stepping, Overlays)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = StudioSurfaceDark,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Timecode readout
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTime(playheadMs),
                        color = StudioCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " / ${formatTime(totalDurationMs)}",
                        color = StudioTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Center: Transport Controls (Step Back, Play/Pause, Step Forward)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onStepBackward,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("transport_step_back")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Step -1 Frame",
                            tint = StudioTextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Glowing Play/Pause FAB
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = if (isPlaying) listOf(StudioCoral, Color(0xFFEF4444))
                                    else listOf(StudioCyan, StudioViolet)
                                )
                            )
                            .clickable { onTogglePlay() }
                            .testTag("transport_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = StudioObsidianDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onStepForward,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("transport_step_forward")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Step +1 Frame",
                            tint = StudioTextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Right: Monitor Overlays (Grid, Safe Zones, Zoom reset)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showGrid = !showGrid },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("toggle_grid_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Toggle Grid",
                            tint = if (showGrid) StudioCyan else StudioTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showSafeZones = !showSafeZones },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("toggle_safe_zones_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Safe Zones",
                            tint = if (showSafeZones) StudioCoral else StudioTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
