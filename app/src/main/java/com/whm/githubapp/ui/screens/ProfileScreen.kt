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
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.whm.githubapp.viewmodel.AuthViewModel
import com.whm.githubapp.viewmodel.UserReposViewModel
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    repoViewModel: UserReposViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val repos by repoViewModel.repos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.padding(24.dp)) {
            if (user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.testTag("LoginButton"),
                        onClick = {
                            val authUri = Uri.parse(
                                "https://github.com/login/oauth/authorize" +
                                        "?client_id=Ov23ctcH0eRM3sjBTHcz" +
                                        "&redirect_uri=myapp://callback" +
                                        "&scope=repo,user"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, authUri))
                        }
                    ) {
                        Text(
                           "Login with GitHub"
                        )
                    }
                }
            } else {
                user?.let { currentUser ->
                    // 用户信息卡片
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = currentUser.name ?: currentUser.login,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = "@${currentUser.login}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    modifier = Modifier.testTag("LogoutButton"),
                                    onClick = { showLogoutDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Text("Logout")
                                }
                            }
                            
                            if (currentUser.bio != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentUser.bio,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${currentUser.publicRepos ?: 0}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Repositories",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${currentUser.followers ?: 0}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Followers",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${currentUser.following ?: 0}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Following",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your Repositories",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                val snackbarHostState = remember { SnackbarHostState() }
                
                if (repos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No repositories found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(repos) { repo ->
                            Card(
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            "repoDetail/${repo.owner.login}/${
                                                repo.fullName.split("/")[1]
                                            }"
                                        )
                                    }
                                    .fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = repo.fullName,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            if (!repo.description.isNullOrEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = repo.description,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                        
                                        user?.let { currentUser ->
                                            if (currentUser.login == repo.owner.login) {
                                                Button(
                                                    onClick = {
                                                        navController.navigate(
                                                            "new_issue/${repo.owner.login}/${
                                                                repo.fullName.split("/")[1]
                                                            }"
                                                        )
                                                    },
                                                    modifier = Modifier.padding(start = 8.dp)
                                                ) {
                                                    Text("New Issue")
                                                }
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("⭐", style = MaterialTheme.typography.bodySmall)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${repo.stars}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("🍴", style = MaterialTheme.typography.bodySmall)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${repo.forks ?: 0}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            if (repo.language != null) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("●", style = MaterialTheme.typography.bodySmall)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = repo.language,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                        }
                                        
                                        Text(
                                            text = if (repo.updatedAt != null) {
                                                "Updated ${formatRelativeTime(repo.updatedAt)}"
                                            } else "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
    
    // Logout confirmation dialog
    LogoutConfirmationDialog(
        showDialog = showLogoutDialog,
        onDismiss = { showLogoutDialog = false },
        onConfirm = { viewModel.logout() }
    )
}

fun formatRelativeTime(raw: String?): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(raw ?: return "")
        val now = Date()
        val diff = now.time - (date?.time ?: return "")
        
        when {
            diff < 24 * 60 * 60 * 1000 -> "today"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}d ago"
            diff < 30 * 24 * 60 * 60 * 1000 -> "${diff / (7 * 24 * 60 * 60 * 1000)}w ago"
            diff < 365 * 24 * 60 * 60 * 1000 -> "${diff / (30 * 24 * 60 * 60 * 1000)}mo ago"
            else -> "${diff / (365 * 24 * 60 * 60 * 1000)}y ago"
        }
    } catch (e: Exception) {
        ""
    }
}

@Composable
private fun LogoutConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Confirm Logout")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}