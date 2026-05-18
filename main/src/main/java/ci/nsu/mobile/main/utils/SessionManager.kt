package ci.nsu.mobile.main.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveAuth(token: String, userId: Long) {
        prefs.edit().apply {
            putString("token", token)
            putLong("userId", userId)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString("token", null)
    fun getUserId(): Long = prefs.getLong("userId", -1)
    fun isLoggedIn(): Boolean = getToken() != null
    fun clearSession() = prefs.edit().clear().apply()
}