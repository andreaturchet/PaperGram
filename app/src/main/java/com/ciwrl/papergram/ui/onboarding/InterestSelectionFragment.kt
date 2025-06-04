package com.ciwrl.papergram.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.MainActivity
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.ui.adapter.CategoryAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch

class InterestSelectionFragment : Fragment(R.layout.fragment_interest_selection) {

    private val viewModel: InterestSelectionViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiCategories.collect { categories ->
                    categoryAdapter.submitList(categories)
                }
            }
        }

        val finishButton = view.findViewById<View>(R.id.button_finish)
        val skipButton = view.findViewById<View>(R.id.button_skip)

        finishButton.setOnClickListener {
            viewModel.saveSelectedCategories()
            goToMainActivity()
        }

        skipButton.setOnClickListener {
            goToMainActivity()
        }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_interests)
        categoryAdapter = CategoryAdapter { clickedCategory ->
            viewModel.toggleCategorySelection(clickedCategory)
        }

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.justifyContent = JustifyContent.SPACE_AROUND

        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = layoutManager
    }

    private fun goToMainActivity() {
        UserPreferences.setOnboardingCompleted(requireContext(), true)
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}