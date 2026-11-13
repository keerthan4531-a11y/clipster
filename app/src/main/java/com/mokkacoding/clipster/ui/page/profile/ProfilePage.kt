package com.mokkacoding.clipster.ui.page.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cookie
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mokkacoding.clipster.App
import com.mokkacoding.clipster.R
import com.mokkacoding.clipster.database.objects.DownloadedVideoInfo
import com.mokkacoding.clipster.ui.common.AsyncImageImpl
import com.mokkacoding.clipster.ui.component.BackButton
import com.mokkacoding.clipster.ui.theme.ColorPalette2026
import com.mokkacoding.clipster.ui.theme.LiquidGlassDefaults
import com.mokkacoding.clipster.ui.theme.isAppInDarkTheme
import com.mokkacoding.clipster.ui.theme.liquidGlass
import com.mokkacoding.clipster.ui.theme.liquidGlassPressable
import com.mokkacoding.clipster.ui.theme.neomorphicGlass
import com.mokkacoding.clipster.util.DatabaseUtil
import com.mokkacoding.clipster.util.FileUtil
import com.mokkacoding.clipster.util.PreferenceUtil.getString
import com.mokkacoding.clipster.util.ToastUtil
import com.mokkacoding.clipster.util.YT_DLP_VERSION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToDownloads: () -> Unit = {},
    onNavigateToCookies: () -> Unit = {},
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isDark = isAppInDarkTheme()

    val historyList by DatabaseUtil.getDownloadHistoryFlow().collectAsStateWithLifecycle(initialValue = emptyList())
    val downloadDir = App.videoDownloadDir

    val audioCount = historyList.count {
        val path = it.videoPath.lowercase()
        path.endsWith(".mp3") || path.endsWith(".m4a") || path.endsWith(".opus") || path.endsWith(".ogg") || path.endsWith(".flac")
    }
    val videoCount = historyList.size - audioCount

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "USER PROFILE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = "CLIPSTER LIQUID CLIENT // HUB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ColorPalette2026.HiResCyan,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                },
                navigationIcon = { BackButton(onNavigateBack) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // User Hub Top Card with Liquid Glass Refraction
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 22.dp,
                            glassAlpha = 0.78f,
                            elevation = 10.dp
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .liquidGlass(
                                    cornerRadius = 38.dp,
                                    glassAlpha = 0.85f,
                                    borderWidth = 1.5.dp,
                                    elevation = 4.dp
                                )
                                .padding(6.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.clipster_logo),
                                contentDescription = "Clipster Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Clipster User",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1A202C)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = "Active Status",
                                    tint = ColorPalette2026.HiResCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "Liquid Media Vault & Client",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ColorPalette2026.LosslessEmerald.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ColorPalette2026.LosslessEmerald.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(ColorPalette2026.LosslessEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIQUID ENGINE // ONLINE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ColorPalette2026.LosslessEmerald,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // User Download Statistics
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 18.dp,
                            glassAlpha = 0.75f,
                            elevation = 6.dp
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DOWNLOAD STATISTICS & ACTIVITY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ColorPalette2026.HiResCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            UserStatBadge(
                                modifier = Modifier.weight(1f),
                                title = "Total Media",
                                value = "${historyList.size}",
                                color = ColorPalette2026.HiResCyan,
                                icon = Icons.Rounded.Download
                            )
                            UserStatBadge(
                                modifier = Modifier.weight(1f),
                                title = "Videos",
                                value = "$videoCount",
                                color = ColorPalette2026.DolbyAtmosPurple,
                                icon = Icons.Rounded.Movie
                            )
                            UserStatBadge(
                                modifier = Modifier.weight(1f),
                                title = "Audios",
                                value = "$audioCount",
                                color = ColorPalette2026.LosslessEmerald,
                                icon = Icons.Rounded.MusicNote
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Folder,
                                contentDescription = null,
                                tint = ColorPalette2026.HiResCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Download Dir: ${downloadDir.ifEmpty { "Default Storage" }}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Recent Downloads Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 18.dp,
                            glassAlpha = 0.75f,
                            elevation = 6.dp
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECENT DOWNLOADS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ColorPalette2026.HiResCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )

                            if (historyList.isNotEmpty()) {
                                Text(
                                    text = "View All (${historyList.size})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ColorPalette2026.DolbyAtmosPurple,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.clickable { onNavigateToDownloads() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (historyList.isEmpty()) {
                            Text(
                                text = "No media cached yet. Paste a link on the home screen to start downloading.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            val recentItems = historyList.takeLast(3).reversed()
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                recentItems.forEach { item ->
                                    RecentDownloadItem(
                                        item = item,
                                        onClick = {
                                            FileUtil.openFile(path = item.videoPath) {
                                                ToastUtil.makeToast(R.string.file_unavailable)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Primary Action 1: About Page
            item {
                GlassActionCard(
                    title = "About Clipster",
                    description = "Creator mokka coding, Instagram portal, liquid glass matrix & version intel",
                    icon = Icons.Rounded.Info,
                    accentColor = ColorPalette2026.DolbyAtmosPurple,
                    onClick = onNavigateToAbout
                )
            }

            // Primary Action 2: Downloads Library
            item {
                GlassActionCard(
                    title = "Downloads Library",
                    description = "Browse, play, share or delete downloaded videos and audio clips",
                    icon = Icons.Rounded.Download,
                    accentColor = ColorPalette2026.HiResCyan,
                    onClick = onNavigateToDownloads
                )
            }

            // Primary Action 3: Settings & Customization
            item {
                GlassActionCard(
                    title = "Settings & Customization",
                    description = "Video formats, audio bitrates, download directories, templates & UI theme",
                    icon = Icons.Rounded.Settings,
                    accentColor = ColorPalette2026.VerifiedBlue,
                    onClick = onNavigateToSettings
                )
            }

            // Primary Action 4: Account & Cookies (Instagram / YouTube)
            item {
                GlassActionCard(
                    title = "Account Cookies",
                    description = "Manage Instagram, YouTube or custom site logins to bypass rate limits",
                    icon = Icons.Rounded.Cookie,
                    accentColor = ColorPalette2026.VerifiedGold,
                    onClick = onNavigateToCookies
                )
            }

            // System Telemetry Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 18.dp,
                            glassAlpha = 0.75f,
                            elevation = 6.dp
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CORE ENGINES TELEMETRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ColorPalette2026.HiResCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TelemetryBadge(
                                modifier = Modifier.weight(1f),
                                title = "yt-dlp",
                                status = YT_DLP_VERSION.getString().takeIf { it.isNotEmpty() } ?: "ONLINE",
                                color = ColorPalette2026.HiResCyan,
                                icon = Icons.Rounded.Bolt
                            )
                            TelemetryBadge(
                                modifier = Modifier.weight(1f),
                                title = "FFmpeg",
                                status = "READY",
                                color = ColorPalette2026.DolbyAtmosPurple,
                                icon = Icons.Rounded.Memory
                            )
                            TelemetryBadge(
                                modifier = Modifier.weight(1f),
                                title = "Aria2c",
                                status = "BOOSTED",
                                color = ColorPalette2026.LosslessEmerald,
                                icon = Icons.Rounded.Security
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RecentDownloadItem(
    item: DownloadedVideoInfo,
    onClick: () -> Unit
) {
    val isDark = isAppInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                cornerRadius = 12.dp,
                glassAlpha = 0.65f,
                elevation = 2.dp
            )
            .liquidGlassPressable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                AsyncImageImpl(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.videoTitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF1A202C)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.extractor} • ${item.videoAuthor}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ColorPalette2026.HiResCyan,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Play",
                tint = ColorPalette2026.HiResCyan,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun UserStatBadge(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    val isDark = isAppInDarkTheme()

    Box(
        modifier = modifier
            .liquidGlass(
                cornerRadius = 14.dp,
                glassAlpha = 0.65f,
                tintColor = color,
                elevation = 4.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color.White else Color(0xFF1A202C)
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun TelemetryBadge(
    modifier: Modifier = Modifier,
    title: String,
    status: String,
    color: Color,
    icon: ImageVector
) {
    val isDark = isAppInDarkTheme()

    Box(
        modifier = modifier
            .liquidGlass(
                cornerRadius = 14.dp,
                glassAlpha = 0.65f,
                tintColor = color,
                elevation = 4.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A202C)
                )
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GlassActionCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val isDark = isAppInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                cornerRadius = 18.dp,
                glassAlpha = 0.75f,
                tintColor = accentColor,
                elevation = 6.dp
            )
            .liquidGlassPressable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1A202C)
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
