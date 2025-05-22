package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.network.GitHubRepoService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HotReposViewModelTest {

    private lateinit var gitHubRepoService: GitHubRepoService

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gitHubRepoService = mockk()

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

        coEvery { gitHubRepoService.getTrendingRepos(any(), any()) } returns
                GitHubRepoResponse(
                    items = mockRepos
                )

        val viewModel = HotReposViewModel(gitHubRepoService)

        advanceUntilIdle()

        viewModel.hotRepos.test {
            val result = awaitItem()
            assertEquals(mockRepos, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadHotRepos sets error on exception`() = runTest {
        val exceptionMessage = "Network Error"

        coEvery { gitHubRepoService.getTrendingRepos(any(), any()) } throws RuntimeException(
            exceptionMessage
        )

        val viewModel = HotReposViewModel(gitHubRepoService)

        advanceUntilIdle()

        viewModel.error.test {
            val result = awaitItem()
            assertEquals(exceptionMessage, result)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
