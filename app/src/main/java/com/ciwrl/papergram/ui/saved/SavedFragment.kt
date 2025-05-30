package com.ciwrl.papergram.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.databinding.FragmentSavedBinding
import com.ciwrl.papergram.ui.adapter.SavedPaperAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val savedViewModel: SavedViewModel by viewModels()
    private lateinit var savedPaperAdapter: SavedPaperAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            savedViewModel.savedPapers.collectLatest { savedPapers ->
                if (savedPapers.isEmpty()) {
                    binding.savedRecyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.savedRecyclerView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    savedPaperAdapter.submitList(savedPapers)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        savedPaperAdapter = SavedPaperAdapter(
            onPaperClick = { entity ->
                val paper = Paper(
                    id = entity.id,
                    title = entity.title,
                    authors = entity.authors.split(", "),
                    abstractText = entity.abstractText,
                    keywords = entity.keywords,
                    publishedDate = entity.publishedDate,
                    htmlLink = entity.htmlLink,
                    pdfLink = entity.pdfLink,
                    imageUrl = entity.imageUrl
                )
                val action = SavedFragmentDirections.actionNavSavedToPaperDetailFragment(paper)
                findNavController().navigate(action)
            },
            onDeleteClick = { paperId ->
                savedViewModel.deletePaper(paperId)
            }
        )
        binding.savedRecyclerView.adapter = savedPaperAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}