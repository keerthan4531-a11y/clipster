package com.mokkacoding.clipster.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mokkacoding.clipster.ui.common.Route
import com.mokkacoding.clipster.ui.theme.ColorPalette2026
import com.mokkacoding.clipster.ui.theme.isAppInDarkTheme
import com.mokkacoding.clipster.ui.theme.liquidGlass
import com.mokkacoding.clipster.ui.theme.liquidGlassPressable
import com.mokkacoding.clipster.ui.theme.neomorphicGlass

data class ClipsterNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val ClipsterNavDestinations = listOf(
    ClipsterNavItem(
        route = Route.HOME,
        title = "Downloader",
        selectedIcon = Icons.Rounded.Download,
        unselectedIcon = Icons.Outlined.Download
    ),
    ClipsterNavItem(
        route = Route.DOWNLOADS,
        title = "Library",
        selectedIcon = Icons.Rounded.Folder,
        unselectedIcon = Icons.Outlined.Folder
    ),
    ClipsterNavItem(
        route = Route.TASK_LIST,
        title = "Tasks",
        selectedIcon = Icons.Rounded.Terminal,
        unselectedIcon = Icons.Outlined.Terminal
    ),
    ClipsterNavItem(
        route = Route.PROFILE,
        title = "Profile",
        selectedIcon = Icons.Rounded.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

/**
 * Echo-Music inspired Floating Liquid Glass Bottom Navigation Bar.
 * Features:
 * - 100% Apple Liquid Glass translucent pill shell
 * - Specular refraction edge borders
 * - Tactile spring press feedback
 * - Luxury 2026 Titanium Obsidian (Dark) & Ivory Porcelain (Light) adaptive rendering
 */
@Composable
fun ClipsterBottomNavBar(
    currentRoute: String?,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
            .liquidGlass(
                cornerRadius = 100.dp,
                glassAlpha = if (isDark) 0.85f else 0.78f,
                elevation = 12.dp
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ClipsterNavDestinations.forEach { item ->
                val isSelected = currentRoute == item.route

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        ColorPalette2026.HiResCyan
                    } else {
                        if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "navColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .then(
                            if (isSelected) {
                                Modifier.neomorphicGlass(
                                    cornerRadius = 100.dp,
                                    shape = RoundedCornerShape(100.dp),
                                    accentColor = ColorPalette2026.HiResCyan.copy(alpha = 0.25f)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .liquidGlassPressable {
                            if (!isSelected) {
                                onNavigateTo(item.route)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

