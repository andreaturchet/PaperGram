package com.ciwrl.papergram.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object UserPreferences {
    private const val PREFS_NAME = "papergram_prefs"
    private const val KEY_CATEGORIES = "selected_categories"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCategories(context: Context, categories: Set<String>) {
        getPrefs(context).edit() { putStringSet(KEY_CATEGORIES, categories) }
    }

    fun getCategories(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_CATEGORIES, setOf("cs.AI")) ?: setOf("cs.AI")
    }
}