package com.example.data

import com.example.data.model.AIVoiceProfile
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrackItem
import com.example.data.model.ClipColorAdjustment
import com.example.data.model.EffectItem
import com.example.data.model.Keyframe
import com.example.data.model.LutPreset
import com.example.data.model.StoryboardScene
import com.example.data.model.TemplateItem
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineComment
import com.example.data.model.TrackType
import com.example.data.model.TransitionType

object SampleDataProvider {

    fun getInitialClips(): List<TimelineClip> = listOf(
        // Text & Title track
        TimelineClip(
            id = "clip_txt_1",
            trackType = TrackType.TEXT,
            name = "Title: FUTURE VISION",
            startMs = 0L,
            durationMs = 4500L,
            colorGradientStart = 0xFF9333EAL,
            colorGradientEnd = 0xFFC084FCL,
            textContent = "FUTURE VISION // 2026",
            textStyleName = "Cyber Kinetic"
        ),
        TimelineClip(
            id = "clip_txt_2",
            trackType = TrackType.TEXT,
            name = "Caption: 'Shift your perspective'",
            startMs = 5000L,
            durationMs = 6000L,
            colorGradientStart = 0xFF7C3AEDL,
            colorGradientEnd = 0xFFA855F7L,
            textContent = "Shift your perspective everyday.",
            textStyleName = "TikTok Highlight Yellow"
        ),

        // Overlay / B-Roll / Sticker track
        TimelineClip(
            id = "clip_overlay_1",
            trackType = TrackType.OVERLAY,
            name = "B-Roll: HUD Hologram overlay",
            startMs = 3000L,
            durationMs = 7000L,
            colorGradientStart = 0xFF0891B2L,
            colorGradientEnd = 0xFF22D3EEL,
            volume = 0f,
            keyframes = listOf(
                Keyframe(timeMs = 0L, scale = 0.8f, opacity = 0f),
                Keyframe(timeMs = 1000L, scale = 1.0f, opacity = 0.85f),
                Keyframe(timeMs = 6000L, scale = 1.05f, opacity = 0.85f),
                Keyframe(timeMs = 7000L, scale = 1.15f, opacity = 0f)
            )
        ),

        // Main Video track (Continuous sequence)
        TimelineClip(
            id = "clip_vid_1",
            trackType = TrackType.VIDEO,
            name = "Shot 01: Aerial City Skyline",
            startMs = 0L,
            durationMs = 6000L,
            colorGradientStart = 0xFF4338CAL,
            colorGradientEnd = 0xFF6366F1L,
            speed = 1.0f,
            transitionIn = TransitionType.NONE,
            adjustment = ClipColorAdjustment(exposure = 5f, contrast = 12f, saturation = 15f, lutName = "Cyberpunk Teal")
        ),
        TimelineClip(
            id = "clip_vid_2",
            trackType = TrackType.VIDEO,
            name = "Shot 02: Neon Street Walk Speedup",
            startMs = 6000L,
            durationMs = 7500L,
            colorGradientStart = 0xFF3730A3L,
            colorGradientEnd = 0xFF4F46E5L,
            speed = 1.5f,
            transitionIn = TransitionType.WHIP_PAN,
            adjustment = ClipColorAdjustment(contrast = 18f, saturation = 20f, temperature = -8f, lutName = "Cinematic Moody")
        ),
        TimelineClip(
            id = "clip_vid_3",
            trackType = TrackType.VIDEO,
            name = "Shot 03: Subject Portrait SlowMo",
            startMs = 13500L,
            durationMs = 8000L,
            colorGradientStart = 0xFF312E81L,
            colorGradientEnd = 0xFF4338CAL,
            speed = 0.5f,
            transitionIn = TransitionType.ZOOM_IN,
            adjustment = ClipColorAdjustment(exposure = 8f, contrast = 10f, saturation = 8f, lutName = "Kodak Portra")
        ),

        // Music track
        TimelineClip(
            id = "clip_mus_1",
            trackType = TrackType.AUDIO_MUSIC,
            name = "Synthwave Odyssey - Neon Pulse (128 BPM)",
            startMs = 0L,
            durationMs = 21500L,
            volume = 0.75f,
            colorGradientStart = 0xFF059669L,
            colorGradientEnd = 0xFF10B981L,
            audioDucking = true,
            waveformPoints = listOf(0.4f, 0.7f, 0.9f, 0.8f, 0.95f, 0.6f, 0.8f, 0.5f, 0.9f, 0.85f, 0.7f, 0.9f, 0.65f, 0.4f)
        ),

        // Voice & SFX track
        TimelineClip(
            id = "clip_voice_1",
            trackType = TrackType.AUDIO_VOICE,
            name = "AI Voice: Studio Pro Male Narrator",
            startMs = 1000L,
            durationMs = 8500L,
            volume = 1.0f,
            colorGradientStart = 0xFFD97706L,
            colorGradientEnd = 0xFFF59E0BL,
            waveformPoints = listOf(0.2f, 0.8f, 0.9f, 0.7f, 0.1f, 0.85f, 0.95f, 0.6f, 0.2f, 0.75f, 0.1f)
        ),
        TimelineClip(
            id = "clip_sfx_1",
            trackType = TrackType.AUDIO_VOICE,
            name = "SFX: Cinematic Sub Bass Drop & Whoosh",
            startMs = 6000L,
            durationMs = 2500L,
            volume = 0.9f,
            colorGradientStart = 0xFFB45309L,
            colorGradientEnd = 0xFFD97706L,
            waveformPoints = listOf(0.1f, 0.95f, 0.8f, 0.4f, 0.2f)
        )
    )

