package com.softfocus.robots.auth

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.softfocus.helpers.TestTags
import com.softfocus.robots.BaseRobot

/**
 * Robot para la pantalla de Login.
 *
 * Encapsula CÓMO interactuar con LoginScreen.
 * Los tests solo describen QUÉ quieren hacer.
 *
 * Si cambia un tag o un texto en la pantalla, solo se actualiza aquí.
 */
class LoginRobot(composeTestRule: ComposeTestRule) : BaseRobot(composeTestRule) {

    fun enterEmail(email: String): LoginRobot {
        type(TestTags.Auth.LOGIN_EMAIL_FIELD, email)
        return this
    }

    fun enterPassword(password: String): LoginRobot {
        type(TestTags.Auth.LOGIN_PASSWORD_FIELD, password)
        return this
    }

    fun tapLoginButton(): LoginRobot {
        click(TestTags.Auth.LOGIN_BUTTON)
        return this
    }

    fun tapForgotPassword(): LoginRobot {
        click(TestTags.Auth.LOGIN_FORGOT_PASSWORD_LINK)
        return this
    }

    fun tapRegisterLink(): LoginRobot {
        click(TestTags.Auth.LOGIN_REGISTER_LINK)
        return this
    }

    fun tapGoogleButton(): LoginRobot {
        click(TestTags.Auth.LOGIN_GOOGLE_BUTTON)
        return this
    }

    // --- Assertions ---

    fun assertLoginScreenVisible(): LoginRobot {
        assertVisible(TestTags.Auth.LOGIN_SCREEN)
        return this
    }

    fun assertLoadingVisible(): LoginRobot {
        assertVisible(TestTags.Auth.LOGIN_LOADING)
        return this
    }

    fun assertLoadingGone(): LoginRobot {
        assertNotVisible(TestTags.Auth.LOGIN_LOADING)
        return this
    }

    fun assertErrorVisible(): LoginRobot {
        assertVisible(TestTags.Auth.LOGIN_ERROR_MESSAGE)
        return this
    }

    fun assertErrorGone(): LoginRobot {
        assertNotVisible(TestTags.Auth.LOGIN_ERROR_MESSAGE)
        return this
    }

    fun assertLoginButtonEnabled(): LoginRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.LOGIN_BUTTON).assertIsEnabled()
        return this
    }

    fun assertLoginButtonDisabled(): LoginRobot {
        composeTestRule.onNodeWithTag(TestTags.Auth.LOGIN_BUTTON).assertIsNotEnabled()
        return this
    }

    // --- Flujos completos reutilizables ---

    fun fillAndSubmitLogin(email: String, password: String): LoginRobot {
        return enterEmail(email)
            .enterPassword(password)
            .tapLoginButton()
    }
}
