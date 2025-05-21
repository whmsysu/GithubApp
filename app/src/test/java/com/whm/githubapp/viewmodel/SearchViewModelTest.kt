package com.whm.githubapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.network.GitHubRepoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule val rule = InstantTaskExecutorRule()
    private lateinit var service: GitHubRepoService
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        service = mock(GitHubRepoService::class.java)
        viewModel = SearchViewModel(service)
    }

    @Test
    fun `searchRepos success`() = runTest {
        val repos = listOf(GitHubRepo("Repo1", null, "desc", 123, "Kotlin"))
        `when`(service.searchRepos("kotlin", "stars")).thenReturn(GitHubRepoResponse(repos))

        viewModel.searchRepos("kotlin", "stars")
        assertEquals(1, viewModel.repos.first().size)
    }
}