package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.repository.RepoRepository
import com.whm.githubapp.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class NewIssueViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repoRepository = mock<RepoRepository>()
    private lateinit var viewModel: NewIssueViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NewIssueViewModel(repoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `createIssue updates status on success`() = runTest {
        val response = IssueResponse(
            number = 123,
            id = 456,
            title = "Test Issue",
            body = "Test Description",
            state = "open"
        )
        whenever(repoRepository.createIssue(any(), any(), any(), any())).thenReturn(response)

        viewModel.createIssue("user", "repo", "title", "desc")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(response, (state as UiState.Success).data)
        }
    }

    @Test
    fun `createIssue shows loading state initially`() = runTest {
        val response = IssueResponse(
            number = 123,
            id = 456,
            title = "Test Issue",
            body = "Test Description",
            state = "open"
        )
        whenever(repoRepository.createIssue(any(), any(), any(), any())).thenReturn(response)

        viewModel.createIssue("user", "repo", "title", "desc")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Loading)
        }
    }

    @Test
    fun `createIssue shows error state on failure`() = runTest {
        val errorMessage = "Network Error"
        whenever(repoRepository.createIssue(any(), any(), any(), any())).thenThrow(
            RuntimeException(errorMessage)
        )

        viewModel.createIssue("user", "repo", "title", "desc")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            assertEquals(errorMessage, (state as UiState.Error).message)
        }
    }

    @Test
    fun `createIssue calls repository with correct parameters`() = runTest {
        val response = IssueResponse(
            number = 123,
            id = 456,
            title = "Test Issue",
            body = "Test Description",
            state = "open"
        )
        whenever(repoRepository.createIssue(any(), any(), any(), any())).thenReturn(response)

        viewModel.createIssue("testuser", "testrepo", "Test Title", "Test Description")
        dispatcher.scheduler.advanceUntilIdle()

        verify(repoRepository).createIssue("testuser", "testrepo", "Test Title", "Test Description")
    }

    @Test
    fun `success state is emitted after successful creation`() = runTest {
        val response = IssueResponse(
            number = 123,
            id = 456,
            title = "Test Issue",
            body = "Test Description",
            state = "open"
        )
        whenever(repoRepository.createIssue(any(), any(), any(), any())).thenReturn(response)

        viewModel.createIssue("user", "repo", "title", "desc")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.success.test {
            val result = awaitItem()
            assertEquals(response, result)
        }
    }
}
