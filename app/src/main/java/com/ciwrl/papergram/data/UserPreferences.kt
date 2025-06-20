package com.ciwrl.papergram.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * A singleton object to manage user preferences using [SharedPreferences].
 *
 * This class provides a centralized way to save and retrieve simple key-value data,
 * such as the user's name, selected categories, theme, and onboarding status.
 */

object UserPreferences {
    private const val PREFS_NAME = "papergram_prefs"
    private const val KEY_CATEGORIES = "selected_categories"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_THEME = "selected_theme"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_FEED_MODE = "feed_mode"

    const val FEED_MODE_CHRONOLOGICAL = "chronological"
    const val FEED_MODE_RANDOM = "random"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCategories(context: Context, categories: Set<String>) {
        getPrefs(context).edit() { putStringSet(KEY_CATEGORIES, categories) }
    }

    fun getCategories(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_CATEGORIES, setOf("cs.AI")) ?: setOf("cs.AI")
    }

    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        getPrefs(context).edit { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    fun saveUserName(context: Context, name: String) {
        getPrefs(context).edit { putString(KEY_USER_NAME, name) }
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "PaperGram User") ?: "PaperGram User"
    }

    fun saveTheme(context: Context, themeMode: Int) {
        getPrefs(context).edit { putInt(KEY_THEME, themeMode) }
    }

    fun getTheme(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME, -1)
    }

    fun saveLanguage(context: Context, languageCode: String) {
        getPrefs(context).edit { putString(KEY_LANGUAGE, languageCode) }
    }

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "it") ?: "it"
    }

    fun saveFeedMode(context: Context, mode: String) {
        getPrefs(context).edit { putString(KEY_FEED_MODE, mode) }
    }

    fun getFeedMode(context: Context): String {
        return getPrefs(context).getString(KEY_FEED_MODE, FEED_MODE_CHRONOLOGICAL) ?: FEED_MODE_CHRONOLOGICAL
    }
}