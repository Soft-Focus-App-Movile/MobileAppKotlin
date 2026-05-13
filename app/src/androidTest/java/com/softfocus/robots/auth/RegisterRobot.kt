package com.softfocus.robots.auth

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.softfocus.helpers.TestTags
import com.softfocus.robots.BaseRobot

/**
 * Robot para RegisterScreen.
 * Cubre flujo General y flujo Psicólogo (con switch de tipo de cuenta).
 */
class RegisterRobot(composeTestRule: ComposeTestRule) : BaseRobot(composeTestRule) {

    fun enterFirstName(name: String): RegisterRobot {
        type(TestTags.Auth.REGISTER_FIRST_NAME_FIELD, name)
        return this
    }

    fun enterLastName(name: String): RegisterRobot {
        type(TestTags.Auth.REGISTER_LAST_NAME_FIELD, name)
        return this
    }

    fun enterEmail(email: String): RegisterRobot {
        type(TestTags.Auth.REGISTER_EMAIL_FIELD, email)
        return this
    }

    fun enterPassword(password: String): RegisterRobot {
        type(TestTags.Auth.REGISTER_PASSWORD_FIELD, password)
        return this
    }

    fun enterConfirmPassword(password: String): RegisterRobot {
        type(TestTags.Auth.REGISTER_CONFIRM_PASSWORD_FIELD, password)
        return this
    }

    fun switchToPsychologist(): RegisterRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.REGISTER_USER_TYPE_SWITCH).performClick()
        return this
    }

    fun switchToGeneral(): RegisterRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.REGISTER_USER_TYPE_SWITCH).performClick()
        return this
    }

    fun tapRegisterButton(): RegisterRobot {
        click(TestTags.Auth.REGISTER_BUTTON)
        return this
    }

    fun tapLoginLink(): RegisterRobot {
        click(TestTags.Auth.REGISTER_LOGIN_LINK)
        return this
    }

    // --- Assertions generales ---

    fun assertRegisterScreenVisible(): RegisterRobot {
        assertVisible(TestTags.Auth.REGISTER_SCREEN)
        return this
    }

    fun assertRegisterButtonEnabled(): RegisterRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.REGISTER_BUTTON).assertIsEnabled()
        return this
    }

    fun assertRegisterButtonDisabled(): RegisterRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.REGISTER_BUTTON).assertIsNotEnabled()
        return this
    }

    fun assertLoadingVisible(): RegisterRobot {
        assertVisible(TestTags.Auth.REGISTER_LOADING)
        return this
    }

    fun assertLoadingGone(): RegisterRobot {
        assertNotVisible(TestTags.Auth.REGISTER_LOADING)
        return this
    }

    fun assertErrorVisible(): RegisterRobot {
        assertVisible(TestTags.Auth.REGISTER_ERROR_MESSAGE)
        return this
    }

    fun assertSwitchIsGeneral(): RegisterRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.REGISTER_USER_TYPE_SWITCH).assertIsOff()
        return this
    }

    fun assertSwitchIsPsychologist(): RegisterRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.REGISTER_USER_TYPE_SWITCH).assertIsOn()
        return this
    }

    // --- Flujos completos ---

    fun fillGeneralUserForm(
        firstName: String = "Juan",
        lastName: String = "Pérez",
        email: String = "juan@softfocus.com",
        password: String = "Password1@",
        confirmPassword: String = "Password1@"
    ): RegisterRobot {
        return enterFirstName(firstName)
            .enterLastName(lastName)
            .enterEmail(email)
            .enterPassword(password)
            .enterConfirmPassword(confirmPassword)
    }
}