    fun getTemplates(): List<TemplateItem> = listOf(
        TemplateItem(
            id = "tmpl_1",
            title = "Ultra Fast Beat Drop",
            category = "Reels & TikTok",
            durationText = "0:15",
            clipsCount = 14,
            textCount = 4,
            aspectRatio = AspectRatio.RATIO_9_16,
            gradientStart = 0xFFF43F5EL,
            gradientEnd = 0xFF8B5CF6L,
            downloadsCount = "182K",
            tags = listOf("Fast", "Sync", "Viral", "Transitions")
        ),
        TemplateItem(
            id = "tmpl_2",
            title = "Cinematic Travel Odyssey",
            category = "Cinematic",
            durationText = "0:30",
            clipsCount = 8,
            textCount = 3,
            aspectRatio = AspectRatio.RATIO_16_9,
            gradientStart = 0xFF0284C7L,
            gradientEnd = 0xFF0D9488L,
            downloadsCount = "95K",
            tags = listOf("4K", "Vlog", "Widescreen", "Graded")
        ),
        TemplateItem(
            id = "tmpl_3",
            title = "Podcast Highlight Clip",
            category = "Podcast",
            durationText = "0:45",
            clipsCount = 3,
            textCount = 8,
            aspectRatio = AspectRatio.RATIO_9_16,
            gradientStart = 0xFFD97706L,
            gradientEnd = 0xFFDC2626L,
            downloadsCount = "240K",
            tags = listOf("Captions", "Karaoke", "Sound Wave", "Engaging")
        ),
        TemplateItem(
            id = "tmpl_4",
            title = "Minimalist SaaS Promo",
            category = "Business",
            durationText = "0:25",
            clipsCount = 6,
            textCount = 6,
            aspectRatio = AspectRatio.RATIO_16_9,
            gradientStart = 0xFF4F46E5L,
            gradientEnd = 0xFF06B6D4L,
            downloadsCount = "64K",
            tags = listOf("Clean", "Tech", "Product", "Motion")
        ),
        TemplateItem(
            id = "tmpl_5",
            title = "Fitness Motivation Stinger",
            category = "Reels & TikTok",
            durationText = "0:12",
            clipsCount = 9,
            textCount = 2,
            aspectRatio = AspectRatio.RATIO_9_16,
            gradientStart = 0xFF10B981L,
            gradientEnd = 0xFF3B82F6L,
            downloadsCount = "120K",
            tags = listOf("Gym", "High-Energy", "Flash", "Hard-Cut")
        ),
        TemplateItem(
            id = "tmpl_6",
            title = "Aesthetic Film Grain Memories",
            category = "Social",
            durationText = "0:20",
            clipsCount = 5,
            textCount = 2,
            aspectRatio = AspectRatio.RATIO_4_5,
            gradientStart = 0xFF9333EAL,
            gradientEnd = 0xFFEC4899L,
            downloadsCount = "88K",
            tags = listOf("Vintage", "Grain", "Warm", "Slow")
        )
    )

