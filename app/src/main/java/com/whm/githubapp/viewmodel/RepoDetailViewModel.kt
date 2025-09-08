package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.repository.RepoRepository
import com.whm.githubapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.whm.githubapp.ui.UiState

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val repoRepository: RepoRepository
) : ViewModel() {

    private val _gitHubRepo = MutableStateFlow<GitHubRepo?>(null)
    val gitHubRepo: StateFlow<GitHubRepo?> = _gitHubRepo

    private val _uiState = MutableStateFlow<UiState<GitHubRepo>>(UiState.Idle)
    val uiState: StateFlow<UiState<GitHubRepo>> = _uiState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _starLoading = MutableStateFlow(false)
    val starLoading: StateFlow<Boolean> = _starLoading

    fun loadRepo(owner: String, repoName: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repoRepository.getRepoDetail(owner, repoName)
                val starredResponse = userRepository.checkIfStarred(owner, repoName)
                val isStarred = starredResponse.code() == 204
                _gitHubRepo.value = result.copy(isStarred = isStarred)
                _uiState.value = UiState.Success(result.copy(isStarred = isStarred))
            } catch (e: Exception) {
                _error.value = e.message
                _uiState.value = UiState.Error(e.message)
            }
        }
    }

    fun toggleStar(owner: String, repo: String) {
        viewModelScope.launch {
            _starLoading.value = true
            try {
                if (gitHubRepo.value?.isStarred == true) {
                    userRepository.unstarRepo(owner, repo)
                } else {
                    userRepository.starRepo(owner, repo)
                }
                _gitHubRepo.value?.let { currentVal ->
                    _gitHubRepo.value = currentVal.copy(isStarred = currentVal.isStarred.not())
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _starLoading.value = false
            }
        }
    }
}
