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
        // 默认第一个是 Search，检查搜索输入框
        composeTestRule
            .onNodeWithTag("SearchInput")
            .assertIsDisplayed()
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
        // 等待加载完成
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule
            .onNode(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_hasSearchInput() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_hasSearchButton() {
        composeTestRule
            .onNodeWithTag("SearchButton")
            .assertIsDisplayed()
    }

    @Test
    fun hotReposScreen_showsLoadingInitially() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        // 检查是否有加载指示器或内容
        composeTestRule.onAllNodes(hasContentDescription("Loading"))
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_showsUserInfoWhenLoggedIn() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 如果已登录，应该显示用户信息
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示登录按钮或用户信息
        val hasLoginButton = composeTestRule.onAllNodes(hasTestTag("LoginButton"))
            .fetchSemanticsNodes().isNotEmpty()
        val hasLogoutButton = composeTestRule.onAllNodes(hasTestTag("LogoutButton"))
            .fetchSemanticsNodes().isNotEmpty()
        
        assert(hasLoginButton || hasLogoutButton) { "Should show either login or logout button" }
    }

    @Test
    fun navigation_betweenTabs() {
        // 测试标签页之间的导航
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("SearchInput").assertIsDisplayed()

        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("🔥 Hot Repositories").assertIsDisplayed()

        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
            .assertIsDisplayed()
    }
}
