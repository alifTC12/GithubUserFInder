package com.aliftc12.githubuserfinder.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.aliftc12.githubuserfinder.CoroutineTestRule
import com.aliftc12.githubuserfinder.domain.model.GithubUser
import com.aliftc12.githubuserfinder.domain.model.PageMeta
import com.aliftc12.githubuserfinder.domain.model.ResourceState
import com.aliftc12.githubuserfinder.domain.repository.UserRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class UserFinderViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var userRepository: UserRepository

    private val templateGithubUsers =
        mutableListOf(GithubUser(id = 2, username = "tono", avatarUrl = ""))

    private lateinit var viewModel: UserFinderViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = UserFinderViewModel(userRepository)
    }

    @Test
    fun `live data githubUsers should be updated when search user success`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(
                PageMeta(totalData = 1, data = templateGithubUsers)
            )
        }

        val observer =
            mockk<Observer<MutableList<GithubUser>>>() { every { onChanged(any()) } just Runs }
        viewModel.githubUsers.observeForever(observer)

        viewModel.searchUser("tono")

        verify(exactly = 1) {
            observer.onChanged(templateGithubUsers)
        }

        coVerify(exactly = 1) {
            userRepository.searchUser(1, "tono")
        }
    }

    @Test
    fun `live data githubUsers should not be updated when search user failed`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Error()
        }

        val observer =
            mockk<Observer<MutableList<GithubUser>>>() { every { onChanged(any()) } just Runs }
        viewModel.githubUsers.observeForever(observer)

        viewModel.searchUser("tono")

        verify(exactly = 0) {
            observer.onChanged(any())
        }

        coVerify(exactly = 1) {
            userRepository.searchUser(1, "tono")
        }
    }

    @Test
    fun `state search user should be loading then succeed`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(
                PageMeta(totalData = 1, data = templateGithubUsers)
            )
        }

        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.searchUser("tono")

        verifySequence {
            observer.onChanged(SearchUserState.Loading)
            observer.onChanged(SearchUserState.Succeed)
        }

        coVerify(exactly = 1) {
            userRepository.searchUser(1, "tono")
        }
    }

    @Test
    fun `state search user should be loading then have not result`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(
                PageMeta(totalData = 0, data = emptyList())
            )
        }

        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.searchUser("tono")

        verifySequence {
            observer.onChanged(SearchUserState.Loading)
            observer.onChanged(SearchUserState.HaveNoResult)
        }

        coVerify(exactly = 1) {
            userRepository.searchUser(1, "tono")
        }
    }

    @Test
    fun `state search user should be loading then error`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Error()
        }

        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.searchUser("tono")

        verifySequence {
            observer.onChanged(SearchUserState.Loading)
            observer.onChanged(SearchUserState.Failed)
        }

        coVerify(exactly = 1) {
            userRepository.searchUser(1, "tono")
        }
    }

    @Test
    fun `repository not invoked because previous state is not Succeed or LoadMoreState_Succeed`() {
        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.loadMoreUser("tono", 0)

        coVerify(exactly = 0) {
            userRepository.searchUser(any(), any())
        }
    }

    @Test
    fun `end state should be AllDataLoaded because total data reached`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(PageMeta(totalData = 1, data = emptyList()))
        }

        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.searchUser("tono")
        viewModel.loadMoreUser("tono", 1)

        verifySequence {
            observer.onChanged(SearchUserState.Loading)
            observer.onChanged(SearchUserState.Succeed)
            observer.onChanged(SearchUserState.LoadMoreState.AllDataLoaded)
        }

        coVerify(exactly = 0) {
            userRepository.searchUser(2, any())
        }
    }

    @Test
    fun `states load more scenario success should be valid`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(PageMeta(totalData = 3, data = emptyList()))
        }
        coEvery { userRepository.searchUser(2, "tono") } coAnswers {
            ResourceState.Success(PageMeta(totalData = 3, data = emptyList()))
        }

        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.searchUser("tono")
        viewModel.loadMoreUser("tono", 1)

        verifySequence {
            observer.onChanged(SearchUserState.Loading)
            observer.onChanged(SearchUserState.Succeed)
            observer.onChanged(SearchUserState.LoadMoreState.Loading)
            observer.onChanged(SearchUserState.LoadMoreState.Succeed)
        }

        coVerifySequence {
            userRepository.searchUser(1, "tono")
            userRepository.searchUser(2, "tono")
        }
    }

    @Test
    fun `states load more scenario error should be valid`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(PageMeta(totalData = 3, data = emptyList()))
        }
        coEvery { userRepository.searchUser(2, "tono") } coAnswers {
            ResourceState.Error()
        }
        val observer =
            mockk<Observer<SearchUserState>>() { every { onChanged(any()) } just Runs }
        viewModel.searchUserState.observeForever(observer)

        viewModel.searchUser("tono")
        viewModel.loadMoreUser("tono", 1)

        verifySequence {
            observer.onChanged(SearchUserState.Loading)
            observer.onChanged(SearchUserState.Succeed)
            observer.onChanged(SearchUserState.LoadMoreState.Loading)
            observer.onChanged(SearchUserState.LoadMoreState.Failed)
        }

        coVerifySequence {
            userRepository.searchUser(1, "tono")
            userRepository.searchUser(2, "tono")
        }
    }

    @Test
    fun `github user list should be updated with data from load more`() {
        coEvery { userRepository.searchUser(1, "tono") } coAnswers {
            ResourceState.Success(PageMeta(totalData = 3, data = emptyList()))
        }
        coEvery { userRepository.searchUser(2, "tono") } coAnswers {
            ResourceState.Success(
                PageMeta(
                    totalData = 3, data = mutableListOf(GithubUser(5, "Tonowi", avatarUrl = ""))
                )
            )
        }

        val observer =
            mockk<Observer<MutableList<GithubUser>>>() { every { onChanged(any()) } just Runs }
        viewModel.githubUsers.observeForever(observer)

        viewModel.searchUser("tono")
        viewModel.loadMoreUser("tono", 1)

        verifySequence {
            observer.onChanged(mutableListOf())
            observer.onChanged(mutableListOf(GithubUser(5, "Tonowi", avatarUrl = "")))
        }
    }

}