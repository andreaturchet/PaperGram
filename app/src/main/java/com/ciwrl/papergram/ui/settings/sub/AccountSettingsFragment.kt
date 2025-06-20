package com.ciwrl.papergram.ui.settings.sub

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ciwrl.papergram.R

class AccountSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)

        val userNamePreference: EditTextPreference? = findPreference("user_name")

        userNamePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue as? String
                true
            }
    }

    override fun onResume() {
        super.onResume()
        val userNamePreference: EditTextPreference? = findPreference("user_name")
        userNamePreference?.summary = userNamePreference?.text
    }
}