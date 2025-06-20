package com.ciwrl.papergram.ui.settings.sub

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ciwrl.papergram.R

class FeedSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.feed_preferences, rootKey)

        val feedModePreference: ListPreference? = findPreference("feed_mode")
        feedModePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                // When the preference changes, send a signal that the FeedFragment can listen to.
                setFragmentResult("feed_mode_changed", bundleOf())
                true
            }
    }
}