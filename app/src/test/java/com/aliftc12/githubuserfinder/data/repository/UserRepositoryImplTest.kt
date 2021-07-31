package com.aliftc12.githubuserfinder.data.repository

import com.aliftc12.githubuserfinder.data.dto.SearchUserResponse
import com.aliftc12.githubuserfinder.data.mapper.Mapper
import com.aliftc12.githubuserfinder.data.service.UserService
import com.aliftc12.githubuserfinder.domain.model.GithubUsers
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import com.aliftc12.githubuserfinder.domain.model.ResourceState
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
class UserRepositoryImplTest {

    @MockK
    lateinit var userService: UserService

    @MockK
    lateinit var searchUserResponseToGithubUsersMapper: Mapper<SearchUserResponse, PageMeta<GithubUsers>>

    // SUT
    lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = UserRepositoryImpl(userService, searchUserResponseToGithubUsersMapper)
    }

    @Test
    fun `search user success returns valid domain model`() = runBlockingTest {
        val response = mockk<SearchUserResponse>()
        val pageMetaGithubUsers = mockk<PageMeta<GithubUsers>>()

        coEvery { userService.searchUser(any(), any()) } coAnswers { Response.success(response) }
        every { searchUserResponseToGithubUsersMapper.map(response) } answers { pageMetaGithubUsers }

        val actual = repository.searchUser(1, "Tono")

        TestCase.assertEquals(
            pageMetaGithubUsers,
            (actual as ResourceState.Success<PageMeta<GithubUsers>>).data
        )
        coVerify(exactly = 1) { userService.searchUser(1, "Tono") }
        coVerify(exactly = 1) { searchUserResponseToGithubUsersMapper.map(response) }
    }

    @Test
    fun `search user failed because response code is not 200`() = runBlockingTest {
        coEvery { userService.searchUser(any(), any()) } coAnswers {
            Response.error(
                HttpURLConnection.HTTP_BAD_GATEWAY,
                ResponseBody.create(null, "Not Found")
            )
        }

        val actual = repository.searchUser(1, "Tono")
        val expected =
            ResourceState.Error<PageMeta<GithubUsers>>(responseCode = HttpURLConnection.HTTP_BAD_GATEWAY)

        TestCase.assertEquals(expected, actual)
        coVerify(exactly = 1) { userService.searchUser(1, "Tono") }
        coVerify(exactly = 0) { searchUserResponseToGithubUsersMapper.map(any()) }
    }

    @Test
    fun `search user failed because function throw exception`() = runBlockingTest {
        val exception = IOException()
        coEvery { userService.searchUser(any(), any()) } throws exception

        val actual = repository.searchUser(1, "Tono")
        val expected =
            ResourceState.Error<PageMeta<GithubUsers>>(exception)

        TestCase.assertEquals(expected, actual)
        coVerify(exactly = 1) { userService.searchUser(1, "Tono") }
        coVerify(exactly = 0) { searchUserResponseToGithubUsersMapper.map(any()) }
    }
}