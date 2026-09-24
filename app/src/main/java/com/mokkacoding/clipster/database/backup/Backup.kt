package com.mokkacoding.clipster.database.backup

import com.mokkacoding.clipster.database.objects.CommandTemplate
import com.mokkacoding.clipster.database.objects.DownloadedVideoInfo
import com.mokkacoding.clipster.database.objects.OptionShortcut
import kotlinx.serialization.Serializable

@Serializable
data class Backup(
    val templates: List<CommandTemplate>? = null,
    val shortcuts: List<OptionShortcut>? = null,
    val downloadHistory: List<DownloadedVideoInfo>? = null,
)
