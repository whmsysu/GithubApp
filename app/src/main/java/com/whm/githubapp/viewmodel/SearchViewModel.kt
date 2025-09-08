package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.repository.RepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.whm.githubapp.ui.UiState

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ViewModel() {

    private val _query = MutableStateFlow("android")
    val query: StateFlow<String> = _query

    private val _searchResults = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val searchResults: StateFlow<List<GitHubRepo>> = _searchResults

    private val _page = MutableStateFlow(1)
    val page: StateFlow<Int> = _page

    private val _perPage = MutableStateFlow(30)
    val perPage: StateFlow<Int> = _perPage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _endReached = MutableStateFlow(false)
    val endReached: StateFlow<Boolean> = _endReached

    private val _prefetchThreshold = MutableStateFlow(5)
    val prefetchThreshold: StateFlow<Int> = _prefetchThreshold

    fun setPrefetchThreshold(value: Int) {
        _prefetchThreshold.value = value.coerceAtLeast(0)
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _uiState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<GitHubRepo>>> = _uiState

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        _page.value = 1
        _searchResults.value = emptyList()
        _endReached.value = false
    }

    fun performSearch() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _uiState.value = UiState.Loading
            try {
                val response = repoRepository.searchRepos(query.value, page = page.value, perPage = perPage.value)
                val merged = if (page.value == 1) response.items else _searchResults.value + response.items
                _searchResults.value = merged
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

    fun loadMore() {
        if (_loading.value || _endReached.value) return
        _page.value = _page.value + 1
        performSearch()
    }

    init {
        query
            .debounce(300)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .onEach {
                _page.value = 1
                performSearch()
            }
            .launchIn(viewModelScope)
    }
}