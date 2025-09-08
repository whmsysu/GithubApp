package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.repository.RepoRepository
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
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SearchViewModel
    private val repoRepository = mock<RepoRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SearchViewModel(repoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchRepos emits results with UiState Success`() = runTest {
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
        whenever(repoRepository.searchRepos(any(), any(), any())).thenReturn(
            GitHubRepoResponse(items = fakeRepos)
        )

        viewModel.updateQuery("test")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(fakeRepos, (state as UiState.Success).data)
        }
    }

    @Test
    fun `searchRepos emits Loading state initially`() = runTest {
        whenever(repoRepository.searchRepos(any(), any(), any())).thenReturn(
            GitHubRepoResponse(items = emptyList())
        )

        viewModel.updateQuery("test")
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Loading)
        }
    }

    @Test
    fun `searchRepos emits Error state on exception`() = runTest {
        val errorMessage = "Network Error"
        whenever(repoRepository.searchRepos(any(), any(), any())).thenThrow(
            RuntimeException(errorMessage)
        )

        viewModel.updateQuery("test")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            assertEquals(errorMessage, (state as UiState.Error).message)
        }
    }

    @Test
    fun `loadMore appends new results to existing ones`() = runTest {
        val initialRepos = listOf(
            GitHubRepo(
                "TestRepo1",
                "TestRepo1/TestRepo1",
                "desc1",
                10,
                owner = mockk(relaxed = true),
                "Kotlin",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )
        val moreRepos = listOf(
            GitHubRepo(
                "TestRepo2",
                "TestRepo2/TestRepo2",
                "desc2",
                20,
                owner = mockk(relaxed = true),
                "Java",
                forks = 0,
                issues = 0,
                updatedAt = "",
                isStarred = false
            )
        )

        whenever(repoRepository.searchRepos(any(), any(), any())).thenReturn(
            GitHubRepoResponse(items = initialRepos)
        ).thenReturn(
            GitHubRepoResponse(items = moreRepos)
        )

        viewModel.updateQuery("test")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMore()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.searchResults.test {
            val result = awaitItem()
            assertEquals(initialRepos + moreRepos, result)
        }
    }

    @Test
    fun `debounced search only triggers after delay`() = runTest {
        whenever(repoRepository.searchRepos(any(), any(), any())).thenReturn(
            GitHubRepoResponse(items = emptyList())
        )

        viewModel.updateQuery("t")
        viewModel.updateQuery("te")
        viewModel.updateQuery("test")
        
        // Advance time to trigger debounce
        dispatcher.scheduler.advanceTimeBy(350)
        dispatcher.scheduler.runCurrent()

        verify(repoRepository, times(1)).searchRepos(any(), any(), any())
    }
}
