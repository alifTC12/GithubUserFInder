package com.aliftc12.githubuserfinder.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import com.aliftc12.githubuserfinder.domain.model.ResourceState
import com.aliftc12.githubuserfinder.domain.repository.UserRepository
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class UserFinderViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var currentPage by Delegates.notNull<Int>()
    private var totalData by Delegates.notNull<Int>()
    private var currentQuery = ""

    private val _githubUsers by lazy { MutableLiveData<GithubUsers>() }
    val githubUsers: LiveData<GithubUsers>
        get() = _githubUsers

    private val _searchUserState by lazy { MutableLiveData<SearchUserState>() }
    val searchUserState: LiveData<SearchUserState>
        get() = _searchUserState

    fun searchUser(query: String = currentQuery) {
        currentPage = 1
        currentQuery = query

        viewModelScope.launch {
            _searchUserState.apply {
                value = SearchUserState.Loading
                value = when (val state = searchUser(page = 1, query = query)) {
                    is ResourceState.Error -> SearchUserState.Failed
                    is ResourceState.Success -> {
                        totalData = state.data.totalData
                        if (totalData == 0) SearchUserState.HaveNoResult
                        else {
                            _githubUsers.value = state.data.data.toMutableList()
                            SearchUserState.Succeed
                        }
                    }
                }
            }
        }
    }

    fun loadMoreUser(query: String = currentQuery, amountCurrentUser: Int) {
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

                        _githubUsers += state.data.data
                        SearchUserState.LoadMoreState.Succeed
                    }
                }
            }
        }
    }

    private suspend fun searchUser(page: Int, query: String): ResourceState<PageMeta<GithubUsers>> {
        return userRepository.searchUser(page, query)
    }

    private operator fun <T> MutableLiveData<List<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: mutableListOf()
        this.value = value + values
    }

}