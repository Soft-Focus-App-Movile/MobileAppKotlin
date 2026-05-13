package com.softfocus.helpers

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo

/**
 * Extension functions on ComposeTestRule for common UI actions.
 *
 * WHY this file?
 * - Eliminates repetitive boilerplate in every test
 * - Centralizes interaction logic: if Compose API changes, fix here only
 * - Makes tests read like plain English
 *
 * These are used inside Robot classes, not directly in tests.
 */

fun ComposeTestRule.clickOnTag(tag: String) {
    onNodeWithTag(tag).performClick()
}

fun ComposeTestRule.clickOnText(text: String) {
    onNodeWithText(text).performClick()
}

fun ComposeTestRule.typeIntoTag(tag: String, text: String) {
    onNodeWithTag(tag).performTextClearance()
    onNodeWithTag(tag).performTextInput(text)
}

fun ComposeTestRule.assertTagVisible(tag: String) {
    onNodeWithTag(tag).assertIsDisplayed()
}

fun ComposeTestRule.assertTagNotVisible(tag: String) {
    onNodeWithTag(tag).assertIsNotDisplayed()
}

fun ComposeTestRule.assertTagEnabled(tag: String) {
    onNodeWithTag(tag).assertIsEnabled()
}

fun ComposeTestRule.assertTextOnTag(tag: String, expectedText: String) {
    onNodeWithTag(tag).assertTextEquals(expectedText)
}

fun ComposeTestRule.scrollToTag(tag: String) {
    onNodeWithTag(tag).performScrollTo()
}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilTagVisible(tag: String, timeoutMs: Long = 5_000) {
    waitUntilAtLeastOneExists(hasTestTag(tag), timeoutMs)
}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilTagGone(tag: String, timeoutMs: Long = 5_000) {
    waitUntilDoesNotExist(hasTestTag(tag), timeoutMs)
}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilTextVisible(text: String, timeoutMs: Long = 5_000) {
    waitUntilAtLeastOneExists(
        androidx.compose.ui.test.hasText(text),
        timeoutMs
    )
}
