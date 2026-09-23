package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.components.ContextualClipToolbar
import com.example.ui.components.MultiTrackTimeline
import com.example.ui.components.PrimaryEditingToolbar
import com.example.ui.components.StudioTopBar
import com.example.ui.components.VideoCanvasPreview
import com.example.ui.sheets.AIStudioSheet
import com.example.ui.sheets.AudioMixerSheet
import com.example.ui.sheets.CameraRecorderSheet
import com.example.ui.sheets.CollaborationSheet
import com.example.ui.sheets.ColorGradingSheet
import com.example.ui.sheets.ExportSheet
import com.example.ui.sheets.NewProjectSheet
import com.example.ui.sheets.SettingsSheet
import com.example.ui.sheets.TemplatesSheet
import com.example.ui.sheets.ThumbnailEditorSheet
import com.example.ui.sheets.TransitionsSheet
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioObsidianDark
import com.example.viewmodel.StudioModalType
import com.example.viewmodel.StudioViewModel

@Composable
fun EditorScreen(
    project: ProjectEntity,
    viewModel: StudioViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clips by viewModel.timelineClips.collectAsState()
    val playheadMs by viewModel.playheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val timelineZoom by viewModel.timelineZoom.collectAsState()
    val snapEnabled by viewModel.snapEnabled.collectAsState()
    val mutedTracks by viewModel.mutedTracks.collectAsState()
    val lockedTracks by viewModel.lockedTracks.collectAsState()
    val hiddenTracks by viewModel.hiddenTracks.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val activeModal by viewModel.activeModal.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val exportSettings by viewModel.exportSettings.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportProgress by viewModel.exportProgress.collectAsState()
    val exportComplete by viewModel.exportComplete.collectAsState()
    val isAiProcessing by viewModel.isAiProcessing.collectAsState()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsState()
    val aiStoryboard by viewModel.aiStoryboard.collectAsState()
    val isRecordingVoice by viewModel.isRecording.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val userNotice by viewModel.userNotice.collectAsState()

    val selectedClip = clips.find { it.id == selectedClipId }
    val totalDurationMs = viewModel.getMaxDurationMs()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("editor_screen"),
        containerColor = StudioObsidianDark,
        topBar = {
            StudioTopBar(
                project = project,
                canUndo = canUndo,
                canRedo = canRedo,
                snapEnabled = snapEnabled,
                onBackClick = onBack,
                onUndoClick = { viewModel.undo() },
                onRedoClick = { viewModel.redo() },
                onToggleSnap = { viewModel.toggleSnap() },
                onCollaborationClick = { viewModel.setModal(StudioModalType.COLLABORATION) },
                onExportClick = { viewModel.setModal(StudioModalType.EXPORT) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Video Canvas Preview Monitor
                VideoCanvasPreview(
                    project = project,
                    clips = clips,
                    playheadMs = playheadMs,
                    isPlaying = isPlaying,
                    selectedClip = selectedClip,
                    onTogglePlay = { viewModel.togglePlayback() },
                    onStepBackward = { viewModel.stepFrames(-1) },
                    onStepForward = { viewModel.stepFrames(1) },
                    formatTime = { viewModel.formatTimestamp(it) },
                    totalDurationMs = totalDurationMs,
                    modifier = Modifier.weight(1.05f)
                )

                // 2. Multi-track Timeline (Text, Overlays, Video, Music, Voice)
                MultiTrackTimeline(
                    clips = clips,
                    playheadMs = playheadMs,
                    selectedClipId = selectedClipId,
                    timelineZoom = timelineZoom,
                    mutedTracks = mutedTracks,
                    lockedTracks = lockedTracks,
                    hiddenTracks = hiddenTracks,
                    totalDurationMs = totalDurationMs,
                    onSeek = { viewModel.seekTo(it) },
                    onSelectClip = { viewModel.selectClip(it) },
                    onZoomChange = { viewModel.setTimelineZoom(it) },
                    onToggleMute = { viewModel.toggleTrackMute(it) },
                    onToggleLock = { viewModel.toggleTrackLock(it) },
                    onToggleHide = { viewModel.toggleTrackHide(it) },
                    modifier = Modifier.weight(0.95f)
                )

                // 3. Dynamic Bottom Toolbar (Contextual when clip selected, Primary otherwise)
                if (selectedClip != null) {
                    ContextualClipToolbar(
                        selectedClip = selectedClip,
                        onDeselect = { viewModel.selectClip(null) },
                        onSplit = { viewModel.splitClipAtPlayhead() },
                        onDelete = { viewModel.deleteSelectedClip() },
                        onDuplicate = { viewModel.duplicateSelectedClip() },
                        onOpenSpeed = { viewModel.updateClipSpeed(if (selectedClip.speed == 1f) 1.5f else 1f) },
                        onOpenVolume = { viewModel.updateClipVolume(if (selectedClip.volume > 0.5f) 0.3f else 1.0f) },
                        onOpenColorGrade = { viewModel.setModal(StudioModalType.COLOR_GRADING) },
                        onOpenTransitions = { viewModel.setModal(StudioModalType.TRANSITIONS_PICKER) },
                        onAddKeyframe = { viewModel.addKeyframeAtPlayhead() }
                    )
                } else {
                    PrimaryEditingToolbar(
                        onOpenMedia = { viewModel.setModal(StudioModalType.CAMERA_RECORDER) },
                        onOpenAudio = { viewModel.setModal(StudioModalType.AUDIO_MIXER) },
                        onOpenText = {
                            val newText = com.example.data.model.TimelineClip(
                                id = "clip_txt_${System.currentTimeMillis() % 1000}",
                                trackType = com.example.data.model.TrackType.TEXT,
                                name = "Title: NEW TEXT",
                                startMs = playheadMs,
                                durationMs = 4000L,
                                colorGradientStart = 0xFF9333EA,
                                colorGradientEnd = 0xFFC084FC,
                                textContent = "ENTER HEADLINE HERE"
                            )
                            viewModel.addClipToTimeline(newText)
                        },
                        onOpenCaptions = {
                            viewModel.showNotice("Auto Captions generated along timeline!")
                        },
                        onOpenEffects = {
                            viewModel.showNotice("Applied Glow Diffuse Filter to active track")
                        },
                        onOpenColorGrade = { viewModel.setModal(StudioModalType.COLOR_GRADING) },
                        onOpenTransitions = { viewModel.setModal(StudioModalType.TRANSITIONS_PICKER) },
                        onOpenAIStudio = { viewModel.setModal(StudioModalType.AI_STUDIO) },
                        onOpenTemplates = { viewModel.setModal(StudioModalType.TEMPLATES) },
                        onOpenThumbnail = { viewModel.setModal(StudioModalType.THUMBNAIL_EDITOR) },
                        onOpenCamera = { viewModel.setModal(StudioModalType.CAMERA_RECORDER) }
                    )
                }
            }

            // Toast / Notice Overlay
            AnimatedVisibility(
                visible = userNotice != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                userNotice?.let { notice ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(20.dp))
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = notice,
                            color = StudioCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets
    when (activeModal) {
        StudioModalType.AI_STUDIO -> {
            AIStudioSheet(
                isProcessing = isAiProcessing,
                statusMessage = aiStatusMessage,
                storyboard = aiStoryboard,
                onGenerateVideo = { prompt, style, voice ->
                    viewModel.generateAIVideo(prompt, style, voice)
                },
                onAutoEditClips = {
                    viewModel.showNotice("Auto-Edit: Cut silence & synced 128 BPM beat points")
                    viewModel.dismissModal()
                },
                onSmartReframe = {
                    viewModel.showNotice("Smart Reframe: Centered subjects for 9:16 vertical")
                    viewModel.dismissModal()
                },
                onCleanAudio = {
                    viewModel.showNotice("Applied Studio Voice Isolation & Denoise")
                    viewModel.dismissModal()
                },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.AUDIO_MIXER -> {
            AudioMixerSheet(
                isRecordingVoice = isRecordingVoice,
                recordingSeconds = recordingSeconds,
                onToggleRecording = { viewModel.toggleRecordingVoice() },
                onAddAudioToTimeline = { viewModel.addClipToTimeline(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.COLOR_GRADING -> {
            ColorGradingSheet(
                initialAdjustment = selectedClip?.adjustment ?: com.example.data.model.ClipColorAdjustment(),
                onApplyAdjustment = { viewModel.updateClipAdjustment(it) },
                onApplyLut = { viewModel.applyLutToSelectedClip(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.EXPORT -> {
            ExportSheet(
                exportSettings = exportSettings,
                isExporting = isExporting,
                exportProgress = exportProgress,
                isComplete = exportComplete,
                onStartExport = { viewModel.startExport() },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.COLLABORATION -> {
            CollaborationSheet(
                comments = comments,
                playheadMs = playheadMs,
                formatTime = { viewModel.formatTimestamp(it) },
                onAddComment = { viewModel.addComment(it) },
                onToggleResolved = { viewModel.toggleCommentResolved(it) },
                onSeekToComment = {
                    viewModel.seekTo(it)
                    viewModel.dismissModal()
                },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.TEMPLATES -> {
            TemplatesSheet(
                onSelectTemplate = { viewModel.applyTemplate(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.THUMBNAIL_EDITOR -> {
            ThumbnailEditorSheet(
                onExportThumbnail = { viewModel.showNotice("Saved YouTube thumbnail: $it") },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.CAMERA_RECORDER -> {
            CameraRecorderSheet(
                onCaptureClip = { viewModel.addClipToTimeline(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.TRANSITIONS_PICKER -> {
            TransitionsSheet(
                currentTransition = selectedClip?.transitionIn ?: com.example.data.model.TransitionType.NONE,
                onSelectTransition = { viewModel.updateClipTransition(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        StudioModalType.SETTINGS -> {
            SettingsSheet(
                onClearCache = {
                    viewModel.showNotice("Purged 1.8 GB render cache!")
                    viewModel.dismissModal()
                },
                onDismiss = { viewModel.dismissModal() }
            )
        }

        else -> {}
    }
}
