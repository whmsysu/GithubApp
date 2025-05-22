package com.whm.githubapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.whm.githubapp.viewmodel.NewIssueViewModel
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NewIssueScreen(
    navController: NavController,
    owner: String,
    repo: String,
    viewModel: NewIssueViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(success) {
        success?.let {
            Toast.makeText(context, "✅ Issue Created: #" + it.number, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("📬 New Issue in $owner/$repo", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TitleInput")
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("DescInput")
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.testTag("SubmitButton"),
            onClick = {
                viewModel.createIssue(owner, repo, title, body)
            }) {
            Text("Submit Issue")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (success != null) {
            Text(
                "✅ Issue created: #${success!!.number} — ${success!!.title}",
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (error != null) {
            Text("❌ Failed: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}