package com.mokkacoding.clipster.ui.page

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cookie
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mokkacoding.clipster.R
import com.mokkacoding.clipster.ui.theme.ColorPalette2026
import com.mokkacoding.clipster.ui.theme.LiquidGlassDefaults
import com.mokkacoding.clipster.ui.theme.isAppInDarkTheme
import com.mokkacoding.clipster.ui.theme.liquidGlass
import com.mokkacoding.clipster.ui.theme.liquidGlassPressable
import com.mokkacoding.clipster.ui.theme.neomorphicGlass
import com.mokkacoding.clipster.util.PreferenceUtil
import com.mokkacoding.clipster.util.PreferenceUtil.getBoolean

private const val PREF_FIRST_LAUNCH_COMPLETED = "clipster_first_launch_completed_v1"

@Composable
fun WelcomeDialog(onDismiss: () -> Unit = {}) {
    val context = LocalContext.current
    val alreadyShown = PREF_FIRST_LAUNCH_COMPLETED.getBoolean()
    var showDialog by remember { mutableStateOf(!alreadyShown) }
    val isDark = isAppInDarkTheme()

    if (!showDialog) return

    val dismissAction = {
        PreferenceUtil.updateValue(PREF_FIRST_LAUNCH_COMPLETED, true)
        showDialog = false
        onDismiss()
    }

    Dialog(
        onDismissRequest = dismissAction,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .liquidGlass(
                    cornerRadius = LiquidGlassDefaults.DialogCornerRadius,
                    glassAlpha = 0.88f,
                    elevation = 16.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .liquidGlass(
                            cornerRadius = 20.dp,
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
                            .size(60.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "WELCOME TO CLIPSTER",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        brush = Brush.horizontalGradient(
                            listOf(ColorPalette2026.HiResCyan, ColorPalette2026.DolbyAtmosPurple)
                        )
                    )
                )

                Text(
                    text = "LIQUID GLASS MEDIA EXTRACTION SUITE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Creator & Instagram Profile Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 16.dp,
                            glassAlpha = 0.75f,
                            elevation = 4.dp
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(ColorPalette2026.HiResCyan, ColorPalette2026.DolbyAtmosPurple)
                                    ),
                                    CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.creator_avatar),
                                contentDescription = "Creator Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "mokka coding",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1A202C)
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = null,
                                    tint = ColorPalette2026.HiResCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Creator & Developer",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Insta DP link with Liquid Glass Pressable
                        Surface(
                            shape = CircleShape,
                            modifier = Modifier
                                .size(38.dp)
                                .liquidGlassPressable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.instagram.com/dark.shadow_4531?stkn=MTZua29kbXpoOGhnMA==")
                                    )
                                    context.startActivity(intent)
                                },
                            color = Color(0xFFFD1D1D).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFD1D1D).copy(alpha = 0.4f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = "Insta",
                                    tint = Color(0xFFFD1D1D),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WelcomeFeatureRow(
                        icon = Icons.Rounded.Bolt,
                        title = "Turbo Acceleration",
                        subtitle = "Multi-threaded extraction with Aria2c & yt-dlp",
                        color = ColorPalette2026.HiResCyan
                    )
                    WelcomeFeatureRow(
                        icon = Icons.Rounded.HighQuality,
                        title = "4K / 8K HDR Support",
                        subtitle = "High quality video and lossless audio downloads",
                        color = ColorPalette2026.DolbyAtmosPurple
                    )
                    WelcomeFeatureRow(
                        icon = Icons.Rounded.Cookie,
                        title = "Session Cookie Vault",
                        subtitle = "Authenticate and unlock private media easily",
                        color = ColorPalette2026.LosslessEmerald
                    )
                    WelcomeFeatureRow(
                        icon = Icons.Rounded.Terminal,
                        title = "Custom CLI Injector",
                        subtitle = "Full terminal command power in your hands",
                        color = ColorPalette2026.VerifiedGold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Initialize Button in Liquid Neomorphic Style
                Button(
                    onClick = dismissAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPalette2026.HiResCyan,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "ENTER CLIPSTER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color
) {
    val isDark = isAppInDarkTheme()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(10.dp),
            color = color.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A202C)
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}
