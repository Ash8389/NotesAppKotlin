package com.example.notes.repository

import com.example.notes.supabase.SupabaseClientHolder
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import javax.inject.Inject

class SupabaseAuthRepository @Inject constructor() {

    private val client = SupabaseClientHolder.client

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return client.auth.currentSessionOrNull() != null
    }
    
    fun getCurrentUserEmail(): String? {
        return client.auth.currentUserOrNull()?.email
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            client.auth.resetPasswordForEmail(email, redirectUrl = "notesapp://auth/callback")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(password: String): Result<Unit> {
        return try {
            client.auth.modifyUser(true) {
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun handlePasswordResetLink(uriFragment: String): Result<Unit> {
        return try {
            val params = uriFragment.split("&").associate {
                val parts = it.split("=", limit = 2)
                parts[0] to parts.getOrElse(1) { "" }
            }
            val accessToken = params["access_token"]
            val refreshToken = params["refresh_token"]
            val expiresIn = params["expires_in"]?.toLongOrNull() ?: 3600

            if (accessToken != null && refreshToken != null) {
                val session = UserSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = expiresIn,
                    tokenType = "bearer",
                    user = null
                )
                client.auth.importSession(session)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid link: missing tokens"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
