package com.mokkacoding.clipster.ui.page.reel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.VolumeMute
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import com.mokkacoding.clipster.App
import com.mokkacoding.clipster.database.objects.DownloadedVideoInfo
import com.mokkacoding.clipster.ui.theme.ColorPalette2026
import com.mokkacoding.clipster.ui.theme.isAppInDarkTheme
import com.mokkacoding.clipster.ui.theme.liquidGlass
import com.mokkacoding.clipster.ui.theme.liquidGlassPressable
import com.mokkacoding.clipster.ui.theme.neomorphicGlass
import com.mokkacoding.clipster.util.DatabaseUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ClipsterVideoEditor"

enum class VideoEditorTab(val label: String, val icon: ImageVector) {
    TRIM("Trim", Icons.Outlined.ContentCut),
    CROP("Aspect", Icons.Outlined.AspectRatio),
    WATERMARK("Watermark", Icons.Outlined.TextFields),
    AUDIO("Audio", Icons.Outlined.VolumeUp),
    SPEED("Speed", Icons.Outlined.Speed)
}

enum class AspectRatioPreset(val label: String, val ratioText: String) {
    ORIGINAL("Original", "Keep"),
    REEL_9_16("9:16", "Reels/Story"),
    SQUARE_1_1("1:1", "Square"),
    LANDSCAPE_16_9("16:9", "Wide"),
    PORTRAIT_4_5("4:5", "Feed")
}

