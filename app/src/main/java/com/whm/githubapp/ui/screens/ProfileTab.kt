package com.whm.githubapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.whm.githubapp.viewmodel.AuthViewModel
import com.whm.githubapp.viewmodel.UserReposViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileTab(
    navController: NavController,
    viewModel: AuthViewModel,
    repoViewModel: UserReposViewModel) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val repos by repoViewModel.repos.collectAsState()

    Column(modifier = Modifier.padding(24.dp)) {
        if (user == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    val authUri = Uri.parse(
                        "https://github.com/login/oauth/authorize" +
                                "?client_id=Ov23ctcH0eRM3sjBTHcz" +
                                "&redirect_uri=https://your-app.com/callback" +
                                "&scope=repo,user"
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, authUri))
                }) {
                    Text("Login with GitHub")
                }
            }
        } else {
            Text("Welcome, ${user!!.login}")
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { viewModel.logout() }) {
                            Text("Logout")
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Your Repositories:", style = MaterialTheme.typography.titleMedium)
            }

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            LazyColumn {
                items(repos) { repo ->
                    Card(
                        modifier = Modifier
                            .clickable { navController.navigate("repoDetail/${repo.owner.login}/${repo.fullName.split("/")[1]}") }
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(repo.fullName, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(repo.description ?: "", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("★ ${repo.stars}", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.weight(1f))
                                if (user?.login == repo.owner.login) {
                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                "new_issue/${repo.owner.login}/${
                                                    repo.fullName.split(
                                                        "/"
                                                    )[1]
                                                }"
                                            )
                                        }) {
                                        Text("Issue")
                                    }
                                }
                            }
                        }
                    }

                }
            }
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}