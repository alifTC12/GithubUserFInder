package com.aliftc12.githubuserfinder.data.mapper

import com.aliftc12.githubuserfinder.data.dto.SearchUserResponse
import com.aliftc12.githubuserfinder.data.dto.User
import com.aliftc12.githubuserfinder.domain.model.GithubUser
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import junit.framework.TestCase
import org.junit.Test

class SearchUserResponseToGithubUsersMapperTest {

    @Test
    fun `mapper should be returns correct domain model`() {
        val response = SearchUserResponse(
            totalCount = 10,
            users = listOf(User(id = 1L, login = "Tono", avatarUrl = "https://avatar.com"))
        )

        val pageMetaGithubUsers = PageMeta(
            totalData = 10,
            data = listOf(GithubUser(id = 1L, username = "Tono", avatarUrl = "https://avatar.com"))
        )

        val actual = SearchUserResponseToGithubUsersMapper().map(response)
        TestCase.assertEquals(pageMetaGithubUsers, actual)
    }
}