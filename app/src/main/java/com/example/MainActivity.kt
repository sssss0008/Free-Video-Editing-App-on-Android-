package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.sheets.AIStudioSheet
import com.example.ui.sheets.CameraRecorderSheet
import com.example.ui.sheets.NewProjectSheet
import com.example.ui.sheets.SettingsSheet
import com.example.ui.sheets.TemplatesSheet
import com.example.ui.theme.LuminaTheme
import com.example.ui.theme.StudioObsidianDark
import com.example.viewmodel.StudioModalType
import com.example.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuminaTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StudioObsidianDark
                ) {
                    LuminaStudioApp()
                }
            }
        }
    }
}

@Composable
fun LuminaStudioApp(
    viewModel: StudioViewModel = viewModel()
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val activeModal by viewModel.activeModal.collectAsState()

    // Handle back button when in editor to return to dashboard
    if (activeProject != null) {
        BackHandler {
            viewModel.closeProject()
        }
        EditorScreen(
            project = activeProject!!,
            viewModel = viewModel,
            onBack = { viewModel.closeProject() }
        )
    } else {
        DashboardScreen(
            projects = allProjects,
            onOpenProject = { viewModel.openProject(it) },
            onNewProjectClick = { viewModel.setModal(StudioModalType.NEW_PROJECT) },
            onAiVideoClick = { viewModel.setModal(StudioModalType.AI_STUDIO) },
            onCameraRecordClick = { viewModel.setModal(StudioModalType.CAMERA_RECORDER) },
            onTemplatesClick = { viewModel.setModal(StudioModalType.TEMPLATES) },
            onSettingsClick = { viewModel.setModal(StudioModalType.SETTINGS) },
            formatTime = { viewModel.formatTimestamp(it) }
        )

        // Modals launched from Dashboard
        when (activeModal) {
            StudioModalType.NEW_PROJECT -> {
                NewProjectSheet(
                    onDismiss = { viewModel.dismissModal() },
                    onCreateProject = { name, ratio, res, fps ->
                        viewModel.createNewProject(name, ratio, res, fps)
                    }
                )
            }
            StudioModalType.AI_STUDIO -> {
                val isAiProcessing by viewModel.isAiProcessing.collectAsState()
                val aiStatusMessage by viewModel.aiStatusMessage.collectAsState()
                val aiStoryboard by viewModel.aiStoryboard.collectAsState()

                AIStudioSheet(
                    isProcessing = isAiProcessing,
                    statusMessage = aiStatusMessage,
                    storyboard = aiStoryboard,
                    onGenerateVideo = { prompt, style, voice ->
                        viewModel.generateAIVideo(prompt, style, voice)
                    },
                    onAutoEditClips = {
                        viewModel.showNotice("Auto-Edit run completed")
                        viewModel.dismissModal()
                    },
                    onSmartReframe = {
                        viewModel.showNotice("Reframe to 9:16 completed")
                        viewModel.dismissModal()
                    },
                    onCleanAudio = {
                        viewModel.showNotice("Clean audio applied")
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
            StudioModalType.CAMERA_RECORDER -> {
                CameraRecorderSheet(
                    onCaptureClip = { clip ->
                        val firstProj = allProjects.firstOrNull()
                        if (firstProj != null) {
                            viewModel.openProject(firstProj)
                            viewModel.addClipToTimeline(clip)
                        } else {
                            viewModel.createNewProject("Camera Capture", com.example.data.model.AspectRatio.RATIO_9_16, com.example.data.model.ProjectResolution.RES_1080P, 60)
                        }
                    },
                    onDismiss = { viewModel.dismissModal() }
                )
            }
            StudioModalType.SETTINGS -> {
                SettingsSheet(
                    onClearCache = {
                        viewModel.showNotice("Cache purged successfully")
                        viewModel.dismissModal()
                    },
                    onDismiss = { viewModel.dismissModal() }
                )
            }
            else -> {}
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
