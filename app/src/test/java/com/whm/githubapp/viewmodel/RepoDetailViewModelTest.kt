package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.repository.RepoRepository
import com.whm.githubapp.repository.UserRepository
import com.whm.githubapp.ui.UiState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class RepoDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repoRepository = mock<RepoRepository>()
    private val userRepository = mock<UserRepository>()
    private lateinit var viewModel: RepoDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRepoDetail loads repository successfully`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = false
        )
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(mockRepo, (state as UiState.Success).data)
        }
    }

    @Test
    fun `loadRepoDetail shows loading state initially`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = false
        )
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Loading)
        }
    }

    @Test
    fun `loadRepoDetail shows error state on failure`() = runTest {
        val errorMessage = "Repository not found"
        whenever(repoRepository.getRepoDetail(any(), any())).thenThrow(
            RuntimeException(errorMessage)
        )

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            assertEquals(errorMessage, (state as UiState.Error).message)
        }
    }

    @Test
    fun `toggleStar stars repository successfully`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = false
        )
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)
        whenever(userRepository.starRepo(any(), any())).thenReturn(Unit)

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleStar("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        verify(userRepository).starRepo("owner", "TestRepo")
    }

    @Test
    fun `toggleStar unstars repository successfully`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = true
        )
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)
        whenever(userRepository.unstarRepo(any(), any())).thenReturn(Unit)

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleStar("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        verify(userRepository).unstarRepo("owner", "TestRepo")
    }

    @Test
    fun `checkStarStatus checks if repository is starred`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = false
        )
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)
        whenever(userRepository.checkIfStarred(any(), any())).thenReturn(true)

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.checkStarStatus("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        verify(userRepository).checkIfStarred("owner", "TestRepo")
    }

    @Test
    fun `toggleStar shows error when star operation fails`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = false
        )
        val errorMessage = "Failed to star repository"
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)
        whenever(userRepository.starRepo(any(), any())).thenThrow(
            RuntimeException(errorMessage)
        )

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleStar("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.error.test {
            val result = awaitItem()
            assertEquals(errorMessage, result)
        }
    }

    @Test
    fun `repo state is updated correctly`() = runTest {
        val mockRepo = GitHubRepo(
            name = "TestRepo",
            fullName = "owner/TestRepo",
            description = "Test description",
            stars = 100,
            owner = mockk(relaxed = true),
            language = "Kotlin",
            forks = 10,
            issues = 5,
            updatedAt = "2023-01-01T00:00:00Z",
            isStarred = false
        )
        whenever(repoRepository.getRepoDetail(any(), any())).thenReturn(mockRepo)

        viewModel = RepoDetailViewModel(repoRepository, userRepository)
        viewModel.loadRepoDetail("owner", "TestRepo")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.repo.test {
            val result = awaitItem()
            assertEquals(mockRepo, result)
        }
    }
}
