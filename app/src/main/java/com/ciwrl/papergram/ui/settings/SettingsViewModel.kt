package com.ciwrl.papergram.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.ciwrl.papergram.data.UserPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    fun getUserName(): String {
        return UserPreferences.getUserName(getApplication())
    }

    fun saveUserName(newName: String) {
        UserPreferences.saveUserName(getApplication(), newName)
    }

    fun setTheme(themeMode: Int) {
        UserPreferences.saveTheme(getApplication(), themeMode)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    fun getTheme(): Int {
        return UserPreferences.getTheme(getApplication())
    }
}