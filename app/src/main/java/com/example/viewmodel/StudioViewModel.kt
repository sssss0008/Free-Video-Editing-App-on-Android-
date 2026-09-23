package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleDataProvider
import com.example.data.db.LuminaDatabase
import com.example.data.db.ProjectRepository
import com.example.data.model.AIVoiceProfile
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrackItem
import com.example.data.model.ClipColorAdjustment
import com.example.data.model.ExportSettings
import com.example.data.model.Keyframe
import com.example.data.model.LutPreset
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectResolution
import com.example.data.model.ProjectStatus
import com.example.data.model.StoryboardScene
import com.example.data.model.TemplateItem
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineComment
import com.example.data.model.TrackType
import com.example.data.model.TransitionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StudioModalType {
    NONE,
    NEW_PROJECT,
    AI_STUDIO,
    AUDIO_MIXER,
    COLOR_GRADING,
    EXPORT,
    TEMPLATES,
    THUMBNAIL_EDITOR,
    CAMERA_RECORDER,
    COLLABORATION,
    SETTINGS,
    TRANSITIONS_PICKER,
    KEYFRAME_EDITOR,
    SPEED_CURVES
}

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProjectRepository

    val allProjects: StateFlow<List<ProjectEntity>>

    // Current Active Workspace
    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    // Timeline State
    private val _timelineClips = MutableStateFlow<List<TimelineClip>>(SampleDataProvider.getInitialClips())
    val timelineClips: StateFlow<List<TimelineClip>> = _timelineClips.asStateFlow()

    private val _playheadMs = MutableStateFlow(3200L)
    val playheadMs: StateFlow<Long> = _playheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>("clip_vid_1")
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5x to 3.0f
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private val _snapEnabled = MutableStateFlow(true)
    val snapEnabled: StateFlow<Boolean> = _snapEnabled.asStateFlow()

    private val _activeModal = MutableStateFlow(StudioModalType.NONE)
    val activeModal: StateFlow<StudioModalType> = _activeModal.asStateFlow()

    // Track States
    private val _mutedTracks = MutableStateFlow<Set<TrackType>>(emptySet())
    val mutedTracks: StateFlow<Set<TrackType>> = _mutedTracks.asStateFlow()

    private val _lockedTracks = MutableStateFlow<Set<TrackType>>(emptySet())
    val lockedTracks: StateFlow<Set<TrackType>> = _lockedTracks.asStateFlow()

    private val _hiddenTracks = MutableStateFlow<Set<TrackType>>(emptySet())
    val hiddenTracks: StateFlow<Set<TrackType>> = _hiddenTracks.asStateFlow()

    // Undo / Redo History
    private val undoStack = mutableListOf<List<TimelineClip>>()
    private val redoStack = mutableListOf<List<TimelineClip>>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Collaboration comments
    private val _comments = MutableStateFlow<List<TimelineComment>>(SampleDataProvider.getInitialComments())
    val comments: StateFlow<List<TimelineComment>> = _comments.asStateFlow()

    // Export State
    private val _exportSettings = MutableStateFlow(ExportSettings())
    val exportSettings: StateFlow<ExportSettings> = _exportSettings.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportProgress = MutableStateFlow(0f)
    val exportProgress: StateFlow<Float> = _exportProgress.asStateFlow()

    private val _exportComplete = MutableStateFlow(false)
    val exportComplete: StateFlow<Boolean> = _exportComplete.asStateFlow()

    // AI Generation State
    private val _isAiProcessing = MutableStateFlow(false)
    val isAiProcessing: StateFlow<Boolean> = _isAiProcessing.asStateFlow()

    private val _aiStoryboard = MutableStateFlow<List<StoryboardScene>>(SampleDataProvider.getStoryboardSample())
    val aiStoryboard: StateFlow<List<StoryboardScene>> = _aiStoryboard.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow("Ready to generate script & scenes")
    val aiStatusMessage: StateFlow<String> = _aiStatusMessage.asStateFlow()

    // Camera & Voice Recording
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    // Toast/Feedback
    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    private var playbackJob: Job? = null
    private var recordingJob: Job? = null
    private var exportJob: Job? = null

    init {
        val database = LuminaDatabase.getDatabase(application)
        repository = ProjectRepository(database.projectDao())

        allProjects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialProjectsIfEmpty()
        }
    }

    fun openProject(project: ProjectEntity) {
        _activeProject.value = project
        _playheadMs.value = 0L
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun closeProject() {
        pausePlayback()
        _activeProject.value = null
        _activeModal.value = StudioModalType.NONE
    }

    fun setModal(modal: StudioModalType) {
        _activeModal.value = modal
    }

    fun dismissModal() {
        _activeModal.value = StudioModalType.NONE
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val totalDuration = getMaxDurationMs()
            while (_isPlaying.value) {
                delay(33) // ~30 fps update
                val next = _playheadMs.value + 33
                if (next >= totalDuration) {
                    _playheadMs.value = 0L // Loop
                } else {
                    _playheadMs.value = next
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(timeMs: Long) {
        val total = getMaxDurationMs().coerceAtLeast(1000L)
        var clamped = timeMs.coerceIn(0L, total)

        // Snapping logic if enabled
        if (_snapEnabled.value) {
            val snapThreshold = 300L
            val snapTargets = mutableListOf<Long>(0L, total)
            _timelineClips.value.forEach { clip ->
                snapTargets.add(clip.startMs)
                snapTargets.add(clip.startMs + clip.durationMs)
            }
            for (target in snapTargets) {
                if (kotlin.math.abs(clamped - target) <= snapThreshold) {
                    clamped = target
                    break
                }
            }
        }
        _playheadMs.value = clamped
    }

    fun stepFrames(frames: Int) {
        val frameMs = 1000L / (_activeProject.value?.fps ?: 60)
        seekTo(_playheadMs.value + (frames * frameMs))
    }

    fun selectClip(clipId: String?) {
        _selectedClipId.value = clipId
    }

    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.0f)
    }

    fun toggleSnap() {
        _snapEnabled.value = !_snapEnabled.value
        showNotice(if (_snapEnabled.value) "Timeline Snapping ON" else "Timeline Snapping OFF")
    }

    fun toggleTrackLock(type: TrackType) {
        val current = _lockedTracks.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _lockedTracks.value = current
    }

    fun toggleTrackMute(type: TrackType) {
        val current = _mutedTracks.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _mutedTracks.value = current
    }

    fun toggleTrackHide(type: TrackType) {
        val current = _hiddenTracks.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _hiddenTracks.value = current
    }

    // Clip Editing Operations with Undo/Redo support
    private fun pushHistory() {
        undoStack.add(_timelineClips.value)
        if (undoStack.size > 25) undoStack.removeAt(0)
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_timelineClips.value)
            _timelineClips.value = previous
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
            showNotice("Action Undone")
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_timelineClips.value)
            _timelineClips.value = next
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
            showNotice("Action Redone")
        }
    }

    fun splitClipAtPlayhead() {
        val clipId = _selectedClipId.value ?: return
        val currentClips = _timelineClips.value
        val clip = currentClips.find { it.id == clipId } ?: return

        val playhead = _playheadMs.value
        val clipStart = clip.startMs
        val clipEnd = clip.startMs + clip.durationMs

        if (playhead in (clipStart + 200)..(clipEnd - 200)) {
            pushHistory()
            val firstDuration = playhead - clipStart
            val secondDuration = clipEnd - playhead

            val firstPart = clip.copy(durationMs = firstDuration)
            val secondPart = clip.copy(
                id = "${clip.id}_split_${System.currentTimeMillis() % 1000}",
                name = "${clip.name} (Part 2)",
                startMs = playhead,
                durationMs = secondDuration
            )

            val updated = currentClips.toMutableList()
            val index = updated.indexOfFirst { it.id == clipId }
            if (index != -1) {
                updated[index] = firstPart
                updated.add(index + 1, secondPart)
                _timelineClips.value = updated
                _selectedClipId.value = secondPart.id
                showNotice("Clip Split at ${formatTimestamp(playhead)}")
            }
        } else {
            showNotice("Move playhead inside the selected clip to split")
        }
    }

    fun deleteSelectedClip() {
        val clipId = _selectedClipId.value ?: return
        pushHistory()
        _timelineClips.value = _timelineClips.value.filterNot { it.id == clipId }
        _selectedClipId.value = null
        showNotice("Clip Deleted")
    }

    fun duplicateSelectedClip() {
        val clipId = _selectedClipId.value ?: return
        val clip = _timelineClips.value.find { it.id == clipId } ?: return
        pushHistory()
        val copy = clip.copy(
            id = "clip_dup_${System.currentTimeMillis() % 10000}",
            name = "${clip.name} (Copy)",
            startMs = clip.startMs + clip.durationMs + 200L
        )
        _timelineClips.value = _timelineClips.value + copy
        _selectedClipId.value = copy.id
        showNotice("Clip Duplicated")
    }

    fun updateClipSpeed(speed: Float) {
        val clipId = _selectedClipId.value ?: return
        pushHistory()
        _timelineClips.value = _timelineClips.value.map {
            if (it.id == clipId) {
                val oldSpeed = it.speed
                val ratio = oldSpeed / speed
                val newDuration = (it.durationMs * ratio).toLong().coerceAtLeast(300L)
                it.copy(speed = speed, durationMs = newDuration)
            } else it
        }
        showNotice("Speed set to ${speed}x")
    }

    fun updateClipVolume(volume: Float) {
        val clipId = _selectedClipId.value ?: return
        _timelineClips.value = _timelineClips.value.map {
            if (it.id == clipId) it.copy(volume = volume.coerceIn(0f, 2f)) else it
        }
    }

    fun updateClipAdjustment(adjustment: ClipColorAdjustment) {
        val clipId = _selectedClipId.value ?: return
        _timelineClips.value = _timelineClips.value.map {
            if (it.id == clipId) it.copy(adjustment = adjustment) else it
        }
    }

    fun applyLutToSelectedClip(lut: LutPreset) {
        val clipId = _selectedClipId.value ?: return
        pushHistory()
        _timelineClips.value = _timelineClips.value.map {
            if (it.id == clipId) {
                it.copy(adjustment = it.adjustment.copy(lutName = lut.name))
            } else it
        }
        showNotice("Applied LUT: ${lut.name}")
    }

    fun updateClipTransition(transition: TransitionType) {
        val clipId = _selectedClipId.value ?: return
        pushHistory()
        _timelineClips.value = _timelineClips.value.map {
            if (it.id == clipId) it.copy(transitionIn = transition) else it
        }
        showNotice("Transition: ${transition.displayName}")
    }

    fun addKeyframeAtPlayhead() {
        val clipId = _selectedClipId.value ?: return
        pushHistory()
        val playhead = _playheadMs.value
        _timelineClips.value = _timelineClips.value.map { clip ->
            if (clip.id == clipId) {
                val relativeTime = (playhead - clip.startMs).coerceIn(0L, clip.durationMs)
                val newKfs = clip.keyframes + Keyframe(timeMs = relativeTime, scale = 1.1f, rotation = 5f, opacity = 1.0f)
                clip.copy(keyframes = newKfs.sortedBy { it.timeMs })
            } else clip
        }
        showNotice("Keyframe Added at ${formatTimestamp(playhead)}")
    }

    fun addClipToTimeline(clip: TimelineClip) {
        pushHistory()
        _timelineClips.value = _timelineClips.value + clip
        _selectedClipId.value = clip.id
        showNotice("Added ${clip.name}")
    }

    fun createNewProject(
        name: String,
        aspectRatio: AspectRatio,
        resolution: ProjectResolution,
        fps: Int
    ) {
        viewModelScope.launch {
            val newProj = ProjectEntity(
                name = name.ifBlank { "Untitled Studio Project" },
                aspectRatio = aspectRatio.name,
                resolution = resolution.name,
                fps = fps,
                durationMs = 20000L,
                thumbnailGradientStart = 0xFF00F2FEL,
                thumbnailGradientEnd = 0xFF8B5CF6L,
                status = ProjectStatus.EDITING.name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.insertProject(newProj)
            val created = repository.getProjectById(newId)
            created?.let { openProject(it) }
            dismissModal()
            showNotice("Created project: ${newProj.name}")
        }
    }

    fun applyTemplate(template: TemplateItem) {
        viewModelScope.launch {
            val newProj = ProjectEntity(
                name = "${template.title} Project",
                aspectRatio = template.aspectRatio.name,
                resolution = ProjectResolution.RES_1080P.name,
                fps = 60,
                durationMs = 15000L,
                thumbnailGradientStart = template.gradientStart,
                thumbnailGradientEnd = template.gradientEnd,
                status = ProjectStatus.EDITING.name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.insertProject(newProj)
            val created = repository.getProjectById(newId)
            created?.let { openProject(it) }
            dismissModal()
            showNotice("Loaded template: ${template.title}")
        }
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        val newComment = TimelineComment(
            id = "c_${System.currentTimeMillis()}",
            timestampMs = _playheadMs.value,
            authorName = "You (Lead Creator)",
            authorRole = "Editor",
            message = text,
            createdAt = "Just now",
            isResolved = false
        )
        _comments.value = _comments.value + newComment
        showNotice("Comment pinned at ${formatTimestamp(_playheadMs.value)}")
    }

    fun toggleCommentResolved(commentId: String) {
        _comments.value = _comments.value.map {
            if (it.id == commentId) it.copy(isResolved = !it.isResolved) else it
        }
    }

    fun setExportSettings(settings: ExportSettings) {
        _exportSettings.value = settings
    }

    fun startExport() {
        _isExporting.value = true
        _exportProgress.value = 0f
        _exportComplete.value = false

        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            for (step in 1..100) {
                delay(35) // Simulated high-bitrate rendering pipeline
                _exportProgress.value = step / 100f
            }
            _isExporting.value = false
            _exportComplete.value = true
            showNotice("Export Finished Successfully!")
        }
    }

    fun generateAIVideo(prompt: String, style: String, voice: AIVoiceProfile?) {
        viewModelScope.launch {
            _isAiProcessing.value = true
            _aiStatusMessage.value = "Analyzing narrative hook & visual pacing..."
            delay(1200)
            _aiStatusMessage.value = "Generating multi-camera storyboard scenes..."
            delay(1200)
            _aiStatusMessage.value = "Synthesizing AI voiceover with ${voice?.name ?: "Marcus Pro"}..."
            delay(1000)
            _aiStatusMessage.value = "Auto-cutting beat markers & transitions..."
            delay(800)
            _isAiProcessing.value = false
            _aiStatusMessage.value = "AI Draft Timeline Ready!"
            showNotice("AI Video generated successfully!")
        }
    }

    fun toggleRecordingVoice() {
        if (_isRecording.value) {
            _isRecording.value = false
            recordingJob?.cancel()
            // Add recorded audio to timeline
            val recordedClip = TimelineClip(
                id = "rec_voice_${System.currentTimeMillis() % 1000}",
                trackType = TrackType.AUDIO_VOICE,
                name = "Voiceover Take #${(1..99).random()}",
                startMs = _playheadMs.value,
                durationMs = (_recordingSeconds.value * 1000L).coerceAtLeast(2000L),
                colorGradientStart = 0xFFD97706L,
                colorGradientEnd = 0xFFF59E0BL,
                volume = 1.0f
            )
            addClipToTimeline(recordedClip)
            _recordingSeconds.value = 0
            showNotice("Voiceover added to timeline")
        } else {
            _isRecording.value = true
            _recordingSeconds.value = 0
            recordingJob?.cancel()
            recordingJob = viewModelScope.launch {
                while (_isRecording.value) {
                    delay(1000)
                    _recordingSeconds.value += 1
                }
            }
        }
    }

    fun getMaxDurationMs(): Long {
        val maxClipEnd = _timelineClips.value.maxOfOrNull { it.startMs + it.durationMs } ?: 15000L
        val projDuration = _activeProject.value?.durationMs ?: 20000L
        return maxOf(maxClipEnd, projDuration)
    }

    fun formatTimestamp(ms: Long): String {
        val totalSec = ms / 1000
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        val frames = ((ms % 1000) / (1000f / 30f)).toInt()
        return "%02d:%02d.%02d".format(minutes, seconds, frames)
    }

    fun showNotice(msg: String) {
        _userNotice.value = msg
        viewModelScope.launch {
            delay(2400)
            if (_userNotice.value == msg) {
                _userNotice.value = null
            }
        }
    }

    fun clearNotice() {
        _userNotice.value = null
    }
}
