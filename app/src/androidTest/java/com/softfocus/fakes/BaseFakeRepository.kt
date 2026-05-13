package com.softfocus.fakes

/**
 * Marker interface for all Fake repositories used in tests.
 *
 * Fakes replace real repositories in tests so we never call
 * the real network/database. They return controlled responses.
 *
 * WHY Fakes instead of Mocks?
 * - Fakes are real implementations with controlled behavior
 * - Mocks (MockK) are better for unit tests where you stub individual calls
 * - Fakes are better for integration/UI tests: more realistic, less fragile
 *
 * HOW TO USE:
 * 1. Create a FakeXRepository that implements XRepository
 * 2. Add properties to control what it returns (shouldFail, fakeUser, etc.)
 * 3. Replace the real repository in a @TestInstallIn Hilt module
 */
interface BaseFakeRepository
