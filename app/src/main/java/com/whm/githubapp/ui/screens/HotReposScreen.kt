package com.whm.githubapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.whm.githubapp.viewmodel.HotReposViewModel
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.ui.UiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.max

@Composable
fun HotReposScreen(navController: NavController, viewModel: HotReposViewModel = hiltViewModel()) {

    val repos by viewModel.hotRepos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(repos, loading) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index to listState.layoutInfo.totalItemsCount }
            .map { (lastIndex, total) ->
                val n = viewModel.prefetchThreshold.value
                val percentThreshold = (total * 8) / 10
                val threshold = max(0, max(total - n, percentThreshold))
                lastIndex != null && total > 0 && lastIndex >= threshold
            }
            .distinctUntilChanged()
            .filter { it && !loading }
            .collect { viewModel.loadMore() }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("🔥 Hot Repositories", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (loading && repos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(error) {
                snackbarHostState.showSnackbar(error ?: "Unknown error")
            }
            Text("Failed: $error", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(state = listState) {
                items(repos) { repo ->
                    HotRepoCard(repo, navController)
                }
            }
        }
    }
}

@Composable
fun HotRepoCard(repo: GitHubRepo, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { navController.navigate("repoDetail/${repo.owner.login}/${repo.name}") }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = repo.fullName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = repo.description ?: "No description",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "⭐ ${repo.stars}   🍴 ${repo.forks ?: "-"}")
        }
    }
}