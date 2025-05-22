package com.whm.githubapp.viewmodel

import app.cash.turbine.test
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.CreateIssueRequest
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.network.GitHubRepoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class NewIssueViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val service = mock<GitHubRepoService>()
    private val sessionManager = mock<UserSessionManager>()
    private lateinit var viewModel: NewIssueViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NewIssueViewModel(sessionManager, service)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitIssue updates status on success`() = runTest {
        val resp = IssueResponse(
            0,
            0,
            "",
            "",
            ""
        )
        whenever(service.createIssue(any(), any(), any(), any())).thenReturn(
            resp
        )
        whenever(sessionManager.token).thenReturn(flowOf("123"))
        viewModel.createIssue("user", "repo", "title", "desc")
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.success.test {
            val state = awaitItem()
            assertEquals(state, resp)
        }
    }
}
