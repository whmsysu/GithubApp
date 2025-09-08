package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.repository.RepoRepository
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
class HotReposViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repoRepository = mock<RepoRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadHotRepos updates repos state with fetched data`() = runTest {
        val mockRepos = listOf(
            GitHubRepo(
                name = "TestRepo1",
                fullName = "TestRepo1/TestRepo1",
                owner = mockk(relaxed = true),
                description = "desc",
                stars = 123,
                language = "Kotlin",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            ),
            GitHubRepo(
                name = "TestRepo2",
                fullName = "TestRepo1/TestRepo2",
                owner = mockk(relaxed = true),
                description = "desc2",
                stars = 456,
                language = "Java",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )

        whenever(repoRepository.getTrendingRepos(any(), any())).thenReturn(
            GitHubRepoResponse(items = mockRepos)
        )

        val viewModel = HotReposViewModel(repoRepository)

        advanceUntilIdle()

        viewModel.uiState.test {
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertEquals(mockRepos, (result as UiState.Success).data)
        }
    }

    @Test
    fun `loadHotRepos sets error on exception`() = runTest {
        val exceptionMessage = "Network Error"

        whenever(repoRepository.getTrendingRepos(any(), any())).thenThrow(
            RuntimeException(exceptionMessage)
        )

        val viewModel = HotReposViewModel(repoRepository)

        advanceUntilIdle()

        viewModel.uiState.test {
            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals(exceptionMessage, (result as UiState.Error).message)
        }
    }

    @Test
    fun `loadMore appends new results to existing ones`() = runTest {
        val initialRepos = listOf(
            GitHubRepo(
                name = "TestRepo1",
                fullName = "TestRepo1/TestRepo1",
                owner = mockk(relaxed = true),
                description = "desc1",
                stars = 123,
                language = "Kotlin",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )
        val moreRepos = listOf(
            GitHubRepo(
                name = "TestRepo2",
                fullName = "TestRepo2/TestRepo2",
                owner = mockk(relaxed = true),
                description = "desc2",
                stars = 456,
                language = "Java",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )

        whenever(repoRepository.getTrendingRepos(any(), any())).thenReturn(
            GitHubRepoResponse(items = initialRepos)
        ).thenReturn(
            GitHubRepoResponse(items = moreRepos)
        )

        val viewModel = HotReposViewModel(repoRepository)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        viewModel.hotRepos.test {
            val result = awaitItem()
            assertEquals(initialRepos + moreRepos, result)
        }
    }

    @Test
    fun `refresh resets repos and loads first page`() = runTest {
        val refreshedRepos = listOf(
            GitHubRepo(
                name = "NewRepo",
                fullName = "NewRepo/NewRepo",
                owner = mockk(relaxed = true),
                description = "new desc",
                stars = 999,
                language = "Kotlin",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )

        whenever(repoRepository.getTrendingRepos(any(), any())).thenReturn(
            GitHubRepoResponse(items = emptyList())
        ).thenReturn(
            GitHubRepoResponse(items = refreshedRepos)
        )

        val viewModel = HotReposViewModel(repoRepository)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.hotRepos.test {
            val result = awaitItem()
            assertEquals(refreshedRepos, result)
        }
    }

    @Test
    fun `endReached is true when no more results`() = runTest {
        whenever(repoRepository.getTrendingRepos(any(), any())).thenReturn(
            GitHubRepoResponse(items = emptyList())
        )

        val viewModel = HotReposViewModel(repoRepository)
        advanceUntilIdle()

        assertTrue(viewModel.endReached.value)
    }
}
