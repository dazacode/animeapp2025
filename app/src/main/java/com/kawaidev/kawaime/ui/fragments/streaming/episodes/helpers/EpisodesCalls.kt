package com.kawaidev.kawaime.ui.fragments.streaming.episodes.helpers

import android.content.Intent
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EpisodesCalls(private val fragment: EpisodesFragment) {

    fun getServers(id: String) {
        fragment.viewModel.fetchServers(id, onResult = { servers ->
            (fragment.requireActivity() as? MainActivity)?.bottomSheets?.onBottomSheet(servers, fragment)
        }, onError = { error ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onError(error)
        }, onLoading = { loading ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onLoading(loading)
        })
    }

    fun getStreaming(streamingParams: StreamingParams) {
        fragment.viewModel.fetchStreaming(streamingParams, onResult = { streaming ->
            val intent = Intent(fragment.requireContext(), PlayerActivity::class.java)
            intent.putExtra("streaming", Json.encodeToString(streaming))
            intent.putExtra("id", streamingParams.animeEpisodeId)
            fragment.startActivity(intent)
        }, onError = { error ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onError(error)
        }, onLoading = { loading ->
            (fragment.requireActivity() as? MainActivity)?.dialogs?.onLoading(loading)
        })
    }
}