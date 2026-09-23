package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AspectRatio(val label: String, val ratio: Float, val iconLabel: String) {
    RATIO_9_16("9:16", 9f / 16f, "TikTok / Reels / Shorts"),
    RATIO_16_9("16:9", 16f / 9f, "YouTube / Cinema"),
    RATIO_1_1("1:1", 1f, "Instagram Square"),
    RATIO_4_5("4:5", 4f / 5f, "Instagram Portrait"),
    RATIO_4_3("4:3", 4f / 3f, "Classic TV"),
    RATIO_21_9("21:9", 21f / 9f, "Ultra Cinematic")
}

enum class ProjectResolution(val label: String, val width: Int, val height: Int, val badge: String) {
    RES_720P("720p HD", 1280, 720, "HD"),
    RES_1080P("1080p FHD", 1920, 1080, "FHD"),
    RES_1440P("1440p 2K", 2560, 1440, "2K"),
    RES_4K("4K UHD", 3840, 2160, "4K UHD"),
    RES_8K("8K Cinema", 7680, 4320, "8K PRO")
}

enum class ProjectStatus {
    DRAFT,
    EDITING,
    EXPORTING,
    READY,
    SYNCED
}

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationMs: Long = 18000L,
    val aspectRatio: String = AspectRatio.RATIO_9_16.name,
    val resolution: String = ProjectResolution.RES_1080P.name,
    val fps: Int = 60,
    val thumbnailGradientStart: Long = 0xFF1E293BL,
    val thumbnailGradientEnd: Long = 0xFF0F172AL,
    val accentColor: Long = 0xFF00F2FEL,
    val category: String = "Shorts",
    val status: String = ProjectStatus.EDITING.name,
    val isFavorite: Boolean = false,
    val isCloud: Boolean = true,
    val storageSizeMb: Double = 142.5,
    val createdAt: Long = System.currentTimeMillis() - 86400000L * 2,
    val updatedAt: Long = System.currentTimeMillis()
)
