package com.softfocus.features.auth.presentation.accountreview

import androidx.lifecycle.ViewModel

/**
 * ViewModel for AccountReview screen.
 * Simple screen that shows "Tu cuenta está siendo revisada" message
 * for psychologists whose account is pending verification.
 *
 * No additional functionality needed - documents are already uploaded during registration.
 */
class AccountReviewViewModel : ViewModel() {
    // This screen is now just informational - no actions needed
    // Documents were already uploaded during psychologist registration
}
