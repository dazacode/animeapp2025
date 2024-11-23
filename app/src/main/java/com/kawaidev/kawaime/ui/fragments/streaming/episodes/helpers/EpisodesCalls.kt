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
            val intent = Intent(fragment.requireContext(), PlayerActivity::class.java)
            if (playerParams.download) {
                (fragment.requireActivity() as? MainActivity)?.dialogs?.onQuality {
                    intent.putExtra("streaming", Json.encodeToString(streaming))
                    intent.putExtra("id", playerParams.animeEpisodeId)
                    intent.putExtra("quality", it)
                    fragment.startActivity(intent)
                }
            } else {
                intent.putExtra("streaming", Json.encodeToString(streaming))
                intent.putExtra("episodes", Json.encodeToString(playerParams.episodes))
                intent.putExtra("id", playerParams.animeEpisodeId)
                intent.putExtra("name", playerParams.title)
                intent.putExtra("episode", playerParams.episode)
                intent.putExtra("server", playerParams.server)
                intent.putExtra("category", playerParams.category)
                fragment.startActivity(intent)
            }
        }, onError = {
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onError(Errors.getErrorReasonByStatus(it.status ?: 0), it.message ?: "")
        }, onLoading = { loading ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onLoading(loading)
        })
    }
}