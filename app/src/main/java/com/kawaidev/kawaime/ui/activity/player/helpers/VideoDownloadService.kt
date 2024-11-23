package com.kawaidev.kawaime.ui.activity.player.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.offline.DownloadService.DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL
import androidx.media3.exoplayer.scheduler.Scheduler
import com.kawaidev.kawaime.R
import java.io.File

@OptIn(UnstableApi::class)
class VideoDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    CHANNEL_ID,
    R.string.channel_name,
    R.string.channel_description
) {
    override fun getDownloadManager(): androidx.media3.exoplayer.offline.DownloadManager {
        return DownloadManager.getInstance(applicationContext)
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.download)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (downloads.isEmpty()) {
            builder.setContentTitle(getString(R.string.no_active_downloads))
                .setContentText(getString(R.string.waiting_for_downloads))
            return builder.build()
        }

        // Handle multiple download states
        downloads.forEach { download ->
            val downloadId = download.request.id
            when (download.state) {
                Download.STATE_COMPLETED -> {
                    builder.setContentTitle("Download Complete")
                        .setContentText("Download completed for ID: $downloadId")
                        .setProgress(0, 0, false)
                        .setAutoCancel(false)
                        .setContentIntent(createSuccessIntent(downloadId))
                }
                Download.STATE_FAILED -> {
                    builder.setContentTitle("Download Failed")
                        .setContentText("Download failed for ID: $downloadId")
                        .setProgress(0, 0, false)
                }
                Download.STATE_STOPPED -> {
                    builder.setContentTitle("Download Paused")
                        .setContentText("Download paused for ID: $downloadId")
                        .setProgress(0, 0, false)
                }
                Download.STATE_DOWNLOADING -> {
                    builder.setContentTitle("Downloading...")
                        .setContentText("Downloading ID: $downloadId")
                        .setProgress(100, calculateProgress(downloads), false)
                }
                Download.STATE_QUEUED -> {
                    builder.setContentTitle("Download Queued")
                        .setContentText("Queued download for ID: $downloadId")
                        .setProgress(0, 0, false)
                }
                else -> {
                    builder.setContentTitle("Download Notification")
                        .setContentText("Download state updated for ID: $downloadId")
                        .setProgress(0, 0, false)
                }
            }
        }

        return builder.build()
    }

    private fun calculateProgress(downloads: MutableList<Download>): Int {
        val totalProgress = downloads.filter { it.state == Download.STATE_DOWNLOADING }
            .sumOf { it.percentDownloaded.toInt() }
        return totalProgress / downloads.size
    }

    private fun createSuccessIntent(id: String): PendingIntent {
        val videoFilePath = getDownloadedFilePath(id)
        val videoUri = Uri.parse(videoFilePath)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(videoUri, "video/*")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getDownloadedFilePath(id: String): String {
        return File(applicationContext.cacheDir, "$id.mp4").absolutePath
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Download Notifications",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val FOREGROUND_NOTIFICATION_ID = 1
    }
}