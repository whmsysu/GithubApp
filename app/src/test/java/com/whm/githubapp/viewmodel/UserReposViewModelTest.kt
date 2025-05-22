package com.whm.githubapp.viewmodel

import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.network.GitHubUserService
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserReposViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val service = mock<GitHubUserService>()
    private val sessionManager = mock<UserSessionManager>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserRepos emits repos`() = runTest {
        val fakeRepos = listOf(
            GitHubRepo(
                "TestRepo",
                "TestRepo/TestRepo",
                "desc",
                10,
                owner = mockk(relaxed = true),
                "Kotlin",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )
        whenever(service.getUserRepos(any())).thenReturn(fakeRepos)
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        val viewModel = UserReposViewModel(
            sessionManager,
            service
        )
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.repos.test {
            val state = awaitItem()
            assertEquals(state, fakeRepos)
        }
    }
}
