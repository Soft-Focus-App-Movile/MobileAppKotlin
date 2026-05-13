package com.softfocus.base

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import com.softfocus.ui.theme.SoftFocusMobileTheme
import org.junit.Rule

/**
 * Base class for all Compose UI Tests.
 *
 * Provides:
 * - composeTestRule already configured
 * - Theme wrapper so all screens render correctly
 * - Utility to print the semantic tree (useful for debugging)
 *
 * All UITests should extend this class.
 */
abstract class BaseComposeTest {

    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * Launches a Composable wrapped in the app theme.
     * Use this instead of composeTestRule.setContent directly.
     */
    protected fun launchScreen(content: @androidx.compose.runtime.Composable () -> Unit) {
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                content()
            }
        }
    }

    /**
     * Prints the full semantic tree to Logcat.
     * Tag: "ComposeTree"
     * Use when a test fails and you don't know the tag/text of a node.
     */
    protected fun printSemanticTree() {
        composeTestRule.onRoot().printToLog("ComposeTree")
    }
}
