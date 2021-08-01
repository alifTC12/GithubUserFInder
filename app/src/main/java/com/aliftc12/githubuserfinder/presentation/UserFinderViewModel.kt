package com.aliftc12.githubuserfinder.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliftc12.githubuserfinder.domain.model.GithubUser
import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import com.aliftc12.githubuserfinder.domain.model.ResourceState
import com.aliftc12.githubuserfinder.domain.repository.UserRepository
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class UserFinderViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var currentPage by Delegates.notNull<Int>()
    private var totalData by Delegates.notNull<Int>()

    private val _githubUsers by lazy { MutableLiveData<MutableList<GithubUser>>() }
    val githubUsers: LiveData<MutableList<GithubUser>>
        get() = _githubUsers

    private val _searchUserState by lazy { MutableLiveData<SearchUserState>() }
    val searchUserState: LiveData<SearchUserState>
        get() = _searchUserState

    fun searchUser(query: String) {
        currentPage = 1
        _githubUsers.value = mutableListOf()

        viewModelScope.launch {
            _searchUserState.apply {
                value = SearchUserState.Loading
                value = when (val state = searchUser(page = 0, query = query)) {
                    is ResourceState.Error -> SearchUserState.Failed
                    is ResourceState.Success -> {
                        totalData = state.data.totalData
                        _githubUsers += state.data.data
                        SearchUserState.Succeed(state.data)
                    }
                }
            }
        }
    }

    fun loadMoreUser(query: String, amountCurrentUser: Int) {
        if (searchUserState.value !is SearchUserState.Succeed && searchUserState.value !is SearchUserState.LoadMoreState.Succeed) return

        if (amountCurrentUser >= totalData) {
            _searchUserState.value = SearchUserState.LoadMoreState.AllDataLoaded
            return
        }

        viewModelScope.launch {
            _searchUserState.apply {
                value = SearchUserState.LoadMoreState.Loading
                value = when (val state = searchUser(currentPage + 1, query)) {
                    is ResourceState.Error -> SearchUserState.LoadMoreState.Failed
                    is ResourceState.Success -> {
                        currentPage += 1

                        val githubUsers = state.data.data
                        _githubUsers += githubUsers
                        SearchUserState.LoadMoreState.Succeed(githubUsers)
                    }
                }
            }
        }
    }

    private suspend fun searchUser(page: Int, query: String): ResourceState<PageMeta<GithubUsers>> {
        return userRepository.searchUser(page, query)
    }

    private operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: mutableListOf()
        value.addAll(values)
        this.value = value
    }

}