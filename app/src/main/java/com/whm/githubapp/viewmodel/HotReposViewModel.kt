package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.repository.RepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import com.whm.githubapp.ui.UiState

@HiltViewModel
class HotReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ViewModel() {

    private val _hotRepos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val hotRepos: StateFlow<List<GitHubRepo>> = _hotRepos

    private val _page = MutableStateFlow(1)
    val page: StateFlow<Int> = _page

    private val _perPage = MutableStateFlow(30)
    val perPage: StateFlow<Int> = _perPage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _uiState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<GitHubRepo>>> = _uiState

    private val _endReached = MutableStateFlow(false)
    val endReached: StateFlow<Boolean> = _endReached

    private val _prefetchThreshold = MutableStateFlow(5)
    val prefetchThreshold: StateFlow<Int> = _prefetchThreshold

    fun setPrefetchThreshold(value: Int) {
        _prefetchThreshold.value = value.coerceAtLeast(0)
    }

    init {
        fetchHotRepos()
    }

    private fun fetchHotRepos() {
        viewModelScope.launch {
            _loading.value = true
            _uiState.value = UiState.Loading
            try {
                val date7DaysAgo = LocalDate.now().minusDays(7).toString()
                val query = "created:>$date7DaysAgo"
                val response = repoRepository.getTrendingRepos(query, page = page.value, perPage = perPage.value)
                val merged = if (page.value == 1) response.items else _hotRepos.value + response.items
                _hotRepos.value = merged
                _uiState.value = UiState.Success(merged)
                if (response.items.isEmpty()) {
                    _endReached.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message
                _uiState.value = UiState.Error(e.message)
            } finally {
                _loading.value = false
            }
        }
    }

    fun refresh() {
        _page.value = 1
        _hotRepos.value = emptyList()
        _endReached.value = false
        fetchHotRepos()
    }

    fun loadMore() {
        if (_loading.value || _endReached.value) return
        _page.value = _page.value + 1
        fetchHotRepos()
    }
}
