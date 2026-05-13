package com.softfocus.robots.therapy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.softfocus.robots.BaseRobot

class PsychologistChatRobot(composeTestRule: ComposeTestRule, stepDelayMs: Long = 800) : BaseRobot(composeTestRule, stepDelayMs) {

    fun assertChatVisible(): PsychologistChatRobot {
        assertVisible("ChatScreen")
        return this
    }

    fun typeMessage(message: String): PsychologistChatRobot {
        composeTestRule.onNodeWithTag("ChatInputTextField").performTextInput(message)
        return this
    }

    fun clickSend(): PsychologistChatRobot {
        click("ChatSendButton")
        return this
    }

    fun assertMessageVisible(message: String): PsychologistChatRobot {
        composeTestRule.onNodeWithText(message).assertIsDisplayed()
        return this
    }
}