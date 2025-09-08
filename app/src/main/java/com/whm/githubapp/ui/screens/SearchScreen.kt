package com.whm.githubapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.whm.githubapp.viewmodel.SearchViewModel
import com.whm.githubapp.ui.UiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.max

@Composable
fun SearchScreen(navController: NavHostController, viewModel: SearchViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(results, loading) {
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
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            singleLine = true,
            maxLines = 1,
            label = { Text("Search Repositories") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = viewModel::performSearch,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (loading && results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
            Text("❌ " + error.orEmpty(), color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(state = listState) {
                items(results) { repo ->
                    Card(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(
                                    "repoDetail/${repo.owner.login}/${
                                        repo.fullName.split(
                                            "/"
                                        )[1]
                                    }"
                                )
                            }
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(repo.fullName, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(repo.description ?: "", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("★ ${repo.stars}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }


    }
}