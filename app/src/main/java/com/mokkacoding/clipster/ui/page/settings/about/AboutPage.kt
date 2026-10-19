package com.mokkacoding.clipster.ui.page.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Build
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
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Cookie
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Terminal
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mokkacoding.clipster.App
import com.mokkacoding.clipster.R
import com.mokkacoding.clipster.ui.component.BackButton
import com.mokkacoding.clipster.ui.theme.ColorPalette2026
import com.mokkacoding.clipster.ui.theme.LiquidGlassDefaults
import com.mokkacoding.clipster.ui.theme.isAppInDarkTheme
import com.mokkacoding.clipster.ui.theme.liquidGlass
import com.mokkacoding.clipster.ui.theme.liquidGlassPressable
import com.mokkacoding.clipster.ui.theme.neomorphicGlass
import com.mokkacoding.clipster.util.ToastUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit,
    onNavigateToCreditsPage: () -> Unit,
    onNavigateToUpdatePage: () -> Unit,
    onNavigateToDonatePage: () -> Unit,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isDark = isAppInDarkTheme()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val versionName = App.packageInfo.versionName ?: "2.0.0"

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ABOUT CLIPSTER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = "LIQUID GLASS EDITION // ARCHITECTURE",
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
                // Hero Header Card with Liquid Glass Refraction
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = LiquidGlassDefaults.CardCornerRadius,
                            glassAlpha = 0.78f,
                            elevation = 10.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .liquidGlass(
                                    cornerRadius = 24.dp,
                                    glassAlpha = 0.9f,
                                    borderWidth = 1.5.dp,
                                    elevation = 6.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.clipster_logo),
                                contentDescription = "Clipster Logo",
                                modifier = Modifier
                                    .size(76.dp)
                                    .padding(4.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "CLIPSTER",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 3.sp,
                                brush = Brush.horizontalGradient(
                                    listOf(ColorPalette2026.HiResCyan, ColorPalette2026.DolbyAtmosPurple)
                                )
                            )
                        )

                        Text(
                            text = "NEXT-GEN LIQUID MEDIA EXTRACTION",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ColorPalette2026.HiResCyan.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ColorPalette2026.HiResCyan.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "v$versionName",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ColorPalette2026.HiResCyan,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ColorPalette2026.DolbyAtmosPurple.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ColorPalette2026.DolbyAtmosPurple.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "PRO GLASS ENGINE",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ColorPalette2026.DolbyAtmosPurple,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Creator Spotlight Card ("mokka coding")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 20.dp,
                            glassAlpha = 0.75f,
                            elevation = 8.dp
                        )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LEAD ARCHITECT // CREATOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ColorPalette2026.HiResCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Verified Creator",
                                tint = ColorPalette2026.HiResCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            listOf(ColorPalette2026.HiResCyan, ColorPalette2026.DolbyAtmosPurple)
                                        ),
                                        shape = CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.creator_avatar),
                                    contentDescription = "mokka coding Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "mokka coding",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (isDark) Color.White else Color(0xFF1A202C)
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Visionary Developer & Designer",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ColorPalette2026.DolbyAtmosPurple.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorPalette2026.DolbyAtmosPurple.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "ORIGINAL CREATOR & DEVELOPER",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ColorPalette2026.DolbyAtmosPurple,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Instagram Official Card with Liquid Glass Press Physics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 20.dp,
                            glassAlpha = 0.75f,
                            elevation = 8.dp
                        )
                        .liquidGlassPressable {
                            openUrl("https://www.instagram.com/dark.shadow_4531?stkn=MTZua29kbXpoOGhnMA==")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCAF45))
                                    ),
                                    shape = CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.insta_avatar),
                                contentDescription = "Instagram Profile DP",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Instagram Official",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1A202C)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "@dark.shadow_4531",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = ColorPalette2026.HiResCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFD1D1D).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFD1D1D).copy(alpha = 0.35f)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = "Open Instagram",
                                    tint = Color(0xFFFD1D1D),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Liquid Glass Feature Showcase Matrix
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 20.dp,
                            glassAlpha = 0.75f,
                            elevation = 8.dp
                        )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "CORE FEATURE MATRIX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ColorPalette2026.HiResCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        GlassFeatureItem(
                            icon = Icons.Rounded.Bolt,
                            title = "Turbo Multi-Threaded Engine",
                            desc = "Aria2c-powered parallel chunk acceleration for lightning-fast downloads",
                            color = ColorPalette2026.HiResCyan
                        )
                        GlassFeatureItem(
                            icon = Icons.Rounded.Cookie,
                            title = "Encrypted Cookie Session Vault",
                            desc = "Bypass authentication barriers and capture protected video feeds",
                            color = ColorPalette2026.DolbyAtmosPurple
                        )
                        GlassFeatureItem(
                            icon = Icons.Rounded.HighQuality,
                            title = "4K / 8K HDR Stream Demuxer",
                            desc = "Lossless video and audio track splitting, remuxing & subtitle embedding",
                            color = ColorPalette2026.LosslessEmerald
                        )
                        GlassFeatureItem(
                            icon = Icons.Rounded.Terminal,
                            title = "Custom CLI Terminal Injector",
                            desc = "Execute custom yt-dlp arguments and templates straight from your device",
                            color = ColorPalette2026.VerifiedGold
                        )
                        GlassFeatureItem(
                            icon = Icons.Rounded.Lock,
                            title = "Zero Telemetry & Local Privacy",
                            desc = "All data, links, and credentials stay 100% on your local storage",
                            color = Color(0xFFFF0055)
                        )
                    }
                }
            }

            item {
                // Animated LMEB Visualizer in Liquid Glass Shell
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 20.dp,
                            glassAlpha = 0.75f,
                            elevation = 8.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIQUID GLASS VISUALIZER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ColorPalette2026.HiResCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ColorPalette2026.LosslessEmerald)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(R.raw.lmeb)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "LMEB Animation",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            item {
                // System Diagnostics / Suite Info in Tactile Glass Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 14.dp,
                            glassAlpha = 0.65f,
                            elevation = 4.dp
                        )
                        .liquidGlassPressable {
                            val info = "Clipster Pro v$versionName\nCreated by: mokka coding\nInstagram: @dark.shadow_4531\nEngine: Original Liquid Multi-Threaded Engine"
                            clipboardManager.setText(AnnotatedString(info))
                            ToastUtil.makeToast(R.string.info_copied)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Engine: Clipster Liquid Suite (v$versionName)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun GlassFeatureItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    desc: String,
    color: Color
) {
    val isDark = isAppInDarkTheme()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A202C)
                )
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}

const val weblate = "https://www.instagram.com/dark.shadow_4531"
const val YtdlpRepository = "https://github.com/yt-dlp/yt-dlp"

@Composable
fun AutoUpdateUnavailableDialog(onDismissRequest: () -> Unit = {}) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            com.mokkacoding.clipster.ui.component.ConfirmButton(stringResource(id = R.string.got_it)) { onDismissRequest() }
        },
        icon = { Icon(Icons.Rounded.Lock, null) },
        title = {
            Text(
                text = stringResource(id = R.string.feature_unavailable),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        },
        text = {
            Text(
                text = "Auto-update is managed directly by Clipster.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
        },
    )
}

