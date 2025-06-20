package com.ciwrl.papergram.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.ciwrl.papergram.databinding.FragmentFeedBinding
import com.ciwrl.papergram.ui.adapter.PaperAdapter
import kotlinx.coroutines.launch

/**
 * A [Fragment] that displays the main feed of scientific papers.
 *
 * This fragment is responsible for:
 * - Observing data from [HomeViewModel] and submitting it to the [PaperAdapter].
 * - Setting up the [RecyclerView] with a [PagerSnapHelper] for a card-style feed.
 * - Handling user interactions like clicking on a paper, saving, liking, or commenting.
 * - Implementing pull-to-refresh functionality.
 */

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireActivity().application)
    }
    private lateinit var paperAdapter: PaperAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        setupSwipeToRefresh()
        setupReloadButton()

        setFragmentResultListener("categories_saved") { _, _ ->
            homeViewModel.refreshFeed()
        }
        setFragmentResultListener("comment_added") { _, bundle ->
            val paperId = bundle.getString("paperId")
            if (paperId != null) {
                homeViewModel.refreshCommentCountForPaper(paperId)
            }
        }
        setFragmentResultListener("feed_mode_changed") { _, _ ->
            homeViewModel.refreshFeed()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.papers.collect { papers ->
                    paperAdapter.submitList(papers)
                    binding.recyclerViewFeed.visibility = if (papers.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.status.collect { status ->
                    val isLoading = status == ApiStatus.LOADING
                    val showError = status == ApiStatus.ERROR

                    // Show shimmer only on initial load
                    if (isLoading && binding.recyclerViewFeed.adapter?.itemCount == 0) {
                        binding.shimmerViewContainer.startShimmer()
                        binding.shimmerViewContainer.visibility = View.VISIBLE
                        binding.recyclerViewFeed.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                    } else {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                    }

                    binding.swipeRefreshLayout.isRefreshing = isLoading

                    // Show error only if the list is empty
                    if (showError && binding.recyclerViewFeed.adapter?.itemCount == 0) {
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.recyclerViewFeed.visibility = View.GONE
                    } else {
                        binding.errorLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        paperAdapter = PaperAdapter(
            onPaperClick = { paper ->
                val action = FeedFragmentDirections.actionNavHomeToPaperDetailFragment(paper)
                findNavController().navigate(action)
            },
            onSaveClick = { paper, isSaved -> homeViewModel.toggleSaveState(paper, isSaved) },
            onLikeClick = { paper -> homeViewModel.toggleLikeState(paper) },
            onCommentClick = { paper ->
                val action = FeedFragmentDirections.actionNavHomeToCommentsFragment(paper.id)
                findNavController().navigate(action)
            }
        )

        binding.recyclerViewFeed.adapter = paperAdapter
        PagerSnapHelper().attachToRecyclerView(binding.recyclerViewFeed)

        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Trigger load more when the user is near the end of the list
                if (totalItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 5) {
                    homeViewModel.loadMorePapers()
                }
            }
        })
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.refreshFeed()
        }
    }

    private fun setupReloadButton() {
        binding.buttonReload.setOnClickListener {
            homeViewModel.refreshFeed()
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.feed_menu, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as? SearchView
                searchView?.queryHint = getString(R.string.search_hint)
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            if (it.isNotBlank()) {
                                searchView.clearFocus()
                                // La ricerca non è implementata in questo ViewModel, andrebbe aggiunta una funzione apposita
                            }
                        }
                        return true
                    }
                    override fun onQueryTextChange(newText: String?): Boolean { return true }
                })
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean { return false }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
