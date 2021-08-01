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
    private val _searchUserState by lazy { MutableLiveData<SearchUserState>() }
    val searchUserState: LiveData<SearchUserState>
        get() = _searchUserState

    private val totalData: Int
        get() {
            val state = searchUserState.value

            return if (state is SearchUserState.Succeed) state.pageMetaGithubUsers.totalData
            else throw NullPointerException("total data yet to initialize")
        }

    fun searchUser(query: String) {
        currentPage = 1

        viewModelScope.launch {
            _searchUserState.apply {
                value = SearchUserState.Loading
                value = when (val state = searchUser(page = 0, query = query)) {
                    is ResourceState.Error -> SearchUserState.Failed
                    is ResourceState.Success -> SearchUserState.Succeed(state.data)
                }
            }
        }
    }

    fun loadMoreUser(query: String, amountCurrentUser: Int) {
        if (amountCurrentUser >= totalData) {
            _searchUserState.value = SearchUserState.LoadMoreState.AllDataLoaded
            return
        }

        currentPage += 1
        viewModelScope.launch {
            _searchUserState.apply {
                value = SearchUserState.LoadMoreState.Loading
                value = when (val state = searchUser(currentPage, query)) {
                    is ResourceState.Error -> SearchUserState.LoadMoreState.Failed
                    is ResourceState.Success -> SearchUserState.LoadMoreState.Succeed(state.data.data)
                }
            }
        }
    }

    private suspend fun searchUser(page: Int, query: String): ResourceState<PageMeta<GithubUsers>> {
        return userRepository.searchUser(page, query)
    }

}