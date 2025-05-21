package com.whm.githubapp.viewmodel

import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.network.GitHubRepoService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class HotReposViewModelTest {
    private lateinit var service: GitHubRepoService
    private lateinit var viewModel: HotReposViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        service = mock(GitHubRepoService::class.java)
        viewModel = HotReposViewModel(service)
    }

    @Test
    fun `fetchHotRepos updates repos`() = runTest {
        val mockList = listOf(GitHubRepo("hot", null, "desc", 2000, "Kotlin"))
        `when`(service.getTrendingRepos("created:>2023-01-01")).thenReturn(GitHubRepoResponse(mockList))

        viewModel.fetchHotRepos("created:>2023-01-01")
        assertEquals(1, viewModel.repos.first().size)
    }
}