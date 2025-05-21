package com.whm.githubapp.viewmodel

        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import com.whm.githubapp.model.GitHubRepo
        import com.whm.githubapp.network.GitHubRepoService
        import kotlinx.coroutines.flow.MutableStateFlow
        import kotlinx.coroutines.flow.StateFlow
        import kotlinx.coroutines.launch

        class SearchViewModel : ViewModel() {

            private val service = GitHubRepoService.create()

            private val _query = MutableStateFlow("android")
            val query: StateFlow<String> = _query

            private val _searchResults = MutableStateFlow<List<GitHubRepo>>(emptyList())
            val searchResults: StateFlow<List<GitHubRepo>> = _searchResults

            private val _loading = MutableStateFlow(false)
            val loading: StateFlow<Boolean> = _loading

            private val _error = MutableStateFlow<String?>(null)
            val error: StateFlow<String?> = _error

            fun onQueryChange(newQuery: String) {
                _query.value = newQuery
            }

            fun performSearch() {
                viewModelScope.launch {
                    _loading.value = true
                    _error.value = null
                    try {
                        val response = service.searchRepos(query.value)
                        _searchResults.value = response.items
                    } catch (e: Exception) {
                        _error.value = e.message
                    } finally {
                        _loading.value = false
                    }
                }
            }
        }