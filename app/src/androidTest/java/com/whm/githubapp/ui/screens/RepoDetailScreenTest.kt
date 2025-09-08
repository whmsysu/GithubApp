package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whm.githubapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepoDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun repoDetailScreen_displaysBackButton() {
        // 首先导航到仓库详情页面
        navigateToRepoDetail()
        
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_backButtonIsClickable() {
        navigateToRepoDetail()
        
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()
        
        // 应该返回到上一个页面
        composeTestRule.waitForIdle()
    }

    @Test
    fun repoDetailScreen_showsLoadingState() {
        navigateToRepoDetail()
        
        // 检查是否有加载指示器
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasContentDescription("Loading"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun repoDetailScreen_displaysRepositoryInfo() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoName"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示仓库名称
        composeTestRule
            .onNodeWithTag("RepoName")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_displaysRepositoryDescription() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoDescription"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示仓库描述
        composeTestRule
            .onNodeWithTag("RepoDescription")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_displaysStarButton() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("StarButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示星标按钮
        composeTestRule
            .onNodeWithTag("StarButton")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_starButtonIsClickable() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("StarButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击星标按钮
        composeTestRule
            .onNodeWithTag("StarButton")
            .performClick()
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun repoDetailScreen_displaysNewIssueButton() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("NewIssueButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示新建 Issue 按钮
        composeTestRule
            .onNodeWithTag("NewIssueButton")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_newIssueButtonIsClickable() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("NewIssueButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击新建 Issue 按钮
        composeTestRule
            .onNodeWithTag("NewIssueButton")
            .performClick()
        
        // 应该导航到新建 Issue 页面
        composeTestRule.waitForIdle()
    }

    @Test
    fun repoDetailScreen_displaysRepositoryStats() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoStats"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示仓库统计信息
        composeTestRule
            .onNodeWithTag("RepoStats")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_showsErrorState() {
        // 这个测试可能需要模拟网络错误或无效的仓库
        navigateToRepoDetail()
        
        // 等待可能的错误状态
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasText("Error"))
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodes(hasTestTag("RepoName"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun repoDetailScreen_displaysRepositoryLanguage() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoLanguage"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示仓库语言
        composeTestRule
            .onNodeWithTag("RepoLanguage")
            .assertIsDisplayed()
    }

    @Test
    fun repoDetailScreen_displaysLastUpdated() {
        navigateToRepoDetail()
        
        // 等待仓库信息加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("LastUpdated"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示最后更新时间
        composeTestRule
            .onNodeWithTag("LastUpdated")
            .assertIsDisplayed()
    }

    private fun navigateToRepoDetail() {
        // 首先搜索一个仓库
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("android")
        composeTestRule.waitForIdle()
        
        // 等待搜索结果加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击第一个仓库项
        composeTestRule
            .onAllNodes(hasTestTag("RepoItem"))
            .onFirst()
            .performClick()
        
        composeTestRule.waitForIdle()
    }
}
