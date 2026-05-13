package com.softfocus.robots.profile

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.softfocus.helpers.TestTags
import com.softfocus.robots.BaseRobot

class ProfileRobot(composeTestRule: ComposeTestRule) : BaseRobot(composeTestRule) {

    fun assertProfileScreenVisible(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_SCREEN)
        return this
    }

    fun assertNameDisplayed(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_NAME_TEXT)
        return this
    }

    fun assertEmailDisplayed(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_EMAIL_TEXT)
        return this
    }

    fun assertErrorVisible(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_ERROR_MESSAGE)
        return this
    }

    fun assertLoadingVisible(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_LOADING)
        return this
    }

    fun assertPsychologistCardVisible(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_ASSIGNED_PSYCHOLOGIST_CARD)
        return this
    }

    fun assertNoPsychologistVisible(): ProfileRobot {
        assertVisible(TestTags.Profile.PROFILE_NO_THERAPIST_TEXT)
        return this
    }

    fun tapLogout(): ProfileRobot {
        click(TestTags.Profile.PROFILE_LOGOUT_BUTTON)
        return this
    }

    fun tapDisconnectPsychologist(): ProfileRobot {
        click(TestTags.Profile.PROFILE_DISCONNECT_BUTTON)
        return this
    }

    fun confirmDisconnect(): ProfileRobot {
        click(TestTags.Profile.PROFILE_DISCONNECT_CONFIRM_BUTTON)
        return this
    }
}
