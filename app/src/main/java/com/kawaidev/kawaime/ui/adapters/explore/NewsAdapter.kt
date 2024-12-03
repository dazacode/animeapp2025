package com.kawaidev.kawaime.ui.adapters.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.ui.adapters.diffs.NewsDiffCallback
import com.kawaidev.kawaime.utils.LoadImage

class NewsAdapter(
    private var news: List<News>,
    private var isLoading: Boolean = false,
    private var isEmpty: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_VIEW = 1
        private const val EMPTY_VIEW = 2
        private const val LOADING_VIEW = 3
        private const val FOOTER_VIEW = 4
    }

    override fun getItemCount(): Int {
        return when {
            isEmpty -> 1
            isLoading && news.isEmpty() -> 1
            isLoading -> news.size + 1
            else -> news.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isEmpty -> EMPTY_VIEW
            isLoading && news.isEmpty() -> LOADING_VIEW
            position == news.size && isLoading -> FOOTER_VIEW
            else -> ITEM_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_item, parent, false)
                NewsViewHolder(view)
            }
            EMPTY_VIEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_view, parent, false)
                EmptyViewHolder(view)
            }
            LOADING_VIEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_view, parent, false)
                LoadingViewHolder(view)
            }
            FOOTER_VIEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.bottom_loading_view, parent, false)
                FooterViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsViewHolder -> holder.bind(news[position])
            is EmptyViewHolder -> holder.bind()
            is LoadingViewHolder -> holder.bind()
            is FooterViewHolder -> holder.bind()
        }
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val title: TextView = itemView.findViewById(R.id.newsTitle)

        fun bind(news: News) {
            LoadImage().loadImage(itemView.context, news.thumbnail, image)
            title.text = news.title
        }
    }

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {}
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {}
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {}
    }

    fun setData(news: List<News>) {
        val cleanNewsList = news
            .filter { it.id.isNullOrEmpty().not()}
            .distinctBy { it.id }

        val diffCallback = NewsDiffCallback(this.news, cleanNewsList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.news = cleanNewsList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setLoading(isLoading: Boolean) {
        val previousState = this@NewsAdapter.isLoading
        this@NewsAdapter.isLoading = isLoading

        if (news.isEmpty()) {
            if (previousState != isLoading) {
                if (isLoading) {
                    notifyItemInserted(0)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }
    }

    fun setEmpty(isEmpty: Boolean) {
        val previousState = this@NewsAdapter.isEmpty
        this@NewsAdapter.isEmpty = isEmpty

        if (news.isEmpty() && previousState != isEmpty) {
            if (isEmpty) {
                notifyItemInserted(0)
            } else {
                notifyItemRemoved(0)
            }
        }
    }
}