package com.whm.githubapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.whm.githubapp.viewmodel.RepoDetailViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.whm.githubapp.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
    navController: NavController,
    owner: String,
    repo: String,
    viewModel: RepoDetailViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val repoState by viewModel.gitHubRepo.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.starLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRepo(owner, repo)
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
        viewModel.loadRepo(owner, repo)

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$owner/$repo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            repoState != null -> {
                val r = repoState!!
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text("📘 Repository: ${r.fullName}", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📝 Description: ${r.description ?: "No description"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🌐 Language: ${r.language ?: "Unknown"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⭐ Stars: ${r.stars}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🍴 Forks: ${r.forks ?: "N/A"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🐞 Open Issues: ${r.issues ?: "N/A"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📅 Updated At: ${formatDate(r.updatedAt)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("👤 Owner: ${r.owner.login}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.toggleStar(owner, repo)
                        },
                        enabled = !loading,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(if (r.isStarred) "Unstar" else "Star")
                    }
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

fun formatDate(raw: String?): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(raw ?: return "Unknown")
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formatter.format(date ?: return "Unknown")
    } catch (e: Exception) {
        "Unknown"
    }
}