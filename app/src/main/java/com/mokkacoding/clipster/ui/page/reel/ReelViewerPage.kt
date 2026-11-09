package com.mokkacoding.clipster.ui.page.reel

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VolumeMute
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.mokkacoding.clipster.database.objects.DownloadedVideoInfo
import com.mokkacoding.clipster.ui.theme.ColorPalette2026
import com.mokkacoding.clipster.ui.theme.liquidGlass
import com.mokkacoding.clipster.ui.theme.liquidGlassPressable
import com.mokkacoding.clipster.ui.theme.neomorphicGlass
import com.mokkacoding.clipster.util.DatabaseUtil
import com.mokkacoding.clipster.util.FileUtil
import java.io.File
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun ReelViewerPage(
    initialVideoId: Int = 0,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fetch downloaded video items from Room database
    val videoListFlow = remember { DatabaseUtil.getDownloadHistoryFlow() }
    val allVideos: List<DownloadedVideoInfo> by videoListFlow.collectAsState(initial = emptyList())

    // Filter to existing video files
    val validVideos = remember(allVideos) {
        allVideos.filter {
            val file = File(it.videoPath)
            file.exists() && (it.videoPath.endsWith(".mp4", ignoreCase = true) ||
                    it.videoPath.endsWith(".mkv", ignoreCase = true) ||
                    it.videoPath.endsWith(".webm", ignoreCase = true))
        }
    }

    if (validVideos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎬 No reels available in library", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        }
        return
    }

    val initialIndex = remember(validVideos, initialVideoId) {
        val found = validVideos.indexOfFirst { it.id == initialVideoId }
        if (found >= 0) found else 0
    }

    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { validVideos.size })
    var editingVideoInfo by remember { mutableStateOf<DownloadedVideoInfo?>(null) }
    var currentVideoDurationMs by remember { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { validVideos[it].id }
        ) { page ->
            val video = validVideos[page]
            val isCurrentPage = pagerState.currentPage == page

            ReelPlayerItem(
                videoInfo = video,
                isPageActive = isCurrentPage,
                onEditClick = { info, duration ->
                    editingVideoInfo = info
                    currentVideoDurationMs = duration
                },
                onNavigateBack = onNavigateBack,
                pageIndex = page + 1,
                totalPages = validVideos.size
            )
        }

        // Built-in Video Editing Suite Sheet
        editingVideoInfo?.let { info ->
            VideoEditorSheet(
                videoInfo = info,
                videoDurationMs = currentVideoDurationMs,
                onDismissRequest = { editingVideoInfo = null },
                onEditSuccess = { newPath ->
                    editingVideoInfo = null
                }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun ReelPlayerItem(
    videoInfo: DownloadedVideoInfo,
    isPageActive: Boolean,
    onEditClick: (DownloadedVideoInfo, Long) -> Unit,
    onNavigateBack: () -> Unit,
    pageIndex: Int,
    totalPages: Int
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(true) }
    var resizeMode by remember { mutableStateOf(AspectRatioFrameLayout.RESIZE_MODE_ZOOM) }
    var showPlayPauseBadge by remember { mutableStateOf(false) }
    var showHeartAnimation by remember { mutableStateOf(false) }

    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }

    // ExoPlayer Instance
    val exoPlayer = remember(videoInfo.videoPath) {
        ExoPlayer.Builder(context).build().apply {
            val file = File(videoInfo.videoPath)
            if (file.exists()) {
                val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
            }
        }
    }

    DisposableEffect(videoInfo.videoPath) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Playback control when page active
    LaunchedEffect(isPageActive, isPlaying) {
        if (isPageActive && isPlaying) {
            exoPlayer.play()
            while (isPageActive && isPlaying) {
                currentPositionMs = exoPlayer.currentPosition
                val dur = exoPlayer.duration
                if (dur > 0) durationMs = dur
                delay(200)
            }
        } else {
            exoPlayer.pause()
        }
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showHeartAnimation = true
                    },
                    onTap = {
                        isPlaying = !isPlaying
                        showPlayPauseBadge = true
                    }
                )
            }
    ) {
        // ExoPlayer AndroidView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    this.resizeMode = resizeMode
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.resizeMode = resizeMode
            },
            modifier = Modifier.fillMaxSize()
        )

        // Center Animated Play / Pause Indicator
        LaunchedEffect(showPlayPauseBadge) {
            if (showPlayPauseBadge) {
                delay(800)
                showPlayPauseBadge = false
            }
        }

        AnimatedVisibility(
            visible = showPlayPauseBadge,
            enter = scaleIn(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .liquidGlass(cornerRadius = 100.dp, glassAlpha = 0.8f)
                    .neomorphicGlass(
                        cornerRadius = 100.dp,
                        shape = CircleShape,
                        accentColor = ColorPalette2026.HiResCyan.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = null,
                    tint = ColorPalette2026.HiResCyan,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Center Double Tap Heart Animation
        LaunchedEffect(showHeartAnimation) {
            if (showHeartAnimation) {
                delay(800)
                showHeartAnimation = false
            }
        }

        AnimatedVisibility(
            visible = showHeartAnimation,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = Color(0xFFFF2A6D),
                modifier = Modifier.size(80.dp)
            )
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .liquidGlass(cornerRadius = 100.dp, glassAlpha = 0.75f, elevation = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Counter Capsule
            Box(
                modifier = Modifier
                    .liquidGlass(cornerRadius = 100.dp, glassAlpha = 0.75f, elevation = 6.dp)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Clipster Reels • $pageIndex / $totalPages",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }

            // Quick Edit Header Button
            Box(
                modifier = Modifier
                    .liquidGlass(cornerRadius = 100.dp, glassAlpha = 0.85f, elevation = 6.dp)
                    .neomorphicGlass(
                        cornerRadius = 100.dp,
                        shape = RoundedCornerShape(100.dp),
                        accentColor = ColorPalette2026.HiResCyan.copy(alpha = 0.3f)
                    )
                    .liquidGlassPressable {
                        onEditClick(videoInfo, durationMs)
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCut,
                        contentDescription = "Edit",
                        tint = ColorPalette2026.HiResCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Edit",
                        color = ColorPalette2026.HiResCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Right-Side Floating Actions Pill
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp)
                .liquidGlass(cornerRadius = 24.dp, glassAlpha = 0.8f, elevation = 8.dp)
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Edit Video Button
            ReelActionIcon(
                icon = Icons.Outlined.ContentCut,
                label = "Edit",
                tint = ColorPalette2026.HiResCyan,
                onClick = { onEditClick(videoInfo, durationMs) }
            )

            // Mute / Unmute Button
            ReelActionIcon(
                icon = if (isMuted) Icons.Outlined.VolumeMute else Icons.Outlined.VolumeUp,
                label = if (isMuted) "Muted" else "Audio",
                tint = if (isMuted) Color(0xFFFF5252) else Color.White,
                onClick = { isMuted = !isMuted }
            )

            // Loop Toggle Button
            ReelActionIcon(
                icon = Icons.Outlined.Loop,
                label = if (isLooping) "Loop On" else "Loop Off",
                tint = if (isLooping) ColorPalette2026.HiResCyan else Color.Gray,
                onClick = { isLooping = !isLooping }
            )

            // Aspect Ratio Toggle (Zoom vs Fit)
            ReelActionIcon(
                icon = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) Icons.Outlined.CropFree else Icons.Outlined.Fullscreen,
                label = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) "Fill" else "Fit",
                tint = Color.White,
                onClick = {
                    resizeMode = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                    } else {
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                }
            )

            // Share File Button
            ReelActionIcon(
                icon = Icons.Outlined.Share,
                label = "Share",
                tint = Color.White,
                onClick = {
                    FileUtil.createIntentForSharingFile(videoInfo.videoPath)?.let { shareIntent ->
                        context.startActivity(Intent.createChooser(shareIntent, "Share Reel"))
                    }
                }
            )
        }

        // Bottom Info & Scrubber Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                .liquidGlass(cornerRadius = 24.dp, glassAlpha = 0.85f, elevation = 12.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Video Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = videoInfo.videoTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (videoInfo.videoAuthor.isNotEmpty()) videoInfo.videoAuthor else "mokka coding media",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ColorPalette2026.HiResCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Time Indicator Pill
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${formatDuration(currentPositionMs)} / ${formatDuration(durationMs)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Timeline Scrubber Slider
            if (durationMs > 0) {
                Slider(
                    value = currentPositionMs.toFloat().coerceIn(0f, durationMs.toFloat()),
                    onValueChange = { newPosition ->
                        currentPositionMs = newPosition.toLong()
                        exoPlayer.seekTo(currentPositionMs)
                    },
                    valueRange = 0f..durationMs.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = ColorPalette2026.HiResCyan,
                        activeTrackColor = ColorPalette2026.HiResCyan,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ReelActionIcon(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .liquidGlassPressable { onClick() }
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val m = totalSec / 60
    val s = totalSec % 60
    return String.format(Locale.getDefault(), "%02d:%02d", m, s)
}
