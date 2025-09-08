package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.whm.githubapp.ui.UiState

@HiltViewModel
class UserReposViewModel @Inject constructor(
    userSession: UserSessionManager,
    userRepository: UserRepository
) : ViewModel() {

    private val _repos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val repos: StateFlow<List<GitHubRepo>> = _repos

    private val _uiState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<GitHubRepo>>> = _uiState

    private var token: String? = null

    init {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            userSession.token
                .filterNotNull()
                .distinctUntilChanged()
                .collect { t ->
                token = t
                if (t.isNotEmpty()) {
                    try {
                        val result = userRepository.getUserRepos()
                        _repos.value = result
                        _uiState.value = UiState.Success(result)
                    } catch (_: Exception) {
                        _uiState.value = UiState.Error(null)
                    }
                }
            }
        }
    }
}