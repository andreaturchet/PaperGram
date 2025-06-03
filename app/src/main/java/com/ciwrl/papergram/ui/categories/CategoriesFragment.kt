
package com.ciwrl.papergram.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ciwrl.papergram.databinding.FragmentCategoriesBinding
import com.ciwrl.papergram.ui.adapter.CategoryAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSaveButton()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiCategories.collect { categories ->
                    categoryAdapter.submitList(categories)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { clickedCategory ->
            viewModel.toggleCategorySelection(clickedCategory)
        }

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.justifyContent = JustifyContent.CENTER

        binding.recyclerViewCategories.adapter = categoryAdapter
        binding.recyclerViewCategories.layoutManager = layoutManager
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (viewModel.getSelectedCodes().isEmpty()) {
                Toast.makeText(requireContext(), "Seleziona almeno una categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveCategories()
            Toast.makeText(requireContext(), "Preferenze salvate!", Toast.LENGTH_SHORT).show()

            setFragmentResult("categories_saved", bundleOf())
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewCategories.adapter = null
        _binding = null
    }
}