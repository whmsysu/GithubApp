package com.whm.githubapp.viewmodel

import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.Owner
import com.whm.githubapp.network.GitHubRepoService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class RepoDetailViewModelTest {
    private lateinit var service: GitHubRepoService
    private lateinit var viewModel: RepoDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        service = mock(GitHubRepoService::class.java)
        viewModel = RepoDetailViewModel(service)
    }

    @Test
    fun `loadRepoDetail success`() = runTest {
        val repo = GitHubRepo("Test", Owner("me"), "desc", 999, "Kotlin")
        `when`(service.getRepoDetail("me", "Test")).thenReturn(repo)

        viewModel.loadRepoDetail("me", "Test", "token xxx")
        assertEquals("Test", viewModel.repo.first()?.name)
    }
}