package com.whm.githubapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.network.GitHubRepoService
import com.whm.githubapp.network.GitHubUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLEncoder

class RepoDetailViewModel(private val context: Context) : ViewModel() {

    fun toggleStar() {
        viewModelScope.launch {
            _loading.value = true
            val repo = _repo.value ?: return@launch
            val token = UserSessionManager(context).token.first()
            try {
                val encodedRepo = URLEncoder.encode(repo.name, "UTF-8")
                val encodedOwner = URLEncoder.encode(repo.owner.login, "UTF-8")
                val resp = if (repo.isStarred) {
                    GitHubUserService.create().unstarRepo("token $token", encodedOwner, encodedRepo)
                } else {
                    GitHubUserService.create().starRepo("token $token", encodedOwner, encodedRepo)
                }
                _repo.value = repo.copy(isStarred = !repo.isStarred)
            _loading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to toggle star: ${e.message}"
            _loading.value = false
            }
        }
    }
    
    private val _repo = MutableStateFlow<GitHubRepo?>(null)
    val repo: StateFlow<GitHubRepo?> = _repo

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchRepo(owner: String, repo: String) {
        viewModelScope.launch {
            try {
                val token = UserSessionManager(context).token.first()
                val result = GitHubRepoService.create().getRepoDetailAuth(
                    auth = "token $token",
                    owner = owner,
                    repo = repo
                )
                val starredResponse = GitHubUserService.create().checkIfStarred("token $token", owner, repo)
                val isStarred = starredResponse.code() == 204
                _repo.value = result.copy(isStarred = isStarred)
            } catch (e: Exception) {
                _error.value = e.message
            }

        }
    }
}

class RepoDetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RepoDetailViewModel(context) as T
    }
}