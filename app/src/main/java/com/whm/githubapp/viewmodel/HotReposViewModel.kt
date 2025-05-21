package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.network.GitHubRepoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HotReposViewModel : ViewModel() {
    private val _repos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val repos: StateFlow<List<GitHubRepo>> = _repos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        fetchHotRepos()
    }

    private fun fetchHotRepos() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val date7DaysAgo = LocalDate.now().minusDays(7).toString()
                val query = "created:>$date7DaysAgo"
                val response = GitHubRepoService.create().getTrendingRepos(query)
                _repos.value = response.items
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}