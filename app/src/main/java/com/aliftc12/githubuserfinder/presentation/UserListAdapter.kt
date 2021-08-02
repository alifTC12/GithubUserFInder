package com.aliftc12.githubuserfinder.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aliftc12.githubuserfinder.R
import com.aliftc12.githubuserfinder.databinding.ItemGithubUserBinding
import com.aliftc12.githubuserfinder.domain.model.GithubUser
import com.bumptech.glide.RequestManager

class UserListAdapter(private val requestManager: RequestManager) :
    ListAdapter<GithubUser, RecyclerView.ViewHolder>(GithubUserDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemGithubUserBinding.inflate(layoutInflater, parent, false)
        return GithubUserViewHolder(view, requestManager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GithubUserViewHolder).bind(getItem(position))
    }

    private class GithubUserViewHolder(
        private val view: ItemGithubUserBinding,
        private val requestManager: RequestManager
    ) :
        RecyclerView.ViewHolder(view.root) {

        fun bind(user: GithubUser) {
            view.usernameTv.text = user.username
            requestManager
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(view.avatarIv)
        }
    }
}


private object GithubUserDiffCallback : DiffUtil.ItemCallback<GithubUser>() {
    override fun areItemsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean {
        return oldItem == newItem
    }

}