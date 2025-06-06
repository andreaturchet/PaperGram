package com.ciwrl.papergram.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.databinding.FragmentFeedBinding
import com.ciwrl.papergram.ui.adapter.PaperAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        setupSwipeToRefresh()

        setFragmentResultListener("categories_saved") { _, _ ->
            homeViewModel.refreshFeed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.status.collect { status ->
                binding.swipeRefreshLayout.isRefreshing = status == ApiStatus.LOADING
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.papers.collectLatest { papers ->
                paperAdapter.submitList(papers)
            }
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.refreshFeed()
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
            },
            onLikeClick = { paper ->
                Toast.makeText(requireContext(), "Like/Unlike: ${paper.title}", Toast.LENGTH_SHORT).show()
            },
            onCommentClick = { paper ->
                val action = FeedFragmentDirections.actionNavHomeToCommentsFragment()
                findNavController().navigate(action)
            }
        )

        binding.recyclerViewFeed.adapter = paperAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewFeed)
        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val threshold = 5
                if (totalItemCount <= (lastVisibleItemPosition + threshold)) {
                    homeViewModel.loadMorePapers()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}