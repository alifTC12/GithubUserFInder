package com.aliftc12.githubuserfinder.domain.repository

import com.aliftc12.githubuserfinder.domain.GithubUser

interface UserRepository {
   suspend fun searchUser(page: Int, query: String): List<GithubUser>
}