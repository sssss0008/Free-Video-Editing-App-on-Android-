package com.example.ui.sheets

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleDataProvider
import com.example.data.model.ClipColorAdjustment
import com.example.data.model.LutPreset
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorGradingSheet(
    initialAdjustment: ClipColorAdjustment,
    onApplyAdjustment: (ClipColorAdjustment) -> Unit,
    onApplyLut: (LutPreset) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: LUT Presets, 1: Sliders, 2: Curves/HSL

    var exposure by remember { mutableFloatStateOf(initialAdjustment.exposure) }
    var contrast by remember { mutableFloatStateOf(initialAdjustment.contrast) }
    var saturation by remember { mutableFloatStateOf(initialAdjustment.saturation) }
    var temperature by remember { mutableFloatStateOf(initialAdjustment.temperature) }
    var tint by remember { mutableFloatStateOf(initialAdjustment.tint) }
    var vignette by remember { mutableFloatStateOf(initialAdjustment.vignette) }

    val luts = remember { SampleDataProvider.getLutPresets() }

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
                .testTag("color_grading_sheet")
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
                            .background(StudioViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = StudioViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "COLOR GRADING STUDIO",
                            color = StudioViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Cinematic LUTs & Curves",
                            color = StudioTextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            exposure = 0f
                            contrast = 0f
                            saturation = 0f
                            temperature = 0f
                            tint = 0f
                            vignette = 0f
                            onApplyAdjustment(ClipColorAdjustment())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Color",
                            tint = StudioTextMuted
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs: [Cinematic LUTs, Manual Sliders, Wheels/HSL]
            val tabs = listOf("Cinematic LUTs", "Manual Sliders", "Wheels & HSL")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioCardDark,
                contentColor = StudioViolet,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = StudioViolet
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
                                color = if (selectedTab == idx) StudioViolet else StudioTextMuted
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab 0: LUT Presets
            if (selectedTab == 0) {
                Text(
                    text = "SELECT 3D CINEMATIC LOOK",
                    color = StudioTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    luts.forEach { lut ->
                        val isCurrent = initialAdjustment.lutName == lut.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) StudioViolet.copy(alpha = 0.2f) else StudioCardDark)
                                .border(1.dp, if (isCurrent) StudioViolet else StudioBorderDark, RoundedCornerShape(10.dp))
                                .clickable {
                                    onApplyLut(lut)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(lut.previewTint))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = lut.name,
                                        color = StudioTextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = lut.mood,
                                        color = StudioTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            if (isCurrent) {
                                Text(
                                    text = "ACTIVE",
                                    color = StudioCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Tab 1: Manual Sliders
            if (selectedTab == 1) {
                ColorSlider(
                    label = "Exposure",
                    value = exposure,
                    range = -50f..50f,
                    onValueChange = {
                        exposure = it
                        onApplyAdjustment(initialAdjustment.copy(exposure = exposure))
                    }
                )

                ColorSlider(
                    label = "Contrast",
                    value = contrast,
                    range = -50f..50f,
                    onValueChange = {
                        contrast = it
                        onApplyAdjustment(initialAdjustment.copy(contrast = contrast))
                    }
                )

                ColorSlider(
                    label = "Saturation",
                    value = saturation,
                    range = -50f..50f,
                    onValueChange = {
                        saturation = it
                        onApplyAdjustment(initialAdjustment.copy(saturation = saturation))
                    }
                )

                ColorSlider(
                    label = "Temperature",
                    value = temperature,
                    range = -50f..50f,
                    onValueChange = {
                        temperature = it
                        onApplyAdjustment(initialAdjustment.copy(temperature = temperature))
                    }
                )

                ColorSlider(
                    label = "Vignette",
                    value = vignette,
                    range = 0f..100f,
                    onValueChange = {
                        vignette = it
                        onApplyAdjustment(initialAdjustment.copy(vignette = vignette))
                    }
                )
            }

            // Tab 2: 3-Way Wheels / HSL preview
            if (selectedTab == 2) {
                Text(
                    text = "3-WAY COLOR WHEELS (LIFT / GAMMA / GAIN)",
                    color = StudioTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WheelPlaceholder(title = "Shadows (Lift)", tint = Color(0xFF00E5FF))
                    WheelPlaceholder(title = "Midtones (Gamma)", tint = Color(0xFFFFA726))
                    WheelPlaceholder(title = "Highlights (Gain)", tint = Color(0xFFFF5252))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Curves and tonal balance calibrated for Rec.709 & HDR workflows.",
                    color = StudioTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = StudioTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "${value.toInt()}", color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = StudioCyan,
                activeTrackColor = StudioCyan,
                inactiveTrackColor = StudioCardDark
            )
        )
    }
}

@Composable
private fun WheelPlaceholder(title: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(38.dp))
                .background(StudioCardDark)
                .border(2.dp, tint, RoundedCornerShape(38.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(tint)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = StudioTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
