package com.aliftc12.githubuserfinder.domain

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliftc12.githubuserfinder.databinding.ItemStateLoadMoreBinding
import com.aliftc12.githubuserfinder.presentation.SearchUserState.LoadMoreState

class LoadMoreStateAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var state: LoadMoreState? = null
    private var listener: LoadMoreStateAdapterInteraction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemStateLoadMoreBinding.inflate(layoutInflater, parent, false)
        return LoadMoreViewHolder(view)
    }

    fun submitState(state: LoadMoreState) {
        this.state = state
        notifyDataSetChanged()
    }

    fun setListener(listener: LoadMoreStateAdapterInteraction) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LoadMoreViewHolder).bind(state)
    }

    private inner class LoadMoreViewHolder(private val view: ItemStateLoadMoreBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun bind(state: LoadMoreState?): Unit = with(view) {
            when (state) {
                LoadMoreState.AllDataLoaded -> {
                    progressBar.visibility = GONE
                    allLoadedTv.visibility = VISIBLE
                    tryAgainTv.visibility = GONE
                }
                LoadMoreState.Failed -> {
                    progressBar.visibility = GONE
                    allLoadedTv.visibility = GONE
                    tryAgainTv.visibility = VISIBLE

                    view.tryAgainTv.setOnClickListener { listener?.retryLoadMore() }
                }
                LoadMoreState.Loading -> {
                    progressBar.visibility = VISIBLE
                    allLoadedTv.visibility = GONE
                    tryAgainTv.visibility = GONE
                }
                null,
                LoadMoreState.Succeed -> {
                    progressBar.visibility = GONE
                    allLoadedTv.visibility = GONE
                    tryAgainTv.visibility = GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (state == null) 0 else 1
    }

    interface LoadMoreStateAdapterInteraction {
        fun retryLoadMore()
    }
}