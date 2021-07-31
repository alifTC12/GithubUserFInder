package com.aliftc12.githubuserfinder.domain.repository

import com.aliftc12.githubuserfinder.domain.GithubUser

interface UserRepository {
    fun searchUser(page: Int, query: String): List<GithubUser>
}