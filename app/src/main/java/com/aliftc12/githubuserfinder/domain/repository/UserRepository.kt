package com.aliftc12.githubuserfinder.domain.repository

import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import com.aliftc12.githubuserfinder.domain.model.ResourceState

interface UserRepository {
    suspend fun searchUser(page: Int, query: String): ResourceState<PageMeta<GithubUsers>>
}