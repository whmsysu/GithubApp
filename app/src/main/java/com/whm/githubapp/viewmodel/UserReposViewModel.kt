package com.whm.githubapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.network.GitHubUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserReposViewModel(context: Context) : ViewModel() {
    private val userSession = UserSessionManager(context)
    private val service = GitHubUserService.create()

    private val _repos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val repos: StateFlow<List<GitHubRepo>> = _repos

    private var token: String? = null

    init {
        viewModelScope.launch {
            userSession.token.collect { t ->
                token = t
                if (!t.isNullOrEmpty()) {
                    try {
                        val result = service.getUserRepos("token $t")
                        _repos.value = result
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }
}