package com.ciwrl.papergram.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.UserPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
/**
 * The main settings screen, acting as a root for all other setting sub-screens.
 * It extends PreferenceFragmentCompat to automatically build the UI from an XML resource.
 */

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("account_settings")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_account)
            true
        }

        findPreference<Preference>("feed_settings")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_feed)
            true
        }

        findPreference<Preference>("theme_settings")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_theme)
            true
        }

        findPreference<Preference>("language_preference")?.setOnPreferenceClickListener {
            showLanguageSelectionDialog()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        updateSummaries()
    }

    private fun updateSummaries() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Update Account summary
        val accountPreference: Preference? = findPreference("account_settings")
        val userName = sharedPreferences.getString("user_name", "PaperGram User")
        accountPreference?.summary = userName

        // Update Feed summary
        val feedPreference: Preference? = findPreference("feed_settings")
        val feedModeValue = sharedPreferences.getString("feed_mode", "chronological")
        feedPreference?.summary = if (feedModeValue == "random") getString(R.string.settings_feed_mode_random) else getString(R.string.settings_feed_mode_chronological)

        // Update Theme summary
        val themePreference: Preference? = findPreference("theme_settings")
        val themeValue = sharedPreferences.getString("selected_theme", "-1")
        val themeEntries = resources.getStringArray(R.array.theme_entries)
        val themeValues = resources.getStringArray(R.array.theme_values)
        val themeIndex = themeValues.indexOf(themeValue)
        if (themeIndex >= 0) {
            themePreference?.summary = themeEntries[themeIndex]
        }

        // Update Language summary
        val languagePreference: Preference? = findPreference("language_preference")
        val currentLanguageCode = UserPreferences.getLanguage(requireContext())
        languagePreference?.summary = if (currentLanguageCode == "it") "Italiano" else "English"
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("Italiano", "English")
        val languageCodes = arrayOf("it", "en")
        val currentLanguageCode = UserPreferences.getLanguage(requireContext())
        val checkedItem = languageCodes.indexOf(currentLanguageCode).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.select_lang))
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]
                UserPreferences.saveLanguage(requireContext(), selectedLanguageCode)
                val appLocale = LocaleListCompat.forLanguageTags(selectedLanguageCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}