    fun getAudioCatalog(): List<AudioTrackItem> = listOf(
        AudioTrackItem(
            id = "aud_1",
            title = "Cyber Pulse Accelerator",
            artist = "Aura Sound Lab",
            durationText = "2:14",
            durationMs = 134000L,
            bpm = 128,
            category = "Trending",
            isFavorite = true,
            waveforms = listOf(0.4f, 0.7f, 0.9f, 0.8f, 0.95f, 0.6f, 0.8f, 0.9f)
        ),
        AudioTrackItem(
            id = "aud_2",
            title = "Golden Horizon Cinematic",
            artist = "Lumina Symphony",
            durationText = "3:05",
            durationMs = 185000L,
            bpm = 85,
            category = "Cinematic",
            isFavorite = true,
            waveforms = listOf(0.2f, 0.4f, 0.6f, 0.8f, 0.95f, 0.7f, 0.5f, 0.3f)
        ),
        AudioTrackItem(
            id = "aud_3",
            title = "Lo-Fi Coffee Midnight",
            artist = "Chillwave Collective",
            durationText = "1:48",
            durationMs = 108000L,
            bpm = 74,
            category = "Calm",
            isFavorite = false,
            waveforms = listOf(0.3f, 0.5f, 0.4f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f)
        ),
        AudioTrackItem(
            id = "aud_4",
            title = "Future Bass Energy Surge",
            artist = "HyperNova",
            durationText = "2:32",
            durationMs = 152000L,
            bpm = 150,
            category = "Energetic",
            isFavorite = false,
            waveforms = listOf(0.5f, 0.8f, 0.95f, 0.9f, 0.85f, 0.95f, 0.7f, 0.9f)
        ),
        AudioTrackItem(
            id = "aud_5",
            title = "Podcast Modern Groove",
            artist = "Studio Broadcast",
            durationText = "1:15",
            durationMs = 75000L,
            bpm = 110,
            category = "Podcast",
            isFavorite = true,
            waveforms = listOf(0.4f, 0.5f, 0.6f, 0.5f, 0.4f, 0.5f, 0.6f, 0.5f)
        )
    )

    fun getSoundEffects(): List<AudioTrackItem> = listOf(
        AudioTrackItem(
            id = "sfx_1",
            title = "Cinematic Heavy Impact",
            artist = "Lumina FX",
            durationText = "0:02",
            durationMs = 2100L,
            bpm = 0,
            category = "Impact",
            waveforms = listOf(0.95f, 0.8f, 0.5f, 0.2f)
        ),
        AudioTrackItem(
            id = "sfx_2",
            title = "Ultra Fast Whip Whoosh",
            artist = "Lumina FX",
            durationText = "0:01",
            durationMs = 850L,
            bpm = 0,
            category = "Whoosh",
            waveforms = listOf(0.2f, 0.8f, 0.95f, 0.1f)
        ),
        AudioTrackItem(
            id = "sfx_3",
            title = "Camera Shutter Click 35mm",
            artist = "Analog Sounds",
            durationText = "0:01",
            durationMs = 600L,
            bpm = 0,
            category = "Click",
            waveforms = listOf(0.9f, 0.3f, 0.8f, 0.1f)
        ),
        AudioTrackItem(
            id = "sfx_4",
            title = "Sub Bass Drone Riser",
            artist = "Cinematic Depth",
            durationText = "0:04",
            durationMs = 4200L,
            bpm = 0,
            category = "Transition",
            waveforms = listOf(0.1f, 0.3f, 0.6f, 0.95f)
        ),
        AudioTrackItem(
            id = "sfx_5",
            title = "Glitch Data Static Burst",
            artist = "Cyber FX",
            durationText = "0:01",
            durationMs = 1100L,
            bpm = 0,
            category = "Gaming",
            waveforms = listOf(0.8f, 0.9f, 0.7f, 0.95f)
        )
    )

