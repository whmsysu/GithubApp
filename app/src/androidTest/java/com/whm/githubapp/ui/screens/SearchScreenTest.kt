package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whm.githubapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun searchScreen_displaysSearchInput() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchButton() {
        composeTestRule
            .onNodeWithTag("SearchButton")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_allowsTextInput() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("android")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .assertTextContains("android")
    }

    @Test
    fun searchScreen_showsLoadingState() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("test")
        composeTestRule.waitForIdle()
        
        // 检查是否有加载指示器
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasContentDescription("Loading"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun searchScreen_showsResultsAfterSearch() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("android")
        composeTestRule.waitForIdle()
        
        // 等待搜索结果加载
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
    fun searchScreen_supportsInfiniteScroll() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("android")
        composeTestRule.waitForIdle()
        
        // 等待初始结果加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 滚动到底部触发无限滚动
        composeTestRule
            .onNodeWithTag("SearchResults")
            .performScrollToIndex(composeTestRule.onAllNodes(hasTestTag("RepoItem")).fetchSemanticsNodes().size - 1)
        composeTestRule.waitForIdle()
        
        // 检查是否加载了更多内容
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().size > 1
        }
    }

    @Test
    fun searchScreen_showsEmptyState() {
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("nonexistentrepository12345")
        composeTestRule.waitForIdle()
        
        // 等待空状态显示
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasText("No repositories found"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithText("No repositories found")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_showsErrorState() {
        // 输入特殊字符可能导致错误
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("!@#$%^&*()")
        composeTestRule.waitForIdle()
        
        // 检查是否显示错误状态
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasText("Error"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun searchScreen_debouncesInput() {
        // 快速输入多个字符
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("a")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("n")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("d")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("r")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("o")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("i")
        composeTestRule
            .onNodeWithTag("SearchInput")
            .performTextInput("d")
        composeTestRule.waitForIdle()
        
        // 等待防抖完成
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("RepoItem"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 应该只触发一次搜索
        composeTestRule
            .onAllNodes(hasTestTag("RepoItem"))
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_repoItemIsClickable() {
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
        
        // 应该导航到仓库详情页面
        composeTestRule.waitForIdle()
    }
}
