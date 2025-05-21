package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class NewIssueScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun issueScreen_submitButton_enabled_whenTitleEntered() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NewIssueScreen(navController, owner = "me", repo = "TestRepo")
        }

        composeTestRule.onNodeWithText("Title").performTextInput("Crash bug")
        composeTestRule.onNodeWithText("Description").performTextInput("App crash on launch")

        composeTestRule.onNodeWithText("Submit Issue").assertIsEnabled()
    }
}