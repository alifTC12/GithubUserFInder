package com.aliftc12.githubuserfinder.data.repository

import com.aliftc12.githubuserfinder.data.dto.SearchUserResponse
import com.aliftc12.githubuserfinder.data.mapper.Mapper
import com.aliftc12.githubuserfinder.data.service.UserService
import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import com.aliftc12.githubuserfinder.domain.model.ResourceState
import com.aliftc12.githubuserfinder.domain.repository.UserRepository
import java.lang.Exception

class UserRepositoryImpl(
    private val userService: UserService,
    private val searchUserResponseToGithubUsersMapper: Mapper<SearchUserResponse, PageMeta<GithubUsers>>
) : UserRepository {
    override suspend fun searchUser(
        page: Int, query: String
    ): ResourceState<PageMeta<GithubUsers>> {
        return try {
            val response = userService.searchUser(page, query)
            if (response.isSuccessful) {
                val pageMetaGithubUsers = searchUserResponseToGithubUsersMapper.map(response.body()!!)
                return ResourceState.Success(pageMetaGithubUsers)
            }

            ResourceState.Error(responseCode = response.code())
        } catch (e: Exception) {
            ResourceState.Error(e)
        }
    }
}