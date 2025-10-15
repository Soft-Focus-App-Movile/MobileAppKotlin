package com.softfocus.features.auth.data.remote

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await

/**
 * Helper class for handling Google Sign-In using Play Services Auth.
 *
 * This class provides Google authentication using the Play Services Auth library
 * which returns access tokens compatible with the backend OAuth flow.
 */
class GoogleSignInManager(
    private val context: Context
) {
    private var googleSignInClient: GoogleSignInClient? = null

    /**
     * Initiates the Google Sign-In flow and returns the access token on success.
     *
     * @param serverClientId The OAuth 2.0 server client ID for your backend
     * @return Result containing Google Sign-In result with access token
     */
    suspend fun signIn(serverClientId: String): Result<GoogleSignInResult> {
        return try {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)


            try {
                googleSignInClient?.signOut()?.await()
                android.util.Log.d("GoogleSignInManager", "Signed out to get fresh serverAuthCode")
            } catch (e: Exception) {
                android.util.Log.w("GoogleSignInManager", "Sign out failed: ${e.message}")
            }


            android.util.Log.d("GoogleSignInManager", "Need to show Google Sign-In UI")
            Result.failure(GoogleSignInRequiredException("User needs to sign in"))

        } catch (e: Exception) {
            android.util.Log.e("GoogleSignInManager", "Sign-in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Gets the Intent to launch Google Sign-In activity.
     * This Intent should be launched with startActivityForResult.
     */
    fun getSignInIntent(serverClientId: String): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(serverClientId)
            .requestServerAuthCode(serverClientId)
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient?.signInIntent ?: throw IllegalStateException("GoogleSignInClient not initialized")
    }

    /**
     * Handles the result from the Google Sign-In Intent.
     * Call this from onActivityResult.
     */
    suspend fun handleSignInResult(data: Intent?): Result<GoogleSignInResult> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.await()

            android.util.Log.d("GoogleSignInManager", "Sign-in successful for: ${account.email}")
            Result.success(mapAccountToResult(account))

        } catch (e: ApiException) {
            android.util.Log.e("GoogleSignInManager", "Sign-in failed with ApiException: ${e.statusCode}", e)
            Result.failure(e)
        } catch (e: Exception) {
            android.util.Log.e("GoogleSignInManager", "Sign-in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut() {
        try {
            googleSignInClient?.signOut()?.await()
            android.util.Log.d("GoogleSignInManager", "User signed out")
        } catch (e: Exception) {
            android.util.Log.e("GoogleSignInManager", "Error signing out: ${e.message}", e)
        }
    }

    private fun mapAccountToResult(account: GoogleSignInAccount): GoogleSignInResult {

        val token = account.serverAuthCode ?: account.idToken ?: ""

        android.util.Log.d("GoogleSignInManager", "Token type: ${if (account.serverAuthCode != null) "serverAuthCode" else "idToken"}")
        android.util.Log.d("GoogleSignInManager", "Token: ${token.take(50)}...")

        return GoogleSignInResult(
            idToken = token,
            email = account.email ?: "",
            displayName = account.displayName,
            profilePictureUri = account.photoUrl?.toString()
        )
    }
}

/**
 * Data class representing the result of a successful Google Sign-In.
 */
data class GoogleSignInResult(
    val idToken: String,
    val email: String,
    val displayName: String?,
    val profilePictureUri: String?
)

/**
 * Exception thrown when user interaction is required for Google Sign-In.
 */
class GoogleSignInRequiredException(message: String) : Exception(message)
