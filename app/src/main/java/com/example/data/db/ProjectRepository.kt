package com.example.data.db

import com.example.data.model.AspectRatio
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectResolution
import com.example.data.model.ProjectStatus
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val favoriteProjects: Flow<List<ProjectEntity>> = projectDao.getFavoriteProjects()

    suspend fun getProjectById(id: Long): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun insertProject(project: ProjectEntity): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun deleteProjectById(id: Long) = projectDao.deleteProjectById(id)

    suspend fun seedInitialProjectsIfEmpty() {
        if (projectDao.getProjectsCount() == 0) {
            val samples = listOf(
                ProjectEntity(
                    name = "Neon Cyberpunk Drift",
                    durationMs = 24500L,
                    aspectRatio = AspectRatio.RATIO_9_16.name,
                    resolution = ProjectResolution.RES_4K.name,
                    fps = 60,
                    thumbnailGradientStart = 0xFF00F2FEL,
                    thumbnailGradientEnd = 0xFF8B5CF6L,
                    accentColor = 0xFF00F2FEL,
                    category = "Shorts",
                    status = ProjectStatus.EDITING.name,
                    isFavorite = true,
                    isCloud = true,
                    storageSizeMb = 312.4,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 12,
                    updatedAt = System.currentTimeMillis() - 1000 * 60 * 25
                ),
                ProjectEntity(
                    name = "Kyoto Autumn Vlog - Ep 4",
                    durationMs = 184000L,
                    aspectRatio = AspectRatio.RATIO_16_9.name,
                    resolution = ProjectResolution.RES_4K.name,
                    fps = 24,
                    thumbnailGradientStart = 0xFFF59E0BL,
                    thumbnailGradientEnd = 0xFFEF4444L,
                    accentColor = 0xFFF59E0BL,
                    category = "YouTube",
                    status = ProjectStatus.READY.name,
                    isFavorite = true,
                    isCloud = true,
                    storageSizeMb = 840.0,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
                    updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3
                ),
                ProjectEntity(
                    name = "Tech Horizon Podcast #18",
                    durationMs = 740000L,
                    aspectRatio = AspectRatio.RATIO_16_9.name,
                    resolution = ProjectResolution.RES_1080P.name,
                    fps = 30,
                    thumbnailGradientStart = 0xFF6366F1L,
                    thumbnailGradientEnd = 0xFF0F172AL,
                    accentColor = 0xFF818CF8L,
                    category = "Podcast",
                    status = ProjectStatus.SYNCED.name,
                    isFavorite = false,
                    isCloud = true,
                    storageSizeMb = 1240.5,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 72,
                    updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24
                ),
                ProjectEntity(
                    name = "HyperFitness Reel Promo",
                    durationMs = 15000L,
                    aspectRatio = AspectRatio.RATIO_9_16.name,
                    resolution = ProjectResolution.RES_1080P.name,
                    fps = 60,
                    thumbnailGradientStart = 0xFF10B981L,
                    thumbnailGradientEnd = 0xFF06B6D4L,
                    accentColor = 0xFF10B981L,
                    category = "Reels",
                    status = ProjectStatus.DRAFT.name,
                    isFavorite = false,
                    isCloud = false,
                    storageSizeMb = 98.2,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 6,
                    updatedAt = System.currentTimeMillis() - 1000 * 60 * 15
                ),
                ProjectEntity(
                    name = "Minimalist Product Launch",
                    durationMs = 30000L,
                    aspectRatio = AspectRatio.RATIO_1_1.name,
                    resolution = ProjectResolution.RES_4K.name,
                    fps = 60,
                    thumbnailGradientStart = 0xFFEC4899L,
                    thumbnailGradientEnd = 0xFF8B5CF6L,
                    accentColor = 0xFFEC4899L,
                    category = "Ad",
                    status = ProjectStatus.READY.name,
                    isFavorite = true,
                    isCloud = true,
                    storageSizeMb = 210.0,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 96,
                    updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 12
                )
            )
            for (p in samples) {
                projectDao.insertProject(p)
            }
        }
    }
}
