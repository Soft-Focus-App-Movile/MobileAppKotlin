package com.softfocus.robots

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.softfocus.helpers.assertTagNotVisible
import com.softfocus.helpers.assertTagVisible
import com.softfocus.helpers.clickOnTag
import com.softfocus.helpers.typeIntoTag
import com.softfocus.helpers.waitUntilTagGone
import com.softfocus.helpers.waitUntilTagVisible

abstract class BaseRobot(
    protected val composeTestRule: ComposeTestRule,
    private val stepDelayMs: Long = 800
) {

    private fun step() {
        if (stepDelayMs > 0) Thread.sleep(stepDelayMs)
    }

    protected fun click(tag: String): BaseRobot {
        composeTestRule.clickOnTag(tag)
        step()
        return this
    }

    protected fun type(tag: String, text: String): BaseRobot {
        composeTestRule.typeIntoTag(tag, text)
        step()
        return this
    }

    protected fun assertVisible(tag: String): BaseRobot {
        composeTestRule.assertTagVisible(tag)
        step()
        return this
    }

    protected fun assertNotVisible(tag: String): BaseRobot {
        composeTestRule.assertTagNotVisible(tag)
        step()
        return this
    }

    protected fun waitUntilVisible(tag: String, timeoutMs: Long = 5_000): BaseRobot {
        composeTestRule.waitUntilTagVisible(tag, timeoutMs)
        step()
        return this
    }

    protected fun waitUntilGone(tag: String, timeoutMs: Long = 5_000): BaseRobot {
        composeTestRule.waitUntilTagGone(tag, timeoutMs)
        step()
        return this
    }
}
