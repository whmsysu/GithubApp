package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.CreateIssueRequest
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.repository.RepoRepository
import com.whm.githubapp.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewIssueViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ViewModel() {

    private val _success = MutableStateFlow<IssueResponse?>(null)
    val success: StateFlow<IssueResponse?> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _uiState = MutableStateFlow<UiState<IssueResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<IssueResponse>> = _uiState

    fun createIssue(owner: String, repo: String, title: String, body: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repoRepository.createIssue(
                    owner = owner,
                    repo = repo,
                    title = title,
                    body = body
                )
                _success.value = response
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _error.value = e.message
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
}
