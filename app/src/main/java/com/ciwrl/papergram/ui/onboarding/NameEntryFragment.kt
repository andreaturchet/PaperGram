package com.ciwrl.papergram.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.UserPreferences
import com.google.android.material.textfield.TextInputEditText

class NameEntryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_name_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText = view.findViewById<TextInputEditText>(R.id.editText_name)
        val continueButton = view.findViewById<Button>(R.id.button_continue)

        continueButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            if (name.isNotEmpty()) {
                UserPreferences.saveUserName(requireContext(), name)
                findNavController().navigate(R.id.action_nameEntryFragment_to_interestSelectionFragment)
            } else {
                nameEditText.error = "Per favore, inserisci un nome"
            }
        }
    }
}