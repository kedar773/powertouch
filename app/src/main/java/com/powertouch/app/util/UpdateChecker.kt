package com.powertouch.app.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.powertouch.app.BuildConfig
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object UpdateChecker {

    private const val GITHUB_API_URL =
        "https://api.github.com/repos/kedar773/powertouch/releases/latest"

    data class UpdateInfo(
        val isUpdateAvailable: Boolean,
        val currentVersion: String,
        val latestVersion: String,
        val releaseNotes: String,
        val downloadUrl: String?
    )

    fun checkForUpdate(callback: (Result<UpdateInfo>) -> Unit) {
        val mainHandler = Handler(Looper.getMainLooper())

        thread {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(GITHUB_API_URL)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "PowerTouch-App")
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val json = JSONObject(response)
                    val rawTag = json.optString("tag_name", "").trim()
                    val remoteVersion = if (rawTag.startsWith("v", ignoreCase = true)) {
                        rawTag.substring(1)
                    } else {
                        rawTag
                    }
                    val body = json.optString("body", "Bug fixes and performance improvements.")

                    var downloadUrl: String? = null
                    val assets = json.optJSONArray("assets")
                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                downloadUrl = asset.optString("browser_download_url")
                                break
                            }
                        }
                    }

                    if (downloadUrl.isNullOrEmpty()) {
                        // Fallback to direct release link
                        downloadUrl = "https://github.com/kedar773/powertouch/releases/latest"
                    }

                    val currentVersion = BuildConfig.VERSION_NAME
                    val updateAvailable = isNewerVersion(currentVersion, remoteVersion)

                    val info = UpdateInfo(
                        isUpdateAvailable = updateAvailable,
                        currentVersion = currentVersion,
                        latestVersion = remoteVersion,
                        releaseNotes = body,
                        downloadUrl = downloadUrl
                    )

                    mainHandler.post {
                        callback(Result.success(info))
                    }
                } else {
                    mainHandler.post {
                        callback(Result.failure(Exception("HTTP Error ${connection.responseCode}")))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    callback(Result.failure(e))
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun isNewerVersion(current: String, remote: String): Boolean {
        try {
            val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val remoteParts = remote.split(".").map { it.toIntOrNull() ?: 0 }
            val maxLen = maxOf(currentParts.size, remoteParts.size)

            for (i in 0 until maxLen) {
                val c = currentParts.getOrElse(i) { 0 }
                val r = remoteParts.getOrElse(i) { 0 }
                if (r > c) return true
                if (r < c) return false
            }
        } catch (_: Exception) {
            return false
        }
        return false
    }
}
