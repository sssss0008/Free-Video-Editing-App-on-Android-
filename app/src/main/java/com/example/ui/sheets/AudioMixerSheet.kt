package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleDataProvider
import com.example.data.model.AudioTrackItem
import com.example.data.model.TimelineClip
import com.example.data.model.TrackType
import com.example.ui.theme.PlayheadRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioMixerSheet(
    isRecordingVoice: Boolean,
    recordingSeconds: Int,
    onToggleRecording: () -> Unit,
    onAddAudioToTimeline: (TimelineClip) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Music, 1: SFX, 2: Voiceover

    val musicCatalog = remember { SampleDataProvider.getAudioCatalog() }
    val sfxCatalog = remember { SampleDataProvider.getSoundEffects() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioSurfaceDark,
        tonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .testTag("audio_mixer_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AUDIO PRODUCTION SUITE",
                        color = StudioEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Soundtracks & Voice Studio",
                        color = StudioTextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = StudioTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs: Music / SFX / Voice Recorder
            val tabs = listOf("Music Tracks", "Sound FX", "Voice Recorder")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioCardDark,
                contentColor = StudioEmerald,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = StudioEmerald
                    )
                }
            ) {
                tabs.forEachIndexed { idx, label ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == idx) StudioEmerald else StudioTextMuted
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab 0: Music Tracks Catalog
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(musicCatalog) { track ->
                        AudioTrackRow(
                            track = track,
                            onAdd = {
                                val clip = TimelineClip(
                                    id = "clip_mus_${System.currentTimeMillis() % 10000}",
                                    trackType = TrackType.AUDIO_MUSIC,
                                    name = "${track.title} (${track.bpm} BPM)",
                                    startMs = 0L,
                                    durationMs = track.durationMs.coerceAtMost(25000L),
                                    volume = 0.8f,
                                    colorGradientStart = 0xFF059669L,
                                    colorGradientEnd = 0xFF10B981L,
                                    waveformPoints = track.waveforms
                                )
                                onAddAudioToTimeline(clip)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            // Tab 1: Sound FX Catalog
            if (selectedTab == 1) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sfxCatalog) { sfx ->
                        AudioTrackRow(
                            track = sfx,
                            onAdd = {
                                val clip = TimelineClip(
                                    id = "clip_sfx_${System.currentTimeMillis() % 10000}",
                                    trackType = TrackType.AUDIO_VOICE,
                                    name = sfx.title,
                                    startMs = 2000L,
                                    durationMs = sfx.durationMs,
                                    volume = 0.95f,
                                    colorGradientStart = 0xFFB45309L,
                                    colorGradientEnd = 0xFFD97706L,
                                    waveformPoints = sfx.waveforms
                                )
                                onAddAudioToTimeline(clip)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            // Tab 2: Studio Voice Recorder
            if (selectedTab == 2) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Recording Timer display
                    Text(
                        text = "%02d:%02d".format(recordingSeconds / 60, recordingSeconds % 60),
                        color = if (isRecordingVoice) PlayheadRed else StudioTextWhite,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = if (isRecordingVoice) "RECORDING STUDIO VOICEOVER..." else "Tap Mic to Start Take",
                        color = StudioTextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Live Waveform visualizer simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioCardDark)
                            .border(1.dp, StudioBorderDark, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val count = 28
                            val step = size.width / count
                            for (i in 0 until count) {
                                val hFactor = if (isRecordingVoice) ((i * 17) % 10) / 10f * 0.8f + 0.2f else 0.15f
                                val barH = size.height * hFactor
                                drawRoundRect(
                                    color = if (isRecordingVoice) Color(0xFFF59E0B) else Color(0xFF64748B),
                                    topLeft = Offset(i * step, (size.height - barH) / 2f),
                                    size = Size(step * 0.6f, barH),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Large Mic Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRecordingVoice) PlayheadRed else StudioEmerald
                            )
                            .clickable { onToggleRecording() }
                            .testTag("toggle_voice_record_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecordingVoice) "Stop Recording" else "Record Voice",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioTrackRow(
    track: AudioTrackItem,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(StudioCardDark)
            .border(1.dp, StudioBorderDark, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioEmerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = StudioEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = track.title,
                        color = StudioTextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${track.artist} • ${track.durationText} ${if (track.bpm > 0) "• ${track.bpm} BPM" else ""}",
                        color = StudioTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Track",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Add",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
