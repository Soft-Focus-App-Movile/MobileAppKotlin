package com.softfocus.robots.crisis

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.softfocus.helpers.TestTags
import com.softfocus.robots.BaseRobot

class CrisisAlertsRobot(composeTestRule: ComposeTestRule, stepDelayMs: Long = 800) : BaseRobot(composeTestRule, stepDelayMs) {

    fun assertLoadingVisible(): CrisisAlertsRobot {
        assertVisible("CrisisAlertsLoading")
        return this
    }

    fun assertEmptyStateVisible(): CrisisAlertsRobot {
        assertVisible("CrisisAlertsEmpty")
        return this
    }

    fun assertAlertsListVisible(): CrisisAlertsRobot {
        assertVisible("CrisisAlertsList")
        return this
    }

    fun assertErrorVisible(): CrisisAlertsRobot {
        assertVisible("CrisisAlertsError")
        return this
    }

    fun clickUpdateStatusButton(): CrisisAlertsRobot {
        click("UpdateStatusButton")
        return this
    }
}