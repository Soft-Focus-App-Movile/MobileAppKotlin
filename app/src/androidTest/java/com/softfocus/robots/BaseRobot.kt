package com.softfocus.robots

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.softfocus.helpers.assertTagNotVisible
import com.softfocus.helpers.assertTagVisible
import com.softfocus.helpers.clickOnTag
import com.softfocus.helpers.typeIntoTag
import com.softfocus.helpers.waitUntilTagGone
import com.softfocus.helpers.waitUntilTagVisible

/**
 * Base class for all Robot classes.
 *
 * A Robot encapsulates HOW to interact with a screen.
 * Tests only describe WHAT they want to do.
 *
 * WHY Robot Pattern?
 * - If the UI changes (tag names, layout), only the Robot needs updating
 * - Tests remain stable and readable
 * - Reusable: multiple tests share the same Robot
 *
 * Each screen should have its own Robot that extends this class.
 *
 * Example usage in a test:
 *   loginRobot
 *       .enterEmail("user@email.com")
 *       .enterPassword("12345")
 *       .tapLogin()
 *       .assertLoginSuccess()
 */
abstract class BaseRobot(protected val composeTestRule: ComposeTestRule) {

    protected fun click(tag: String): BaseRobot {
        composeTestRule.clickOnTag(tag)
        return this
    }

    protected fun type(tag: String, text: String): BaseRobot {
        composeTestRule.typeIntoTag(tag, text)
        return this
    }

    protected fun assertVisible(tag: String): BaseRobot {
        composeTestRule.assertTagVisible(tag)
        return this
    }

    protected fun assertNotVisible(tag: String): BaseRobot {
        composeTestRule.assertTagNotVisible(tag)
        return this
    }

    protected fun waitUntilVisible(tag: String, timeoutMs: Long = 5_000): BaseRobot {
        composeTestRule.waitUntilTagVisible(tag, timeoutMs)
        return this
    }

    protected fun waitUntilGone(tag: String, timeoutMs: Long = 5_000): BaseRobot {
        composeTestRule.waitUntilTagGone(tag, timeoutMs)
        return this
    }
}
