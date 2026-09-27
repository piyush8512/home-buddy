package com.example.buddy.data.auth

import com.example.buddy.data.network.RetrofitClient
import com.example.buddy.data.user.User
import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.buddy.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthRepository(
    private val context: Context
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(
        activity: Activity
    ): Result<Unit> {

        return try {

            val credentialManager =
                CredentialManager.create(context)

            val googleIdOption =
                GetGoogleIdOption.Builder()
                    .setServerClientId(
                        context.getString(
                            R.string.default_web_client_id
                        )
                    )
                    .setFilterByAuthorizedAccounts(false)
                    .build()

            val request =
                GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

            // IMPORTANT:
            // Use Activity context here because Credential Manager
            // needs to launch the Google account selector UI.
            val result =
                credentialManager.getCredential(
                    context = activity,
                    request = request
                )

            val googleCredential =
                GoogleIdTokenCredential.createFrom(
                    result.credential.data
                )

            val firebaseCredential =
                GoogleAuthProvider.getCredential(
                    googleCredential.idToken,
                    null
                )

            auth.signInWithCredential(firebaseCredential).await()

            Result.success(Unit)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)
        }
    }


    suspend fun getBackendUser(): Result<User> {

        return try {

            val firebaseUser = auth.currentUser
                ?: return Result.failure(
                    Exception("User is not logged in")
                )

            val tokenResult = firebaseUser
                .getIdToken(true)
                .await()

            val token = tokenResult.token
                ?: return Result.failure(
                    Exception("Firebase token is null")
                )

            val user = RetrofitClient
                .userApi
                .getCurrentUser("Bearer $token")

            Result.success(user)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
}