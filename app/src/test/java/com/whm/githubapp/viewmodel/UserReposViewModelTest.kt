package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.repository.UserRepository
import com.whm.githubapp.ui.UiState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserReposViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val userRepository = mock<UserRepository>()
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
    fun `loadUserRepos emits repos with UiState Success`() = runTest {
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
        whenever(userRepository.getUserRepos()).thenReturn(fakeRepos)
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        dispatcher.scheduler.advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(fakeRepos, (state as UiState.Success).data)
        }
    }

    @Test
    fun `loadUserRepos shows loading state initially`() = runTest {
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
        whenever(userRepository.getUserRepos()).thenReturn(fakeRepos)
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Loading)
        }
    }

    @Test
    fun `loadUserRepos shows error state on failure`() = runTest {
        val errorMessage = "Network Error"
        whenever(userRepository.getUserRepos()).thenThrow(RuntimeException(errorMessage))
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        dispatcher.scheduler.advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            assertEquals(errorMessage, (state as UiState.Error).message)
        }
    }

    @Test
    fun `loadUserRepos shows empty state when no repos`() = runTest {
        whenever(userRepository.getUserRepos()).thenReturn(emptyList())
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        dispatcher.scheduler.advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Empty)
        }
    }

    @Test
    fun `loadUserRepos does not load when token is null`() = runTest {
        whenever(sessionManager.token).thenReturn(flowOf(null))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        dispatcher.scheduler.advanceUntilIdle()
        
        verify(userRepository, never()).getUserRepos()
    }

    @Test
    fun `loadUserRepos does not load when token is empty`() = runTest {
        whenever(sessionManager.token).thenReturn(flowOf(""))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        dispatcher.scheduler.advanceUntilIdle()
        
        verify(userRepository, never()).getUserRepos()
    }

    @Test
    fun `repos state is updated correctly`() = runTest {
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
        whenever(userRepository.getUserRepos()).thenReturn(fakeRepos)
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        
        val viewModel = UserReposViewModel(sessionManager, userRepository)
        dispatcher.scheduler.advanceUntilIdle()
        
        viewModel.repos.test {
            val result = awaitItem()
            assertEquals(fakeRepos, result)
        }
    }
}
