package com.whm.githubapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.whm.githubapp.viewmodel.HotReposViewModel
import com.whm.githubapp.model.GitHubRepo

@Composable
fun HotReposScreen(navController: NavController) {
    val viewModel: HotReposViewModel = viewModel()
    val repos by viewModel.repos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()


    Column(modifier = Modifier.padding(16.dp)) {
        Text("🔥 Hot Repositories", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text("Failed: $error", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
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
            Text(text = repo.description ?: "No description", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "⭐ ${repo.stars}   🍴 ${repo.forks ?: "-"}")
        }
    }
}