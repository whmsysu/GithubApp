package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewIssueScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun submitButton_isEnabled_whenTitleAndDescEntered() {
        composeTestRule.setContent {
            NewIssueScreen(
                navController = rememberNavController(),
                owner = "whm",
                repo = "GitHubApp"
            )
        }

        composeTestRule.onNodeWithText("Title").performTextInput("Bug Report")
        composeTestRule.onNodeWithText("Description").performTextInput("App crashes when submitting issue")
        composeTestRule.onNodeWithText("Submit Issue").assertIsEnabled()
    }
}