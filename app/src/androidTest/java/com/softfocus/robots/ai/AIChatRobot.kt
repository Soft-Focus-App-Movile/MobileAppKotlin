package com.softfocus.robots.ai

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.softfocus.helpers.TestTags
import com.softfocus.robots.BaseRobot

class AIChatRobot(composeTestRule: ComposeTestRule) : BaseRobot(composeTestRule) {

    fun assertChatScreenVisible(): AIChatRobot {
        assertVisible(TestTags.AI.AI_CHAT_SCREEN)
        return this
    }

    fun typeMessage(message: String): AIChatRobot {
        type(TestTags.AI.AI_CHAT_INPUT_FIELD, message)
        return this
    }

    fun tapSend(): AIChatRobot {
        click(TestTags.AI.AI_CHAT_SEND_BUTTON)
        return this
    }

    fun assertSendButtonEnabled(): AIChatRobot {
        composeTestRule.onNodeWithTag(TestTags.AI.AI_CHAT_SEND_BUTTON).assertIsEnabled()
        return this
    }

    fun assertSendButtonDisabled(): AIChatRobot {
        composeTestRule.onNodeWithTag(TestTags.AI.AI_CHAT_SEND_BUTTON).assertIsNotEnabled()
        return this
    }

    fun assertMessageListVisible(): AIChatRobot {
        assertVisible(TestTags.AI.AI_CHAT_MESSAGE_LIST)
        return this
    }

    fun assertLimitWarningVisible(): AIChatRobot {
        assertVisible(TestTags.AI.AI_CHAT_LIMIT_WARNING)
        return this
    }

    fun assertLimitWarningGone(): AIChatRobot {
        assertNotVisible(TestTags.AI.AI_CHAT_LIMIT_WARNING)
        return this
    }

    fun sendMessage(message: String): AIChatRobot {
        return typeMessage(message).tapSend()
    }
}
