package com.ciwrl.papergram.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ciwrl.papergram.R
import com.ciwrl.papergram.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextUserName.setText(viewModel.getUserName())
        binding.buttonSaveSettings.setOnClickListener {
            val newName = binding.editTextUserName.text.toString().trim()
            if (newName.isNotEmpty()) {
                viewModel.saveUserName(newName)
                Toast.makeText(requireContext(), "Nome salvato!", Toast.LENGTH_SHORT).show()
            } else {
                binding.editTextUserName.error = "Il nome non può essere vuoto"
            }
        }
        setupThemeSelection()
        setupLanguageSelection()
    }

    private fun setupLanguageSelection() {
        binding.textViewLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }

    private fun setupThemeSelection() {
        when (viewModel.getTheme()) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.radioLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.radioDark.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }

        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                R.id.radio_light -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.radio_dark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            viewModel.setTheme(newTheme)
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("Italiano", "English")
        val languageCodes = arrayOf("it", "en")

        val currentLanguageCode = viewModel.getLanguage()
        val checkedItem = languageCodes.indexOf(currentLanguageCode).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleziona Lingua")
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]

                viewModel.setLanguage(selectedLanguageCode)

                dialog.dismiss()

                requireActivity().recreate()
            }
            .setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}