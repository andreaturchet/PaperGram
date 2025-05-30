package com.ciwrl.papergram.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ciwrl.papergram.databinding.FragmentFeedBinding
import com.ciwrl.papergram.ui.adapter.PaperAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.PagerSnapHelper

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var paperAdapter: PaperAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.papers.collectLatest { papers ->
                paperAdapter.submitList(papers)
            }
        }
    }

    private fun setupRecyclerView() {
        paperAdapter = PaperAdapter(
            onPaperClick = { paper ->
                val action = FeedFragmentDirections.actionNavHomeToPaperDetailFragment(paper)
                findNavController().navigate(action)
            },
            onSaveClick = { paper, isSaved ->
                homeViewModel.toggleSaveState(paper, isSaved)
            }
        )
        binding.recyclerViewFeed.adapter = paperAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewFeed)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}