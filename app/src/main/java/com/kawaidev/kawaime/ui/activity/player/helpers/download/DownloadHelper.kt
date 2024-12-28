package com.kawaidev.kawaime.ui.activity.player.helpers.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity

object DownloadHelper {
    fun startDownload(activity: PlayerActivity, url: String) {
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Episode")
            .setDescription("Downloading anime episode")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${activity.params.title}-episode${activity.params.episode}.mp4")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

        downloadManager.enqueue(request)
        Toast.makeText(activity, "Download started", Toast.LENGTH_SHORT).show()
    }
}