    fun getLutPresets(): List<LutPreset> = listOf(
        LutPreset("lut_1", "Teal & Orange", "Hollywood Blockbuster", 0xFF00E5FF),
        LutPreset("lut_2", "Cyberpunk Neon", "High-contrast Electric", 0xFFE040FB),
        LutPreset("lut_3", "Kodak Portra 400", "Warm Creamy Skin Tones", 0xFFFFA726),
        LutPreset("lut_4", "Moody Forest Slate", "Deep Greens & Shadows", 0xFF26A69A),
        LutPreset("lut_5", "Black & White Cinema", "Silver Nitrate Contrast", 0xFFCFD8DC),
        LutPreset("lut_6", "Golden Hour Glow", "Soft Sunset Diffusion", 0xFFFFD54F),
        LutPreset("lut_7", "Fuji Provia Vivid", "Punchy Commercial Colors", 0xFF42A5F5),
        LutPreset("lut_8", "Vintage VHS 1994", "Retro Warm Bleed", 0xFFFF7043)
    )

    fun getEffects(): List<EffectItem> = listOf(
        EffectItem("ef_1", "Cyber Glitch", "Glitch", "glitch", 0.75f, 0xFF00F2FE, 0xFF8B5CF6),
        EffectItem("ef_2", "Film Grain 16mm", "Film", "grain", 0.6f, 0xFFF59E0B, 0xFF78350F),
        EffectItem("ef_3", "Anamorphic Lens Flare", "Cinematic", "flare", 0.85f, 0xFF38BDF8, 0xFF6366F1),
        EffectItem("ef_4", "RGB Split Aberration", "Glitch", "rgb", 0.7f, 0xFFEC4899, 0xFF06B6D4),
        EffectItem("ef_5", "Dreamy Glow Diffuse", "Cinematic", "glow", 0.5f, 0xFFFDE047, 0xFFF43F5E),
        EffectItem("ef_6", "Light Leak Golden", "Light", "light", 0.8f, 0xFFFB923C, 0xFFEA580C)
    )

    fun getAIVoices(): List<AIVoiceProfile> = listOf(
        AIVoiceProfile("v_1", "Marcus (Pro Studio)", "American", "Male", "Deep Cinematic / Authoritative", "Create next-generation videos effortlessly."),
        AIVoiceProfile("v_2", "Sophia (Narrator)", "British RP", "Female", "Elegant / Documentary", "In the heart of the ancient forest, time stood still."),
        AIVoiceProfile("v_3", "Kai (Creator / Energetic)", "American", "Male", "Dynamic / YouTube Hype", "You won't believe what happens in the next ten seconds!"),
        AIVoiceProfile("v_4", "Elena (Calm Storyteller)", "Australian", "Female", "Soothing / Meditative", "Take a deep breath and explore the world around you."),
        AIVoiceProfile("v_5", "Alex (Tech Journalist)", "Mid-Atlantic", "Neutral", "Crisp / Informative", "Here are the top three breakthroughs changing technology today.")
    )

    fun getInitialComments(): List<TimelineComment> = listOf(
        TimelineComment("c_1", 3500L, "Sarah Lin", "Creative Director", "Can we punch in 15% on this cut to enhance subject framing?", "12m ago", false),
        TimelineComment("c_2", 8200L, "Devon Cross", "Sound Designer", "Sub-bass rumble ducked by 3dB here for dialogue clarity.", "45m ago", true),
        TimelineComment("c_3", 14000L, "Maya Patel", "Client Lead", "Love the color grade transition here, very premium look!", "2h ago", false)
    )

    fun getStoryboardSample(): List<StoryboardScene> = listOf(
        StoryboardScene(1, "Hook & Establishing", "Wide drone dolly over neon skyline, heavy rain reflections.", "The creator economy is shifting faster than ever.", 4, "Whip Pan"),
        StoryboardScene(2, "Core Problem", "POV fast handheld motion through packed creative studio.", "Old desktop workstations keep you tethered to a desk.", 5, "Zoom In"),
        StoryboardScene(3, "The Solution", "Crisp close-up of mobile device with Lumina Studio multi-track timeline.", "Until you put a complete Hollywood suite in your palm.", 6, "Cross Dissolve"),
        StoryboardScene(4, "Call to Action", "Bold typography and animated glowing UI elements.", "Create without boundaries. Anywhere, anytime.", 4, "Fade to Black")
    )
}
