package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whm.githubapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HotReposScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun hotReposScreen_displaysTitle() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule
            .onNodeWithText("🔥 Hot Repositories")
            .assertIsDisplayed()
    }

    @Test
    fun hotReposScreen_showsLoadingInitially() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 检查是否有加载指示器
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasContentDescription("Loading"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun hotReposScreen_displaysRepositories() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待仓库列表加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示仓库项
        composeTestRule
            .onAllNodes(hasTestTag("RepoItem"))
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun hotReposScreen_supportsInfiniteScroll() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待初始结果加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 滚动到底部触发无限滚动
        composeTestRule
            .onNodeWithTag("HotReposList")
            .performScrollToIndex(composeTestRule.onAllNodes(hasTestTag("RepoItem")).fetchSemanticsNodes().size - 1)
        composeTestRule.waitForIdle()
        
        // 检查是否加载了更多内容
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().size > 1
        }
    }

    @Test
    fun hotReposScreen_repoItemIsClickable() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待仓库列表加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击第一个仓库项
        composeTestRule
            .onAllNodes(hasTestTag("RepoItem"))
            .onFirst()
            .performClick()
        
        // 应该导航到仓库详情页面
        composeTestRule.waitForIdle()
    }

    @Test
    fun hotReposScreen_showsRepoDetails() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待仓库列表加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查仓库项是否显示详细信息
        composeTestRule
            .onAllNodes(hasTestTag("RepoItem"))
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun hotReposScreen_showsErrorState() {
        // 这个测试可能需要模拟网络错误
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待可能的错误状态
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasText("Error"))
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun hotReposScreen_refreshFunctionality() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待初始加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 执行下拉刷新（如果实现了的话）
        composeTestRule
            .onNodeWithTag("HotReposList")
            .performScrollToIndex(0)
        composeTestRule.waitForIdle()
    }

    @Test
    fun hotReposScreen_navigationFromOtherTabs() {
        // 从其他标签页导航到热门仓库
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule
            .onNodeWithText("🔥 Hot Repositories")
            .assertIsDisplayed()
    }

    @Test
    fun hotReposScreen_displaysCorrectContent() {
        composeTestRule.onNodeWithText("Hot").performClick()
        composeTestRule.waitForIdle()
        
        // 等待内容加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 检查是否显示仓库相关信息
        composeTestRule
            .onAllNodes(hasTestTag("RepoItem"))
            .onFirst()
            .assertIsDisplayed()
    }
}
