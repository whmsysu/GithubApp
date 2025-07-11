package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.network.GitHubRepoService
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SearchViewModel
    private val service = mock<GitHubRepoService>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SearchViewModel(service)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchRepos emits results`() = runTest {
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
        whenever(service.searchRepos(any())).thenReturn(
            GitHubRepoResponse(
                items = fakeRepos
            )
        )

        viewModel.performSearch()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.searchResults.test {
            val state = awaitItem()
            assertEquals(state, fakeRepos)
        }
    }
}
