package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.repository.UserRepository
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
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val userSessionManager = mock<UserSessionManager>()
    private val userRepository = mock<UserRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = AuthViewModel(userSessionManager, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `user is loaded when token is available`() = runTest {
        val mockUser = GitHubUser(
            id = 123,
            login = "testuser",
            name = "Test User",
            bio = "Test bio",
            publicRepos = 10,
            followers = 5,
            following = 3
        )
        whenever(userSessionManager.token).thenReturn(flowOf("valid_token"))
        whenever(userRepository.getUserInfo()).thenReturn(mockUser)

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.user.test {
            val result = awaitItem()
            assertEquals(mockUser, result)
        }
    }

    @Test
    fun `user is null when token is null`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf(null))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.user.test {
            val result = awaitItem()
            assertNull(result)
        }
    }

    @Test
    fun `user is null when token is empty`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf(""))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.user.test {
            val result = awaitItem()
            assertNull(result)
        }
    }

    @Test
    fun `loading is false when token is null`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf(null))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loading.test {
            val result = awaitItem()
            assertFalse(result)
        }
    }

    @Test
    fun `loading is false when token is empty`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf(""))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loading.test {
            val result = awaitItem()
            assertFalse(result)
        }
    }

    @Test
    fun `loading is true initially when token is available`() = runTest {
        val mockUser = GitHubUser(
            id = 123,
            login = "testuser",
            name = "Test User",
            bio = "Test bio",
            publicRepos = 10,
            followers = 5,
            following = 3
        )
        whenever(userSessionManager.token).thenReturn(flowOf("valid_token"))
        whenever(userRepository.getUserInfo()).thenReturn(mockUser)

        viewModel.loading.test {
            val result = awaitItem()
            assertTrue(result)
        }
    }

    @Test
    fun `loading becomes false after user is loaded`() = runTest {
        val mockUser = GitHubUser(
            id = 123,
            login = "testuser",
            name = "Test User",
            bio = "Test bio",
            publicRepos = 10,
            followers = 5,
            following = 3
        )
        whenever(userSessionManager.token).thenReturn(flowOf("valid_token"))
        whenever(userRepository.getUserInfo()).thenReturn(mockUser)

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loading.test {
            val result = awaitItem()
            assertFalse(result)
        }
    }

    @Test
    fun `logout clears user and token`() = runTest {
        val mockUser = GitHubUser(
            id = 123,
            login = "testuser",
            name = "Test User",
            bio = "Test bio",
            publicRepos = 10,
            followers = 5,
            following = 3
        )
        whenever(userSessionManager.token).thenReturn(flowOf("valid_token"))
        whenever(userRepository.getUserInfo()).thenReturn(mockUser)

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.logout()
        dispatcher.scheduler.advanceUntilIdle()

        verify(userSessionManager).clearToken()
        
        viewModel.user.test {
            val result = awaitItem()
            assertNull(result)
        }
    }

    @Test
    fun `token flow is observed correctly`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf("token1", "token2", null))

        viewModel.token.test {
            assertEquals("token1", awaitItem())
            assertEquals("token2", awaitItem())
            assertNull(awaitItem())
        }
    }

    @Test
    fun `user is not loaded when repository throws exception`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf("valid_token"))
        whenever(userRepository.getUserInfo()).thenThrow(RuntimeException("Network error"))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.user.test {
            val result = awaitItem()
            assertNull(result)
        }
    }

    @Test
    fun `loading becomes false when repository throws exception`() = runTest {
        whenever(userSessionManager.token).thenReturn(flowOf("valid_token"))
        whenever(userRepository.getUserInfo()).thenThrow(RuntimeException("Network error"))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loading.test {
            val result = awaitItem()
            assertFalse(result)
        }
    }
}
