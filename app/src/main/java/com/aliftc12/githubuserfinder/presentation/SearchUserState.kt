package com.aliftc12.githubuserfinder.presentation

import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta

sealed class SearchUserState {
    // initial state, when list empty
    object Loading : SearchUserState()
    data class Succeed(val pageMetaGithubUsers: PageMeta<GithubUsers>) : SearchUserState()
    object Failed : SearchUserState()

    // load more state
    sealed class LoadMoreState() : SearchUserState() {
        object Loading : LoadMoreState()
        object Failed : LoadMoreState()
        data class Succeed(val users: GithubUsers) : LoadMoreState()
        object AllDataLoaded : LoadMoreState()
    }
}