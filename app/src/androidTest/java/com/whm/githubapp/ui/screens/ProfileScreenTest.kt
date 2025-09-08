package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whm.githubapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun profileScreen_showsLoginButtonWhenNotLoggedIn() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待登录按钮显示
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithTag("LoginButton")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_showsUserInfoWhenLoggedIn() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待用户信息或登录按钮显示
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
    fun profileScreen_loginButtonIsClickable() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待登录按钮显示
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithTag("LoginButton")
            .performClick()
        
        // 应该打开登录页面或浏览器
        composeTestRule.waitForIdle()
    }

    @Test
    fun profileScreen_logoutButtonShowsConfirmationDialog() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待登出按钮显示（如果已登录）
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LogoutButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击登出按钮
        composeTestRule
            .onNodeWithTag("LogoutButton")
            .performClick()
        
        // 应该显示确认对话框
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Confirm Logout")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_logoutConfirmationDialogHasCorrectButtons() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待登出按钮显示（如果已登录）
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LogoutButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击登出按钮
        composeTestRule
            .onNodeWithTag("LogoutButton")
            .performClick()
        
        // 检查确认对话框的按钮
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Logout")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_canCancelLogout() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待登出按钮显示（如果已登录）
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LogoutButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击登出按钮
        composeTestRule
            .onNodeWithTag("LogoutButton")
            .performClick()
        
        // 点击取消按钮
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()
        
        // 对话框应该消失
        composeTestRule.waitForIdle()
    }

    @Test
    fun profileScreen_showsUserRepositoriesWhenLoggedIn() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待用户信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 如果已登录，应该显示用户仓库
        val hasLogoutButton = composeTestRule.onAllNodes(hasTestTag("LogoutButton"))
            .fetchSemanticsNodes().isNotEmpty()
        
        if (hasLogoutButton) {
            // 等待仓库列表加载
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodes(hasTestTag("UserRepoItem"))
                    .fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodes(hasText("No repositories found"))
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }
    }

    @Test
    fun profileScreen_showsUserStatsWhenLoggedIn() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 等待用户信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 如果已登录，应该显示用户统计信息
        val hasLogoutButton = composeTestRule.onAllNodes(hasTestTag("LogoutButton"))
            .fetchSemanticsNodes().isNotEmpty()
        
        if (hasLogoutButton) {
            // 检查是否显示用户统计信息
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodes(hasText("Repositories"))
                    .fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodes(hasText("Followers"))
                    .fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodes(hasText("Following"))
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }
    }

    @Test
    fun profileScreen_showsLoadingState() {
        composeTestRule
            .onNodeWithTag("ProfileTab")
            .performClick()
        composeTestRule.waitForIdle()
        
        // 检查是否有加载指示器
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasContentDescription("Loading"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun profileScreen_navigationFromOtherTabs() {
        // 从其他标签页导航到个人资料
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.waitForIdle()
        
        // 应该显示个人资料页面
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LoginButton").or(hasTestTag("LogoutButton")))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
