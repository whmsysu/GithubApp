package com.whm.githubapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.CreateIssueRequest
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.network.GitHubRepoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewIssueViewModel(private val context: Context) : ViewModel() {

    private val _success = MutableStateFlow<IssueResponse?>(null)
    val success: StateFlow<IssueResponse?> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createIssue(owner: String, repo: String, title: String, body: String) {
        viewModelScope.launch {
            try {
                val token = UserSessionManager(context).token.first()
                val response = GitHubRepoService.create().createIssue(
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

class NewIssueViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewIssueViewModel(context) as T
    }
}