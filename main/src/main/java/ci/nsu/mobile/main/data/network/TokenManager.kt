package ci.nsu.mobile.main.data.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "jwt_token"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = preferences.getString(KEY_TOKEN, null)
        set(value) {
            preferences.edit().putString(KEY_TOKEN, value).apply()
        }

    fun clearToken() {
        preferences.edit().remove(KEY_TOKEN).apply()
    }

    val isAuthenticated: Boolean
        get() = !token.isNullOrEmpty()
}