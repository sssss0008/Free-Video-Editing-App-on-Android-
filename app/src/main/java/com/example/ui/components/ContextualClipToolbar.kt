package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimelineClip
import com.example.ui.theme.PlayheadRed
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@Composable
fun ContextualClipToolbar(
    selectedClip: TimelineClip,
    onDeselect: () -> Unit,
    onSplit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onOpenSpeed: () -> Unit,
    onOpenVolume: () -> Unit,
    onOpenColorGrade: () -> Unit,
    onOpenTransitions: () -> Unit,
    onAddKeyframe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("contextual_clip_toolbar"),
        color = StudioSurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Selected Clip Banner with Close/Deselect
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioCardDark)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StudioCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Selected: ${selectedClip.name}",
                        color = StudioTextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onDeselect,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Deselect Clip",
                        tint = StudioTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Scrollable Context Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContextActionItem(
                    icon = Icons.Default.ContentCut,
                    label = "Split",
                    tint = StudioCyan,
                    onClick = onSplit
                )

                ContextActionItem(
                    icon = Icons.Default.Speed,
                    label = "Speed",
                    onClick = onOpenSpeed
                )

                ContextActionItem(
                    icon = Icons.Default.VolumeUp,
                    label = "Volume",
                    onClick = onOpenVolume
                )

                ContextActionItem(
                    icon = Icons.Default.Palette,
                    label = "Color / LUT",
                    onClick = onOpenColorGrade
                )

                ContextActionItem(
                    icon = Icons.Default.MotionPhotosOn,
                    label = "Transition",
                    onClick = onOpenTransitions
                )

                ContextActionItem(
                    icon = Icons.Default.Key,
                    label = "Keyframe",
                    tint = StudioViolet,
                    onClick = onAddKeyframe
                )

                ContextActionItem(
                    icon = Icons.Default.ContentCopy,
                    label = "Duplicate",
                    onClick = onDuplicate
                )

                ContextActionItem(
                    icon = Icons.Default.Delete,
                    label = "Delete",
                    tint = PlayheadRed,
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
private fun ContextActionItem(
    icon: ImageVector,
    label: String,
    tint: Color = StudioTextWhite,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .testTag("context_action_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(StudioCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = StudioTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
