package com.softfocus.helpers

/**
 * Central registry of all Compose Test Tags used across the app.
 *
 * WHY a central file?
 * - Avoid typos: "login_button" vs "loginButton" vs "LoginButton"
 * - Single source of truth: test and production code import from here
 * - Easier refactoring: rename in one place, not across 20 files
 *
 * HOW TO USE in your Composable:
 *   Button(modifier = Modifier.testTag(TestTags.Auth.LOGIN_BUTTON)) { ... }
 *
 * HOW TO USE in your test:
 *   composeTestRule.onNodeWithTag(TestTags.Auth.LOGIN_BUTTON).performClick()
 *
 * NAMING CONVENTION:
 * - Grouped by feature/bounded context
 * - SCREAMING_SNAKE_CASE for constants
 * - Object name matches feature folder name
 */
object TestTags {

    object Auth {
        const val LOGIN_SCREEN = "login_screen"
        const val LOGIN_EMAIL_FIELD = "login_email_field"
        const val LOGIN_PASSWORD_FIELD = "login_password_field"
        const val LOGIN_BUTTON = "login_button"
        const val LOGIN_LOADING = "login_loading"
        const val LOGIN_ERROR_MESSAGE = "login_error_message"
        const val LOGIN_GOOGLE_BUTTON = "login_google_button"
        const val LOGIN_REGISTER_LINK = "login_register_link"
        const val LOGIN_FORGOT_PASSWORD_LINK = "login_forgot_password_link"

        const val REGISTER_SCREEN = "register_screen"
        const val REGISTER_FIRST_NAME_FIELD = "register_first_name_field"
        const val REGISTER_LAST_NAME_FIELD = "register_last_name_field"
        const val REGISTER_EMAIL_FIELD = "register_email_field"
        const val REGISTER_PASSWORD_FIELD = "register_password_field"
        const val REGISTER_CONFIRM_PASSWORD_FIELD = "register_confirm_password_field"
        const val REGISTER_USER_TYPE_SWITCH = "register_user_type_switch"
        const val REGISTER_BUTTON = "register_button"
        const val REGISTER_LOADING = "register_loading"
        const val REGISTER_ERROR_MESSAGE = "register_error_message"
        const val REGISTER_LOGIN_LINK = "register_login_link"
        const val REGISTER_PRIVACY_CHECKBOX = "register_privacy_checkbox"
        const val REGISTER_EMAIL_ERROR = "register_email_error"
        const val REGISTER_PASSWORD_ERROR = "register_password_error"
        const val REGISTER_CONFIRM_PASSWORD_ERROR = "register_confirm_password_error"
        // Campos psicólogo
        const val REGISTER_LICENSE_FIELD = "register_license_field"
        const val REGISTER_YEARS_EXPERIENCE_FIELD = "register_years_experience_field"
        const val REGISTER_REGION_FIELD = "register_region_field"
        const val REGISTER_UNIVERSITY_FIELD = "register_university_field"
        const val REGISTER_GRADUATION_YEAR_FIELD = "register_graduation_year_field"

        const val FORGOT_PASSWORD_SCREEN = "forgot_password_screen"
        const val FORGOT_PASSWORD_EMAIL_FIELD = "forgot_password_email_field"
        const val FORGOT_PASSWORD_BUTTON = "forgot_password_button"
    }

    object Profile {
        const val PROFILE_SCREEN = "profile_screen"
        const val PROFILE_NAME_TEXT = "profile_name_text"
        const val PROFILE_EMAIL_TEXT = "profile_email_text"
        const val PROFILE_LOGOUT_BUTTON = "profile_logout_button"
        const val PROFILE_LOADING = "profile_loading"
        const val PROFILE_ERROR_MESSAGE = "profile_error_message"
        const val PROFILE_ASSIGNED_PSYCHOLOGIST_CARD = "profile_assigned_psychologist_card"
        const val PROFILE_DISCONNECT_BUTTON = "profile_disconnect_button"
        const val PROFILE_DISCONNECT_CONFIRM_BUTTON = "profile_disconnect_confirm_button"
        const val PROFILE_NO_THERAPIST_TEXT = "profile_no_therapist_text"
        const val PROFILE_EDIT_BUTTON = "profile_edit_button"

        // Psicólogo
        const val PSYCHOLOGIST_PROFILE_SCREEN = "psychologist_profile_screen"
        const val PSYCHOLOGIST_PROFILE_NAME_TEXT = "psychologist_profile_name_text"
        const val PSYCHOLOGIST_PROFILE_EMAIL_TEXT = "psychologist_profile_email_text"
        const val PSYCHOLOGIST_PROFILE_LOGOUT_BUTTON = "psychologist_profile_logout_button"
        const val PSYCHOLOGIST_PROFILE_SPECIALTIES = "psychologist_profile_specialties"
        const val PSYCHOLOGIST_PROFILE_ERROR_TEXT = "psychologist_profile_error_text"

        const val EDIT_PROFILE_SCREEN = "edit_profile_screen"
        const val EDIT_PROFILE_FIRST_NAME_FIELD = "edit_profile_first_name_field"
        const val EDIT_PROFILE_LAST_NAME_FIELD = "edit_profile_last_name_field"
        const val EDIT_PROFILE_BIO_FIELD = "edit_profile_bio_field"
        const val EDIT_PROFILE_SAVE_BUTTON = "edit_profile_save_button"
        const val EDIT_PROFILE_LOADING = "edit_profile_loading"
        const val EDIT_PROFILE_ERROR_MESSAGE = "edit_profile_error_message"
    }

    object AI {
        const val AI_CHAT_SCREEN = "ai_chat_screen"
        const val AI_CHAT_INPUT_FIELD = "ai_chat_input_field"
        const val AI_CHAT_SEND_BUTTON = "ai_chat_send_button"
        const val AI_CHAT_MESSAGE_LIST = "ai_chat_message_list"
        const val AI_CHAT_LOADING = "ai_chat_loading"
        const val AI_CHAT_ERROR_MESSAGE = "ai_chat_error_message"
        const val AI_CHAT_NEW_CONVERSATION_BUTTON = "ai_chat_new_conversation_button"
        const val AI_CHAT_LIMIT_WARNING = "ai_chat_limit_warning"
        const val AI_WELCOME_SCREEN = "ai_welcome_screen"
        const val AI_WELCOME_INPUT_FIELD = "ai_welcome_input_field"
        const val AI_WELCOME_SEND_BUTTON = "ai_welcome_send_button"
    }

    object Home {
        const val HOME_SCREEN = "home_screen"
        const val HOME_WELCOME_TEXT = "home_welcome_text"
    }

    object Common {
        const val LOADING_INDICATOR = "loading_indicator"
        const val ERROR_SCREEN = "error_screen"
        const val ERROR_MESSAGE = "error_message"
        const val RETRY_BUTTON = "retry_button"
        const val BOTTOM_NAV_BAR = "bottom_nav_bar"
        const val TOP_APP_BAR = "top_app_bar"
        const val BACK_BUTTON = "back_button"
    }
}
