package com.example.data.model

enum class TrackType(val displayName: String, val colorHex: Long) {
    TEXT("Text & Titles", 0xFFE040FB),
    OVERLAY("Overlay / PiP", 0xFF00E5FF),
    VIDEO("Main Video", 0xFF6366F1),
    AUDIO_MUSIC("Music", 0xFF10B981),
    AUDIO_VOICE("Voice & SFX", 0xFFF59E0B),
    EFFECTS("FX & Color", 0xFFFF007A)
}

enum class TransitionType(val displayName: String) {
    NONE("None"),
    DISSOLVE("Cross Dissolve"),
    WHIP_PAN("Whip Pan"),
    ZOOM_IN("Zoom Impact"),
    GLITCH("Cyber Glitch"),
    LIGHT_LEAK("Golden Leak"),
    SLIDE_LEFT("Slide"),
    WIPE("Cinematic Wipe")
}

data class Keyframe(
    val timeMs: Long,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val easing: String = "Ease-In-Out"
)

data class ClipColorAdjustment(
    val exposure: Float = 0f, // -100 to 100
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val temperature: Float = 0f, // -100 to 100
    val tint: Float = 0f,
    val highlights: Float = 0f,
    val shadows: Float = 0f,
    val vignette: Float = 0f,
    val sharpen: Float = 0f,
    val lutName: String = "Natural Pro"
)

data class TimelineClip(
    val id: String,
    val trackType: TrackType,
    val name: String,
    val startMs: Long,
    val durationMs: Long,
    val sourceTrimStartMs: Long = 0L,
    val sourceTrimEndMs: Long = 0L,
    val volume: Float = 1.0f,
    val speed: Float = 1.0f,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val colorGradientStart: Long = 0xFF4338CA,
    val colorGradientEnd: Long = 0xFF6366F1,
    val textContent: String = "",
    val textStyleName: String = "Bold Neon",
    val transitionIn: TransitionType = TransitionType.NONE,
    val transitionDurationMs: Long = 500L,
    val adjustment: ClipColorAdjustment = ClipColorAdjustment(),
    val keyframes: List<Keyframe> = emptyList(),
    val hasBackgroundRemoved: Boolean = false,
    val chromaKeyActive: Boolean = false,
    val audioDucking: Boolean = false,
    val waveformPoints: List<Float> = listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.8f, 0.5f, 0.7f, 0.3f, 0.9f, 0.4f, 0.6f)
)
