package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.network.GitHubRepoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HotReposViewModel @Inject constructor(
    private val gitHubRepoService: GitHubRepoService
) : ViewModel() {

    private val _hotRepos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val hotRepos: StateFlow<List<GitHubRepo>> = _hotRepos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchHotRepos()
    }

    private fun fetchHotRepos() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val date7DaysAgo = LocalDate.now().minusDays(7).toString()
                val query = "created:>$date7DaysAgo"
                val repos = gitHubRepoService.getTrendingRepos(query).items
                _hotRepos.value = repos
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
