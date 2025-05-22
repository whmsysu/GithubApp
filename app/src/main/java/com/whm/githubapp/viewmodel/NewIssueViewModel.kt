package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.CreateIssueRequest
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.network.GitHubRepoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewIssueViewModel @Inject constructor(
    private val userSessionManager: UserSessionManager,
    private val gitHubRepoService: GitHubRepoService
) : ViewModel() {

    private val _success = MutableStateFlow<IssueResponse?>(null)
    val success: StateFlow<IssueResponse?> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createIssue(owner: String, repo: String, title: String, body: String) {
        viewModelScope.launch {
            try {
                val token = userSessionManager.token.first()
                val response = gitHubRepoService.createIssue(
                    auth = "token $token",
                    owner = owner,
                    repo = repo,
                    issue = CreateIssueRequest(title, body)
                )
                _success.value = response
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
