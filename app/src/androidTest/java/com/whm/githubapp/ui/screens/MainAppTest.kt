package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whm.githubapp.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainAppTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomBar_displaysAllTabs() {
        composeTestRule
            .onNodeWithTag("SearchTab")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("HotTab")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .assertIsDisplayed()
    }

    @Test
    fun searchTab_showsSearchScreen() {
        // 默认第一个是 Search
        composeTestRule.onNodeWithText("android").assertIsDisplayed()
    }

    @Test
    fun hotTab_showsHotReposScreen() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        // Hot Repos Screen 顶部标题
        composeTestRule.onNodeWithText("🔥 Hot Repositories").assertIsDisplayed()
    }

    @Test
    fun profileTab_showsLoginButtonOrLogoutButton() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(10000)
        composeTestRule
            .onNode(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
            .assertIsDisplayed()
    }
}
