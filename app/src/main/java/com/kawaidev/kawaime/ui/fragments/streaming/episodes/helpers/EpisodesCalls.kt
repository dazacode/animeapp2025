package com.kawaidev.kawaime.ui.fragments.streaming.episodes.helpers

import android.content.Intent
import com.kawaidev.kawaime.network.dao.api_utils.Errors
import com.kawaidev.kawaime.network.dao.helpers.PlayerParams
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EpisodesCalls(private val fragment: EpisodesFragment) {

    fun getServers(id: String, episodes: Episodes) {
        fragment.viewModel.fetchServers(id, onResult = { servers ->
            (fragment.requireActivity() as? MainActivity)?.bottomSheets?.onBottomSheet(servers, episodes, fragment)
        }, onError = {
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onError(Errors.getErrorReasonByStatus(it.status ?: 0), it.message ?: "")
        }, onLoading = { loading ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onLoading(loading)
        })
    }

    fun getStreaming(playerParams: PlayerParams) {
        fragment.viewModel.fetchStreaming(playerParams, onResult = { streaming ->

            if (streaming.sources.isNullOrEmpty()) {
                (fragment.requireActivity() as? MainActivity)?.dialogs?.onError("Failed to load", "No sources found for episode")
                return@fetchStreaming
            }

            val intent = Intent(fragment.requireContext(), PlayerActivity::class.java)
            intent.putExtra("STREAMING", Json.encodeToString(streaming))
            intent.putExtra("params", Json.encodeToString(playerParams))
            fragment.startActivity(intent)
        }, onError = {
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onError(Errors.getErrorReasonByStatus(it.status ?: 0), it.message ?: "")
        }, onLoading = { loading ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onLoading(loading)
        })
    }
}