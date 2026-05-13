package com.softfocus.base

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Base class for all Integration Tests.
 *
 * Provides:
 * - HiltAndroidRule: manages Hilt injection lifecycle in tests
 * - createAndroidComposeRule<MainActivity>: launches the real Activity
 * - hiltRule.inject() called automatically in @Before
 *
 * Integration Tests use real navigation and real Activity,
 * but repositories are replaced with Fakes via @UninstallModules.
 *
 * All IntegrationTests should extend this class.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
abstract class BaseIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    open fun setUp() {
        hiltRule.inject()
    }
}
