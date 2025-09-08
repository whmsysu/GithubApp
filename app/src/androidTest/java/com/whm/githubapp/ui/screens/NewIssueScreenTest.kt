package com.whm.githubapp.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whm.githubapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewIssueScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun newIssueScreen_displaysBackButton() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_backButtonIsClickable() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()
        
        // 应该返回到上一个页面
        composeTestRule.waitForIdle()
    }

    @Test
    fun newIssueScreen_displaysTitle() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithText("GitHub App")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_displaysRepositoryInfo() {
        navigateToNewIssueScreen()
        
        // 检查是否显示仓库信息
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasText("New Issue in"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithText("New Issue in")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_displaysTitleInput() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithTag("TitleInput")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_titleInputAcceptsText() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput("Test Issue Title")
        
        composeTestRule
            .onNodeWithTag("TitleInput")
            .assertTextContains("Test Issue Title")
    }

    @Test
    fun newIssueScreen_displaysDescriptionInput() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithTag("DescInput")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_descriptionInputAcceptsText() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithTag("DescInput")
            .performTextInput("Test Issue Description")
        
        composeTestRule
            .onNodeWithTag("DescInput")
            .assertTextContains("Test Issue Description")
    }

    @Test
    fun newIssueScreen_displaysSubmitButton() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_submitButtonIsDisabledWhenTitleEmpty() {
        navigateToNewIssueScreen()
        
        // 标题为空时，提交按钮应该被禁用
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .assertIsNotEnabled()
    }

    @Test
    fun newIssueScreen_submitButtonIsEnabledWhenTitleNotEmpty() {
        navigateToNewIssueScreen()
        
        // 输入标题
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput("Test Title")
        
        // 提交按钮应该被启用
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .assertIsEnabled()
    }

    @Test
    fun newIssueScreen_submitButtonShowsLoadingWhenSubmitting() {
        navigateToNewIssueScreen()
        
        // 输入标题和描述
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput("Test Title")
        composeTestRule
            .onNodeWithTag("DescInput")
            .performTextInput("Test Description")
        
        // 点击提交按钮
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .performClick()
        
        // 应该显示加载状态
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Submitting...")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_showsSuccessMessage() {
        navigateToNewIssueScreen()
        
        // 输入标题和描述
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput("Test Title")
        composeTestRule
            .onNodeWithTag("DescInput")
            .performTextInput("Test Description")
        
        // 点击提交按钮
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .performClick()
        
        // 等待成功消息显示
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasText("Issue created"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithText("Issue created")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_showsErrorMessage() {
        navigateToNewIssueScreen()
        
        // 输入无效的标题（空标题）
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput("")
        
        // 尝试提交
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .performClick()
        
        // 应该显示错误消息或按钮保持禁用状态
        composeTestRule.waitForIdle()
    }

    @Test
    fun newIssueScreen_titleInputHasPlaceholder() {
        navigateToNewIssueScreen()
        
        // 检查标题输入框是否有占位符文本
        composeTestRule
            .onNodeWithText("Enter issue title")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_descriptionInputHasPlaceholder() {
        navigateToNewIssueScreen()
        
        // 检查描述输入框是否有占位符文本
        composeTestRule
            .onNodeWithText("Describe the issue in detail")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_submitButtonHasCorrectText() {
        navigateToNewIssueScreen()
        
        composeTestRule
            .onNodeWithText("Submit Issue")
            .assertIsDisplayed()
    }

    @Test
    fun newIssueScreen_formValidation() {
        navigateToNewIssueScreen()
        
        // 测试表单验证
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput(" ")
        
        // 只有空格的标题应该仍然禁用提交按钮
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .assertIsNotEnabled()
        
        // 输入有效标题
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextClearance()
        composeTestRule
            .onNodeWithTag("TitleInput")
            .performTextInput("Valid Title")
        
        // 提交按钮应该被启用
        composeTestRule
            .onNodeWithTag("SubmitButton")
            .assertIsEnabled()
    }

    private fun navigateToNewIssueScreen() {
        // 首先导航到仓库详情页面
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
        
        // 等待仓库详情页面加载
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("NewIssueButton"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // 点击新建 Issue 按钮
        composeTestRule
            .onNodeWithTag("NewIssueButton")
            .performClick()
        
        composeTestRule.waitForIdle()
    }
}