@OptIn(ExperimentalMaterial3Api::class, UnstableApi::class)
@Composable
fun VideoEditorSheet(
    videoInfo: DownloadedVideoInfo,
    videoDurationMs: Long,
    onDismissRequest: () -> Unit,
    onEditSuccess: (newVideoPath: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = isAppInDarkTheme()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val maxDurationSec = if (videoDurationMs > 0) (videoDurationMs / 1000f) else 60f
    var activeTab by remember { mutableStateOf(VideoEditorTab.TRIM) }

    // Trim state
    var trimRange by remember { mutableStateOf(0f..maxDurationSec) }

    // Crop / Aspect state
    var selectedAspect by remember { mutableStateOf(AspectRatioPreset.ORIGINAL) }

    // Watermark state
    var enableWatermark by remember { mutableStateOf(true) }
    var watermarkText by remember { mutableStateOf("@dark.shadow_4531") }
    var watermarkPosition by remember { mutableStateOf("Bottom-Right") }

    // Audio state
    var isMuted by remember { mutableStateOf(false) }

    // Speed state
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // Export state
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableFloatStateOf(0f) }
    var exportStatusText by remember { mutableStateOf("Preparing high-performance engine...") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0F1117) else Color(0xFFF8FAFC),
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "✂️ Clipster Video Studio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    )
                    Text(
                        text = "Liquid Glass Editing Suite // mokka coding",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ColorPalette2026.HiResCyan,
                            fontSize = 11.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tool Selection Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(cornerRadius = 16.dp, glassAlpha = 0.7f, elevation = 4.dp)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                VideoEditorTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.neomorphicGlass(
                                        cornerRadius = 12.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        accentColor = ColorPalette2026.HiResCyan.copy(alpha = 0.25f)
                                    )
                                } else Modifier
                            )
                            .clickable { activeTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) ColorPalette2026.HiResCyan else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ColorPalette2026.HiResCyan else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Content
            when (activeTab) {
                VideoEditorTab.TRIM -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(cornerRadius = 20.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Precision Video Trimming",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Start: ${formatSeconds(trimRange.start)}",
                                color = ColorPalette2026.HiResCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "End: ${formatSeconds(trimRange.endInclusive)}",
                                color = ColorPalette2026.HiResCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Cut: ${formatSeconds(trimRange.endInclusive - trimRange.start)}",
                                color = ColorPalette2026.DolbyAtmosPurple,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        RangeSlider(
                            value = trimRange,
                            onValueChange = { range -> trimRange = range },
                            valueRange = 0f..maxDurationSec,
                            colors = SliderDefaults.colors(
                                thumbColor = ColorPalette2026.HiResCyan,
                                activeTrackColor = ColorPalette2026.HiResCyan,
                                inactiveTrackColor = if (isDark) Color.DarkGray else Color.LightGray
                            )
                        )
                    }
                }

                VideoEditorTab.CROP -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(cornerRadius = 20.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Aspect Ratio & Formatting",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AspectRatioPreset.values().forEach { preset ->
                                val selected = selectedAspect == preset
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedAspect = preset },
                                    label = {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(preset.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(preset.ratioText, fontSize = 9.sp)
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ColorPalette2026.HiResCyan.copy(alpha = 0.25f),
                                        selectedLabelColor = ColorPalette2026.HiResCyan
                                    )
                                )
                            }
                        }
                    }
                }

                VideoEditorTab.WATERMARK -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(cornerRadius = 20.dp)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Branding Watermark",
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )

                            FilterChip(
                                selected = enableWatermark,
                                onClick = { enableWatermark = !enableWatermark },
                                label = { Text(if (enableWatermark) "Enabled" else "Disabled") }
                            )
                        }

                        if (enableWatermark) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = watermarkText,
                                onValueChange = { watermarkText = it },
                                label = { Text("Watermark Text / Signature") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ColorPalette2026.HiResCyan,
                                    focusedLabelColor = ColorPalette2026.HiResCyan
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Position:",
                                fontSize = 12.sp,
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Top-Right", "Bottom-Right", "Bottom-Left", "Center").forEach { pos ->
                                    val isPosSelected = watermarkPosition == pos
                                    FilterChip(
                                        selected = isPosSelected,
                                        onClick = { watermarkPosition = pos },
                                        label = { Text(pos, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ColorPalette2026.HiResCyan.copy(alpha = 0.2f),
                                            selectedLabelColor = ColorPalette2026.HiResCyan
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                VideoEditorTab.AUDIO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(cornerRadius = 20.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Audio Control",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isMuted) ColorPalette2026.HiResCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { isMuted = !isMuted }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Outlined.VolumeMute else Icons.Outlined.VolumeUp,
                                contentDescription = "Mute",
                                tint = if (isMuted) ColorPalette2026.HiResCyan else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isMuted) "Mute Video Audio (Silent Reel)" else "Original Audio Track",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White else Color.Black
                                )
                                Text(
                                    text = if (isMuted) "Audio will be stripped from the output stream" else "Preserves stereo soundtrack",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                VideoEditorTab.SPEED -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(cornerRadius = 20.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Playback Speed: ${playbackSpeed}x",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                                val selected = playbackSpeed == spd
                                FilterChip(
                                    selected = selected,
                                    onClick = { playbackSpeed = spd },
                                    label = { Text("${spd}x", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ColorPalette2026.HiResCyan.copy(alpha = 0.25f),
                                        selectedLabelColor = ColorPalette2026.HiResCyan
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Export Button & Progress Indicator
            if (isExporting) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(cornerRadius = 20.dp)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        progress = { exportProgress },
                        color = ColorPalette2026.HiResCyan,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${(exportProgress * 100).toInt()}% • $exportStatusText",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorPalette2026.HiResCyan
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .liquidGlass(
                            cornerRadius = 100.dp,
                            glassAlpha = 0.9f,
                            elevation = 8.dp
                        )
                        .neomorphicGlass(
                            cornerRadius = 100.dp,
                            shape = RoundedCornerShape(100.dp),
                            accentColor = ColorPalette2026.HiResCyan.copy(alpha = 0.35f)
                        )
                        .liquidGlassPressable {
                            isExporting = true
                            exportProgress = 0.05f
                            exportStatusText = "Rendering with Media3 Hardware Pipeline..."

                            scope.launch(Dispatchers.IO) {
                                try {
                                    val inputFile = File(videoInfo.videoPath)
                                    if (!inputFile.exists()) {
                                        throw Exception("Source video file not found!")
                                    }

                                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    val outputDir = inputFile.parentFile ?: context.filesDir
                                    val outputFile = File(outputDir, "Clipster_Edit_${timeStamp}.mp4")

                                    exportVideoWithMedia3(
                                        context = context,
                                        inputFile = inputFile,
                                        outputFile = outputFile,
                                        startMs = (trimRange.start * 1000).toLong(),
                                        endMs = (trimRange.endInclusive * 1000).toLong(),
                                        isMuted = isMuted,
                                        onProgress = { prog ->
                                            exportProgress = prog
                                        },
                                        onComplete = { successPath ->
                                            scope.launch(Dispatchers.Main) {
                                                isExporting = false
                                                Toast.makeText(context, "🎬 Reel exported successfully!", Toast.LENGTH_SHORT).show()
                                                onEditSuccess(successPath)
                                                onDismissRequest()
                                            }
                                        },
                                        onError = { error ->
                                            scope.launch(Dispatchers.Main) {
                                                isExporting = false
                                                Toast.makeText(context, "Export error: ${error.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    Log.e(TAG, "Export failed", e)
                                    withContext(Dispatchers.Main) {
                                        isExporting = false
                                        Toast.makeText(context, "Render failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🚀 Render & Save Reel",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ColorPalette2026.HiResCyan,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@UnstableApi
private suspend fun exportVideoWithMedia3(
    context: Context,
    inputFile: File,
    outputFile: File,
    startMs: Long,
    endMs: Long,
    isMuted: Boolean,
    onProgress: (Float) -> Unit,
    onComplete: (String) -> Unit,
    onError: (Throwable) -> Unit
) = withContext(Dispatchers.Main) {
    try {
        val clippingConfig = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(startMs)
            .setEndPositionMs(endMs)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.fromFile(inputFile))
            .setClippingConfiguration(clippingConfig)
            .build()

        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .setRemoveAudio(isMuted)
            .build()

        val transformerListener = object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                // Register newly created video in database
                try {
                    val newInfo = DownloadedVideoInfo(
                        id = 0,
                        videoTitle = "Clipster Edit - " + inputFile.nameWithoutExtension,
                        videoAuthor = "mokka coding",
                        videoUrl = "",
                        thumbnailUrl = "",
                        videoPath = outputFile.absolutePath,
                        extractor = "Clipster Studio"
                    )
                    DatabaseUtil.insertInfo(newInfo)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to insert into database", e)
                }
                onComplete(outputFile.absolutePath)
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) {
                Log.e(TAG, "Media3 export error", exportException)
                onError(exportException)
            }
        }

        val transformer = Transformer.Builder(context)
            .addListener(transformerListener)
            .build()

        transformer.start(editedMediaItem, outputFile.absolutePath)

        // Poll progress
        CoroutineScopeHelper.launchMain {
            val progressHolder = ProgressHolder()
            while (transformer.getProgress(progressHolder) != Transformer.PROGRESS_STATE_NOT_STARTED) {
                val state = transformer.getProgress(progressHolder)
                if (state == Transformer.PROGRESS_STATE_AVAILABLE) {
                    onProgress(progressHolder.progress / 100f)
                }
                delay(200)
            }
        }
    } catch (e: Throwable) {
        Log.e(TAG, "Setup failed", e)
        onError(e)
    }
}

private object CoroutineScopeHelper {
    fun launchIO(block: suspend () -> Unit) {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) { block() }
    }
    fun launchMain(block: suspend () -> Unit) {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) { block() }
    }
}

private fun formatSeconds(sec: Float): String {
    val totalSec = sec.toInt()
    val m = totalSec / 60
    val s = totalSec % 60
    return String.format(Locale.getDefault(), "%02d:%02d", m, s)
}
