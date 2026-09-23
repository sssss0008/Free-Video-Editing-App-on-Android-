package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimelineClip
import com.example.data.model.TrackType
import com.example.data.model.TransitionType
import com.example.ui.theme.PlayheadRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioObsidianDark
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@Composable
fun MultiTrackTimeline(
    clips: List<TimelineClip>,
    playheadMs: Long,
    selectedClipId: String?,
    timelineZoom: Float,
    mutedTracks: Set<TrackType>,
    lockedTracks: Set<TrackType>,
    hiddenTracks: Set<TrackType>,
    totalDurationMs: Long,
    onSeek: (Long) -> Unit,
    onSelectClip: (String?) -> Unit,
    onZoomChange: (Float) -> Unit,
    onToggleMute: (TrackType) -> Unit,
    onToggleLock: (TrackType) -> Unit,
    onToggleHide: (TrackType) -> Unit,
    modifier: Modifier = Modifier
) {
    // 100 pixels per second scaled by timelineZoom
    val pixelsPerSec = 75f * timelineZoom
    val pixelsPerMs = pixelsPerSec / 1000f

    val totalWidthDp = ((totalDurationMs * pixelsPerMs) + 400).dp
    val scrollState = rememberScrollState()

    val trackHeaderWidth = 92.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioObsidianDark)
            .testTag("multi_track_timeline")
    ) {
        // Timeline Header: Zoom controls and Snap readout
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = StudioSurfaceDark,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MULTI-TRACK TIMELINE",
                    color = StudioTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Zoom controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onZoomChange(timelineZoom - 0.25f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "%.1fx".format(timelineZoom),
                        color = StudioCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { onZoomChange(timelineZoom + 0.25f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Timeline Scroll Area with Sticky Track Headers on Left
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Track Headers Column (Fixed on the left)
            Column(
                modifier = Modifier
                    .width(trackHeaderWidth)
                    .fillMaxHeight()
                    .background(StudioSurfaceDark)
                    .border(width = 1.dp, color = StudioBorderDark)
            ) {
                // Time Ruler corner block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(StudioCardDark)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "TRACKS",
                        color = StudioTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Headers for each track type
                val tracks = listOf(
                    TrackType.TEXT,
                    TrackType.OVERLAY,
                    TrackType.VIDEO,
                    TrackType.AUDIO_MUSIC,
                    TrackType.AUDIO_VOICE
                )

                tracks.forEach { track ->
                    val isMuted = mutedTracks.contains(track)
                    val isLocked = lockedTracks.contains(track)
                    val isHidden = hiddenTracks.contains(track)
                    val trackHeight = if (track == TrackType.VIDEO) 54.dp else 42.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(trackHeight)
                            .border(0.5.dp, StudioBorderDark)
                            .background(if (isLocked) StudioCardDark.copy(alpha = 0.5f) else StudioSurfaceDark)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = track.displayName.take(8),
                                    color = Color(track.colorHex),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Mute toggle
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onToggleMute(track) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Mute Track",
                                        tint = if (isMuted) StudioAmber else StudioTextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }

                                // Lock toggle
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onToggleLock(track) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = "Lock Track",
                                        tint = if (isLocked) StudioCyan else StudioTextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }

                                // Hide toggle
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onToggleHide(track) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Hide Track",
                                        tint = if (isHidden) PlayheadRed else StudioTextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Horizontally Scrollable Lanes & Playhead
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
                    .pointerInput(totalDurationMs, pixelsPerMs) {
                        detectTapGestures { offset ->
                            val clickedMs = (offset.x / pixelsPerMs).toLong()
                            onSeek(clickedMs)
                        }
                    }
                    .testTag("timeline_lanes_scroll")
            ) {
                // The actual track lanes layout
                Column(modifier = Modifier.width(totalWidthDp)) {
                    // 1. Timecode Ruler with beat ticks
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(StudioCardDark)
                            .pointerInput(totalDurationMs, pixelsPerMs) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    val draggedMs = (change.position.x / pixelsPerMs).toLong()
                                    onSeek(draggedMs)
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val durationSec = (totalDurationMs / 1000).toInt() + 5
                            val tickColor = Color(0xFF64748B)
                            val beatColor = Color(0xFFF59E0B)

                            for (sec in 0..durationSec) {
                                val x = sec * 1000f * pixelsPerMs
                                // Major second tick
                                drawLine(
                                    color = Color.White.copy(alpha = 0.7f),
                                    start = Offset(x, 10f),
                                    end = Offset(x, size.height),
                                    strokeWidth = 1.5f
                                )
                                // Sub ticks (half seconds)
                                val halfX = x + (500f * pixelsPerMs)
                                drawLine(
                                    color = tickColor,
                                    start = Offset(halfX, 18f),
                                    end = Offset(halfX, size.height),
                                    strokeWidth = 1f
                                )

                                // Beat marker dot at 128 BPM interval (~468ms)
                                val beatX = sec * 1000f * pixelsPerMs + (234f * pixelsPerMs)
                                drawCircle(
                                    color = beatColor,
                                    radius = 2.5f,
                                    center = Offset(beatX, 6f)
                                )
                            }
                        }

                        // Time stamps labels
                        val durationSec = (totalDurationMs / 1000).toInt() + 5
                        for (sec in 0..durationSec step 2) {
                            val xPos = (sec * 1000f * pixelsPerMs).dp
                            Text(
                                text = "%02d:%02d".format(sec / 60, sec % 60),
                                color = StudioTextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .offset(x = xPos + 4.dp, y = 2.dp)
                            )
                        }
                    }

                    // 2. Track Lanes
                    val tracks = listOf(
                        TrackType.TEXT,
                        TrackType.OVERLAY,
                        TrackType.VIDEO,
                        TrackType.AUDIO_MUSIC,
                        TrackType.AUDIO_VOICE
                    )

                    tracks.forEach { track ->
                        val isHidden = hiddenTracks.contains(track)
                        val isLocked = lockedTracks.contains(track)
                        val trackHeight = if (track == TrackType.VIDEO) 54.dp else 42.dp

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(trackHeight)
                                .border(0.5.dp, StudioBorderDark.copy(alpha = 0.6f))
                                .background(StudioObsidianDark)
                        ) {
                            if (!isHidden) {
                                val trackClips = clips.filter { it.trackType == track }
                                trackClips.forEach { clip ->
                                    val isSelected = clip.id == selectedClipId
                                    val clipLeftDp = (clip.startMs * pixelsPerMs).dp
                                    val clipWidthDp = (clip.durationMs * pixelsPerMs).dp.coerceAtLeast(40.dp)

                                    // Render Timeline Clip Card
                                    Box(
                                        modifier = Modifier
                                            .offset(x = clipLeftDp)
                                            .width(clipWidthDp)
                                            .fillMaxHeight()
                                            .padding(vertical = 3.dp, horizontal = 1.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(clip.colorGradientStart),
                                                        Color(clip.colorGradientEnd)
                                                    )
                                                )
                                            )
                                            .then(
                                                if (isSelected) {
                                                    Modifier.border(
                                                        2.dp,
                                                        StudioCyan,
                                                        RoundedCornerShape(6.dp)
                                                    )
                                                } else {
                                                    Modifier.border(
                                                        0.5.dp,
                                                        Color.White.copy(alpha = 0.2f),
                                                        RoundedCornerShape(6.dp)
                                                    )
                                                }
                                            )
                                            .clickable {
                                                if (!isLocked) onSelectClip(clip.id)
                                            }
                                            .testTag("timeline_clip_${clip.id}")
                                    ) {
                                        // Audio Waveform Visualization inside audio tracks
                                        if (clip.trackType == TrackType.AUDIO_MUSIC || clip.trackType == TrackType.AUDIO_VOICE) {
                                            Canvas(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 4.dp)
                                            ) {
                                                val count = 24
                                                val step = size.width / count
                                                val wavePoints = clip.waveformPoints
                                                for (i in 0 until count) {
                                                    val sample = wavePoints.getOrElse(i % wavePoints.size) { 0.5f }
                                                    val barH = size.height * sample * 0.8f
                                                    val yStart = (size.height - barH) / 2f
                                                    drawRoundRect(
                                                        color = Color.White.copy(alpha = 0.6f),
                                                        topLeft = Offset(i * step, yStart),
                                                        size = Size(step * 0.65f, barH),
                                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                                                    )
                                                }
                                            }
                                        }

                                        // Clip Content Row
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = clip.name,
                                                color = StudioTextWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )

                                            // Badges (Speed, Transition, Keyframes)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (clip.speed != 1.0f) {
                                                    Text(
                                                        text = "${clip.speed}x",
                                                        color = StudioAmber,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                                                            .padding(horizontal = 3.dp)
                                                    )
                                                }

                                                if (clip.transitionIn != TransitionType.NONE) {
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "✦",
                                                        color = StudioCyan,
                                                        fontSize = 10.sp
                                                    )
                                                }

                                                if (clip.keyframes.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Key,
                                                        contentDescription = "Keyframes",
                                                        tint = StudioAmber,
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Trimming Handles when Selected
                                        if (isSelected) {
                                            // Left trim handle
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .width(10.dp)
                                                    .fillMaxHeight()
                                                    .background(StudioCyan, RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                                            )
                                            // Right trim handle
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd)
                                                    .width(10.dp)
                                                    .fillMaxHeight()
                                                    .background(StudioCyan, RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Vertical Playhead Line running through all tracks
                val playheadXDp = (playheadMs * pixelsPerMs).dp
                Box(
                    modifier = Modifier
                        .offset(x = playheadXDp - 7.dp)
                        .width(14.dp)
                        .fillMaxHeight()
                        .pointerInput(pixelsPerMs) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val draggedMs = (change.position.x / pixelsPerMs).toLong()
                                onSeek(draggedMs)
                            }
                        }
                        .testTag("timeline_playhead_indicator")
                ) {
                    // Playhead top head/handle (Red triangle/diamond)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(PlayheadRed)
                            .shadow(6.dp)
                    )

                    // Vertical glowing red needle
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 12.dp)
                            .width(2.5.dp)
                            .fillMaxHeight()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(PlayheadRed, PlayheadRed.copy(alpha = 0.8f))
                                )
                            )
                    )
                }
            }
        }
    }
}
