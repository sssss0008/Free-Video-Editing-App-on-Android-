package com.example.data.model

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val durationText: String,
    val clipsCount: Int,
    val textCount: Int,
    val aspectRatio: AspectRatio,
    val gradientStart: Long,
    val gradientEnd: Long,
    val downloadsCount: String,
    val tags: List<String>
)

data class AudioTrackItem(
    val id: String,
    val title: String,
    val artist: String,
    val durationText: String,
    val durationMs: Long,
    val bpm: Int,
    val category: String,
    val isFavorite: Boolean = false,
    val waveforms: List<Float>
)

data class EffectItem(
    val id: String,
    val name: String,
    val category: String,
    val iconName: String,
    val intensity: Float = 0.8f,
    val previewGradientStart: Long,
    val previewGradientEnd: Long
)

data class LutPreset(
    val id: String,
    val name: String,
    val mood: String,
    val previewTint: Long
)

data class TimelineComment(
    val id: String,
    val timestampMs: Long,
    val authorName: String,
    val authorRole: String,
    val message: String,
    val createdAt: String,
    val isResolved: Boolean = false
)

data class AIVoiceProfile(
    val id: String,
    val name: String,
    val accent: String,
    val gender: String,
    val style: String,
    val previewText: String
)

data class ExportSettings(
    val resolution: ProjectResolution = ProjectResolution.RES_1080P,
    val fps: Int = 60,
    val bitrateMbps: Int = 25,
    val codec: String = "H.265 (HEVC)",
    val format: String = "MP4",
    val audioQualityKbps: Int = 320,
    val includeWatermark: Boolean = false,
    val customWatermarkText: String = "Lumina Studio",
    val exportPresetName: String = "High-Quality Master (Universal)"
)

data class StoryboardScene(
    val sceneNumber: Int,
    val title: String,
    val visualDescription: String,
    val narrationScript: String,
    val durationSec: Int,
    val transition: String
)
