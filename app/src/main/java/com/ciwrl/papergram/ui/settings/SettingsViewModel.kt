package com.ciwrl.papergram.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ciwrl.papergram.data.UserPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Manages UI logic and acts as an intermediary for the [SettingsFragment].
 *
 * This ViewModel provides methods to interact with user preferences stored in [UserPreferences].
 * Unlike other ViewModels in this app, it does not hold a reactive UI state via StateFlow,
 * but instead offers direct functions to get and set application settings.
 *
 * Its responsibilities include:
 * - Retrieving and saving the user's display name.
 * - Retrieving the current theme setting (Light, Dark, System Default).
 * - Applying a new theme to the application via [AppCompatDelegate] and persisting the choice.
 * - Retrieving the current language setting.
 * - Applying a new language to the application via [AppCompatDelegate] and persisting the choice.
 *
 * @param application The application instance, required for accessing the context.
 */

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    fun getUserName(): String {
        return UserPreferences.getUserName(getApplication())
    }

    fun saveUserName(newName: String) {
        UserPreferences.saveUserName(getApplication(), newName)
    }

    fun saveFeedMode(mode: String) {
        UserPreferences.saveFeedMode(getApplication(), mode)
    }

    fun getFeedMode(): String {
        return UserPreferences.getFeedMode(getApplication())
    }

    fun setTheme(themeMode: Int) {
        UserPreferences.saveTheme(getApplication(), themeMode)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    fun getTheme(): Int {
        return UserPreferences.getTheme(getApplication())
    }

    fun getLanguage(): String {
        return UserPreferences.getLanguage(getApplication())
    }

    fun setLanguage(languageCode: String) {
        UserPreferences.saveLanguage(getApplication(), languageCode)

        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}