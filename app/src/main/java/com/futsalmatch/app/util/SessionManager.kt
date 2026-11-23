package com.futsalmatch.app.util

import android.content.Context

object SessionManager {
    private const val PREF_NAME = "futsalmatch_pref"
    private const val KEY_IS_LOGIN = "isLogin"
    private const val KEY_USER_ID = "userId"
    private const val KEY_TEAM_ID = "teamId"
    private const val KEY_USERNAME = "username"

    fun saveSession(context: Context, userId: Int, teamId: Int, username: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit()
            .putBoolean(KEY_IS_LOGIN, true)
            .putInt(KEY_USER_ID, userId)
            .putInt(KEY_TEAM_ID, teamId)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(KEY_IS_LOGIN, false)
    }

    fun getUserId(context: Context): Int {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(KEY_USER_ID, -1)
    }

    fun getTeamId(context: Context): Int {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(KEY_TEAM_ID, -1)
    }

    fun getUsername(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_USERNAME, "") ?: ""
    }

    fun clearSession(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
    }
}
