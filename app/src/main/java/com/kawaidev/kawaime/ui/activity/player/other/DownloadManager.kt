package com.kawaidev.kawaime.ui.activity.player.other

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import java.io.File

@UnstableApi
object DownloadManager {
    private var downloadManager: DownloadManager? = null
    private var downloadCache: SimpleCache? = null

    fun getInstance(context: Context): DownloadManager {
        if (downloadManager == null) {
            val databaseProvider = StandaloneDatabaseProvider(context)

            val downloadCacheDir = File(context.cacheDir, "downloads")
            if (!downloadCacheDir.exists()) {
                downloadCacheDir.mkdirs()
            }
            downloadCache = SimpleCache(
                downloadCacheDir,
                NoOpCacheEvictor(),
                databaseProvider
            )

            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(30_000)
                .setReadTimeoutMs(30_000)

            val cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(downloadCache!!)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            downloadManager = DownloadManager(
                context,
                databaseProvider,
                downloadCache!!,
                httpDataSourceFactory,
                Runnable::run
            )
        }
        return downloadManager!!
    }
}