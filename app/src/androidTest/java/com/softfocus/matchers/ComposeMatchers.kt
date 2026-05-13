package com.softfocus.matchers

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText

/**
 * Custom semantic matchers for Compose UI Tests.
 *
 * WHY custom matchers?
 * - Built-in matchers (hasTestTag, hasText) cover 90% of cases
 * - Custom matchers handle complex scenarios: nested nodes, combined conditions
 * - Keeps test code readable instead of chaining multiple conditions inline
 *
 * Add matchers here when you find yourself repeating the same
 * combination of conditions across multiple tests.
 */
object ComposeMatchers {

    /**
     * Matches a node that has a specific test tag AND contains a specific text.
     * Useful when the same tag is reused in a list and you need a specific item.
     */
    fun hasTagWithText(tag: String, text: String): SemanticsMatcher =
        hasTestTag(tag) and hasText(text)

    /**
     * Matches a node that has a child with the given test tag.
     * Useful for asserting that a container holds a specific element.
     */
    fun hasChildWithTag(childTag: String): SemanticsMatcher =
        hasAnyChild(hasTestTag(childTag))
}
