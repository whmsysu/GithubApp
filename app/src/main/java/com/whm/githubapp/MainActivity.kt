package com.whm.githubapp

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.whm.githubapp.network.OAuthManager
import com.whm.githubapp.ui.screens.ProfileScreen
import com.whm.githubapp.ui.screens.SearchScreen
import com.whm.githubapp.ui.theme.GitHubAppTheme
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.whm.githubapp.ui.screens.HotReposScreen
import com.whm.githubapp.ui.screens.NewIssueScreen
import com.whm.githubapp.ui.screens.RepoDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOAuthRedirect(intent?.data)
        setContent {

            GitHubAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainApp(navController)
                    }
                    composable(
                        "repoDetail/{owner}/{repo}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType }
                        )
                    ) {
                        val owner = it.arguments?.getString("owner") ?: ""
                        val repo = it.arguments?.getString("repo") ?: ""
                        RepoDetailScreen(navController, owner, repo)
                    }
                    composable(
                        "new_issue/{owner}/{repo}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType }
                        )
                    ) {
                        val owner = it.arguments?.getString("owner") ?: ""
                        val repo = it.arguments?.getString("repo") ?: ""
                        NewIssueScreen(navController, owner, repo)
                    }
                }

            }
        }
    }

    private fun handleOAuthRedirect(data: Uri?) {
        if (data?.scheme == "https" && data.host == "your-app.com") {
            val code = data.getQueryParameter("code")
            code?.let {
                OAuthManager.exchangeCodeForToken(it, applicationContext)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    modifier = Modifier.testTag("SearchTab"),
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = {},
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    modifier = Modifier.testTag("HotTab"),
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = {},
                    label = { Text("Hot") }
                )
                NavigationBarItem(
                    modifier = Modifier.testTag("ProfileTab"),
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = {},
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (selectedTabIndex) {
                0 -> {
                    SearchScreen(navController)
                }

                1 -> {
                    HotReposScreen(navController)
                }

                2 -> {
                    ProfileScreen(navController)
                }
            }
        }
    }
}
