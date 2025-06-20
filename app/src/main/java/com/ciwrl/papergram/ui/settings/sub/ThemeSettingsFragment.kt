package com.ciwrl.papergram.ui.settings.sub

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ciwrl.papergram.R

class ThemeSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.theme_preferences, rootKey)

        val themePreference: ListPreference? = findPreference("selected_theme")
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val themeValue = (newValue as String).toInt()
                AppCompatDelegate.setDefaultNightMode(themeValue)
                true
            }
    }
}