package com.aliftc12.githubuserfinder.presentation

sealed class SearchUserState {
    // initial state, when list empty
    object Loading : SearchUserState()
    object Succeed : SearchUserState()
    object HaveNoResult : SearchUserState()
    object Failed : SearchUserState()

    // load more state
    sealed class LoadMoreState() : SearchUserState() {
        object Loading : LoadMoreState()
        object Failed : LoadMoreState()
        object Succeed : LoadMoreState()
        object AllDataLoaded : LoadMoreState()
    }
}