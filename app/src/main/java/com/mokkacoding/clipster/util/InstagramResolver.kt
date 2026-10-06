package com.mokkacoding.clipster.util

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLDecoder
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object InstagramResolver {
    private const val TAG = "InstagramResolver"
    private const val CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/"

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    fun isInstagramUrl(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("instagram.com") || lower.contains("instagr.am")
    }

    fun sanitizeInstagramUrl(url: String): String {
        val trimmed = url.trim()
        return if (isInstagramUrl(trimmed)) {
            val clean = trimmed.substringBefore("?")
            if (clean.endsWith("/")) clean else "$clean/"
        } else {
            trimmed
        }
    }

    fun extractShortcode(url: String): String {
        val regex = Regex("""/(?:reel|reels|p|tv)/([A-Za-z0-9_-]+)""")
        return regex.find(url)?.groupValues?.getOrNull(1) ?: "video"
    }

    private fun baseConvert(d: String, e: Int): Long {
        var value = 0L
        val rev = d.reversed()
        for (c in rev.indices) {
            val char = rev[c]
            val idx = CHARS.indexOf(char)
            if (idx != -1) {
                value += (idx * (e.toDouble().pow(c.toDouble()))).toLong()
            }
        }
        return value
    }

    fun unpack(h: String, n: String, t: Int, e: Int): String {
        if (e >= n.length) return ""
        val delimiter = n[e]
        val result = StringBuilder()
        var i = 0
        while (i < h.length) {
            val tokenBuilder = StringBuilder()
            while (i < h.length && h[i] != delimiter) {
                tokenBuilder.append(h[i])
                i++
            }
            i++ // skip delimiter
            var token = tokenBuilder.toString()
            if (token.isNotEmpty()) {
                for (j in n.indices) {
                    token = token.replace(n[j].toString(), j.toString())
                }
                val charCode = (baseConvert(token, e) - t).toInt()
                if (charCode in 0..65535) {
                    result.append(charCode.toChar())
                }
            }
        }
        val raw = result.toString()
        return try {
            URLDecoder.decode(raw, "UTF-8")
        } catch (_: Exception) {
            raw
        }
    }

    suspend fun resolveReel(url: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanUrl = sanitizeInstagramUrl(url)
            val shortcode = extractShortcode(cleanUrl)
            Log.d(TAG, "Resolving Instagram URL: $cleanUrl (shortcode: $shortcode)")

            val formBody = FormBody.Builder()
                .add("url", cleanUrl)
                .build()

            val request = Request.Builder()
                .url("https://snapsave.app/action.php?lang=en")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "https://snapsave.app/")
                .header("Origin", "https://snapsave.app")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response from resolver")

            val match = Regex("""\}\("(.+?)",\s*(\d+),\s*"(.+?)",\s*(\d+),\s*(\d+),\s*(\d+)\)\)""").find(responseBody)
                ?: throw Exception("Failed to decode Instagram resolver response")

            val h = match.groupValues[1]
            val n = match.groupValues[3]
            val t = match.groupValues[4].toInt()
            val e = match.groupValues[5].toInt()

            val html = unpack(h, n, t, e)
            if (html.isEmpty()) throw Exception("Failed to unpack media data")

            var videoDownloadUrl: String? = null
            var thumbnailUrl: String? = null

            // Find all rapidcdn tokens
            val tokenRegex = Regex("""token=([a-zA-Z0-9_\-\.]+)""")
            for (tMatch in tokenRegex.findAll(html)) {
                val token = tMatch.groupValues[1]
                val parts = token.split(".")
                if (parts.size >= 2) {
                    try {
                        val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
                        val jsonObj = JSONObject(payloadJson)
                        val mediaUrl = jsonObj.optString("url")
                        if (mediaUrl.isNotEmpty()) {
                            if (mediaUrl.contains(".mp4") || mediaUrl.contains("/o1/v/t2/")) {
                                videoDownloadUrl = "https://d.rapidcdn.app/v2?token=$token&dl=1"
                            } else if (mediaUrl.contains(".jpg") || mediaUrl.contains("/t51.")) {
                                thumbnailUrl = mediaUrl
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error decoding token payload", e)
                    }
                }
            }

            // Fallback: search for direct href in HTML
            if (videoDownloadUrl == null) {
                val hrefRegex = Regex("""href="(https://d\.rapidcdn\.app/v2\?[^"]+)"""")
                videoDownloadUrl = hrefRegex.find(html)?.groupValues?.get(1)?.replace("&amp;", "&")
            }

            if (videoDownloadUrl == null) {
                throw Exception("Could not find video download link for Instagram reel")
            }

            Log.d(TAG, "Successfully extracted Instagram video: $videoDownloadUrl")

            VideoInfo(
                id = shortcode,
                title = "Instagram Reel - $shortcode",
                thumbnail = thumbnailUrl,
                formats = listOf(
                    Format(
                        formatId = "hd",
                        ext = "mp4",
                        url = videoDownloadUrl,
                        vcodec = "h264",
                        acodec = "aac",
                        format = "mp4 - 720p",
                        resolution = "720p",
                    )
                ),
                webpageUrl = cleanUrl,
                originalUrl = videoDownloadUrl,
                extractor = "Instagram",
                extractorKey = "Instagram",
                ext = "mp4"
            )
        }
    }
}
