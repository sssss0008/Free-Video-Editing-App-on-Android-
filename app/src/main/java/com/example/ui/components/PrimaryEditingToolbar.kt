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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioBorderDark
import com.example.ui.theme.StudioCardDark
import com.example.ui.theme.StudioCoral
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextWhite
import com.example.ui.theme.StudioViolet

@Composable
fun PrimaryEditingToolbar(
    onOpenMedia: () -> Unit,
    onOpenAudio: () -> Unit,
    onOpenText: () -> Unit,
    onOpenCaptions: () -> Unit,
    onOpenEffects: () -> Unit,
    onOpenColorGrade: () -> Unit,
    onOpenTransitions: () -> Unit,
    onOpenAIStudio: () -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenThumbnail: () -> Unit,
    onOpenCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("primary_editing_toolbar"),
        color = StudioSurfaceDark,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AI Studio (Special Highlighted Gradient item)
            Column(
                modifier = Modifier
                    .clickable { onOpenAIStudio() }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .testTag("toolbar_ai_studio"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(StudioCyan, StudioViolet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Studio",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "AI Studio",
                    color = StudioCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            ToolbarButton(
                icon = Icons.Default.AddPhotoAlternate,
                label = "Media",
                onClick = onOpenMedia
            )

            ToolbarButton(
                icon = Icons.Default.MusicNote,
                label = "Audio",
                onClick = onOpenAudio
            )

            ToolbarButton(
                icon = Icons.Default.TextFields,
                label = "Text",
                onClick = onOpenText
            )

            ToolbarButton(
                icon = Icons.Default.ClosedCaption,
                label = "Captions",
                onClick = onOpenCaptions
            )

            ToolbarButton(
                icon = Icons.Default.Filter,
                label = "Effects",
                onClick = onOpenEffects
            )

            ToolbarButton(
                icon = Icons.Default.ColorLens,
                label = "Color",
                onClick = onOpenColorGrade
            )

            ToolbarButton(
                icon = Icons.Default.Transform,
                label = "Transition",
                onClick = onOpenTransitions
            )

            ToolbarButton(
                icon = Icons.Default.DashboardCustomize,
                label = "Templates",
                onClick = onOpenTemplates
            )

            ToolbarButton(
                icon = Icons.Default.Image,
                label = "Thumbnail",
                onClick = onOpenThumbnail
            )

            ToolbarButton(
                icon = Icons.Default.CameraAlt,
                label = "Record",
                onClick = onOpenCamera
            )
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag("toolbar_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(StudioCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = StudioTextWhite,
                modifier = Modifier.size(19.dp)
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
