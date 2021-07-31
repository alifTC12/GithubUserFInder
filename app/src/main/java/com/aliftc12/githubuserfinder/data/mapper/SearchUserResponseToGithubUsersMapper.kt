package com.aliftc12.githubuserfinder.data.mapper

import com.aliftc12.githubuserfinder.data.dto.SearchUserResponse
import com.aliftc12.githubuserfinder.domain.model.GithubUser
import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta

class SearchUserResponseToGithubUsersMapper : Mapper<SearchUserResponse, PageMeta<GithubUsers>> {
    override fun map(input: SearchUserResponse): PageMeta<GithubUsers> {
        return PageMeta(
            totalData = input.totalCount,
            data = input.users.map { GithubUser(it.id, it.login, it.avatarUrl) }
        )
    }
}