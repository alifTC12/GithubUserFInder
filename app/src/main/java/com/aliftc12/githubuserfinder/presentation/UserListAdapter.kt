package com.aliftc12.githubuserfinder.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aliftc12.githubuserfinder.databinding.ItemGithubUserBinding
import com.aliftc12.githubuserfinder.domain.model.GithubUser

class UserListAdapter : ListAdapter<GithubUser, RecyclerView.ViewHolder>(GithubUserDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemGithubUserBinding.inflate(layoutInflater, parent, false)
        return GithubUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GithubUserViewHolder).bind(getItem(position))
    }

    private class GithubUserViewHolder(private val view: ItemGithubUserBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun bind(user: GithubUser) {
            view.usernameTv.text = user.username
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