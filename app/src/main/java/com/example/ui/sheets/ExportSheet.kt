package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExportSettings
import com.example.data.model.ProjectResolution
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioObsidianDark
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSheet(
    exportSettings: ExportSettings,
    isExporting: Boolean,
    exportProgress: Float,
    isComplete: Boolean,
    onStartExport: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedResolution by remember { mutableStateOf(exportSettings.resolution) }
    var selectedFps by remember { mutableIntStateOf(exportSettings.fps) }
    var bitrateMbps by remember { mutableIntStateOf(exportSettings.bitrateMbps) }
    var selectedCodec by remember { mutableStateOf(exportSettings.codec) }
    var selectedFormat by remember { mutableStateOf(exportSettings.format) }
    var watermarkEnabled by remember { mutableStateOf(exportSettings.includeWatermark) }

    // Approximate size calculator: (Bitrate in Mbps * Duration in sec) / 8
    val approxSizeMb = (bitrateMbps * 25) / 8

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
                .verticalScroll(rememberScrollState())
                .testTag("export_studio_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = StudioCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HIGH-FIDELITY MASTER EXPORT",
                            color = StudioCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Render & Publish",
                            color = StudioTextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = StudioTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // When Export is completed
            if (isComplete) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(StudioEmerald.copy(alpha = 0.15f))
                        .border(1.dp, StudioEmerald, RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = StudioEmerald,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Master Render Ready!",
                        color = StudioTextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${selectedResolution.badge} • ${selectedFps}fps • ${approxSizeMb} MB • $selectedCodec",
                        color = StudioTextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCardDark)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save File")
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", color = Color.Black)
                            }
                        }
                    }
                }
                return@Column
            }

            // When actively rendering
            if (isExporting) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(StudioCardDark)
                        .border(1.dp, StudioCyan, RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HARDWARE ACCELERATED ENCODING",
                        color = StudioCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${(exportProgress * 100).toInt()}%",
                        color = StudioTextWhite,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { exportProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = StudioCyan,
                        trackColor = StudioObsidianDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Rendering multi-track audio & LUTs • ETA: ~${(10 * (1f - exportProgress)).toInt()}s",
                        color = StudioTextMuted,
                        fontSize = 11.sp
                    )
                }
                return@Column
            }

            // Normal Setup State: Presets and Configuration
            Text(
                text = "EXPORT PRESET",
                color = StudioTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val presets = listOf(
                "Shorts / Reels (1080p60)",
                "YouTube 4K Master",
                "Cinematic 24p HDR",
                "Web Compact"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioCardDark)
                            .border(1.dp, StudioBorderDark, RoundedCornerShape(8.dp))
                            .clickable {
                                if (preset.contains("4K")) {
                                    selectedResolution = ProjectResolution.RES_4K
                                    bitrateMbps = 45
                                } else if (preset.contains("Shorts")) {
                                    selectedResolution = ProjectResolution.RES_1080P
                                    selectedFps = 60
                                    bitrateMbps = 25
                                }
                            }
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.substringBefore(" ("),
                            color = StudioTextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resolution selection
            Text(
                text = "RESOLUTION",
                color = StudioTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProjectResolution.values().forEach { res ->
                    val isSelected = selectedResolution == res
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioCardDark)
                            .border(1.dp, if (isSelected) StudioCyan else StudioBorderDark, RoundedCornerShape(8.dp))
                            .clickable { selectedResolution = res }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = res.badge,
                            color = if (isSelected) StudioCyan else StudioTextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Framerate selection
            Text(
                text = "FRAME RATE",
                color = StudioTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(24, 30, 60, 120).forEach { fps ->
                    val isSelected = selectedFps == fps
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StudioViolet.copy(alpha = 0.2f) else StudioCardDark)
                            .border(1.dp, if (isSelected) StudioViolet else StudioBorderDark, RoundedCornerShape(8.dp))
                            .clickable { selectedFps = fps }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${fps}fps",
                            color = if (isSelected) StudioViolet else StudioTextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bitrate Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "TARGET BITRATE", color = StudioTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "$bitrateMbps Mbps (Est. ${approxSizeMb} MB)", color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = bitrateMbps.toFloat(),
                onValueChange = { bitrateMbps = it.toInt() },
                valueRange = 8f..80f,
                colors = SliderDefaults.colors(
                    thumbColor = StudioCyan,
                    activeTrackColor = StudioCyan,
                    inactiveTrackColor = StudioCardDark
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Codec selection
            Text(
                text = "ENCODING CODEC",
                color = StudioTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("H.265 (HEVC)", "H.264 (AVC)", "ProRes 422").forEach { codec ->
                    val isSelected = selectedCodec == codec
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioCardDark)
                            .border(1.dp, if (isSelected) StudioCyan else StudioBorderDark, RoundedCornerShape(8.dp))
                            .clickable { selectedCodec = codec }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = codec.substringBefore(" "),
                            color = if (isSelected) StudioCyan else StudioTextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Watermark toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(StudioCardDark)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Lumina Studio Watermark", color = StudioTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Export clean 100% watermark-free master", color = StudioTextMuted, fontSize = 10.sp)
                }
                Switch(
                    checked = watermarkEnabled,
                    onCheckedChange = { watermarkEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = StudioCyan,
                        checkedTrackColor = StudioCyan.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Start Render Button
            Button(
                onClick = onStartExport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_export_render_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            Brush.horizontalGradient(listOf(StudioCyan, StudioViolet)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Render & Export",
                            color = StudioObsidianDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
