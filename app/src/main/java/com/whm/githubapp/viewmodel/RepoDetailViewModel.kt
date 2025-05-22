package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.network.GitHubRepoService
import com.whm.githubapp.network.GitHubUserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val gitHubUserService: GitHubUserService,
    private val gitHubRepoService: GitHubRepoService,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _gitHubRepo = MutableStateFlow<GitHubRepo?>(null)
    val gitHubRepo: StateFlow<GitHubRepo?> = _gitHubRepo

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _starLoading = MutableStateFlow(false)
    val starLoading: StateFlow<Boolean> = _starLoading

    fun loadRepo(owner: String, repoName: String) {
        viewModelScope.launch {
            try {
                val token = userSessionManager.token.first()
                val result = gitHubRepoService.getRepoDetailAuth("token $token", owner, repoName)
                val starredResponse =
                    gitHubUserService.checkIfStarred("token $token", owner, repoName)
                val isStarred = starredResponse.code() == 204
                _gitHubRepo.value = result.copy(isStarred = isStarred)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleStar(owner: String, repo: String) {
        viewModelScope.launch {
            _starLoading.value = true
            try {
                val token = userSessionManager.token.first()
                if (gitHubRepo.value?.isStarred == true) {
                    gitHubUserService.unstarRepo("token $token", owner, repo)
                } else {
                    gitHubUserService.starRepo("token $token", owner, repo)
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
