package com.ciwrl.papergram.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.ciwrl.papergram.databinding.FragmentFeedBinding
import com.ciwrl.papergram.ui.adapter.PaperAdapter
import kotlinx.coroutines.flow.combine
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

        // Setup delle varie parti del Fragment
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

        observeViewModel()
    }

    private fun setupReloadButton() {
        val reloadButton = view?.findViewById<Button>(R.id.button_reload)
        reloadButton?.setOnClickListener {
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
                                homeViewModel.searchPapersByTitle(it)
                            }
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })

                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return true // Permetti l'espansione
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        homeViewModel.refreshFeed()
                        return true // Permetti la chiusura
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.status.combine(homeViewModel.papers) { status, papers ->
                UiState(status, papers)
            }.collect { state ->
                val (status, papers) = state
                paperAdapter.submitList(papers)

                val errorLayout = view?.findViewById<LinearLayout>(R.id.error_layout)

                val showShimmer = status == ApiStatus.LOADING && papers.isEmpty()
                binding.shimmerViewContainer.visibility = if (showShimmer) View.VISIBLE else View.GONE
                if (showShimmer) binding.shimmerViewContainer.startShimmer() else binding.shimmerViewContainer.stopShimmer()

                binding.recyclerViewFeed.visibility = if (status == ApiStatus.DONE || papers.isNotEmpty()) View.VISIBLE else View.GONE

                val showError = status == ApiStatus.ERROR && papers.isEmpty()
                errorLayout?.visibility = if (showError) View.VISIBLE else View.GONE

                binding.swipeRefreshLayout.isRefreshing = status == ApiStatus.LOADING && papers.isNotEmpty()
            }
        }
    }

    data class UiState(val status: ApiStatus, val papers: List<UiPaper>)

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
                homeViewModel.toggleLikeState(paper)
            },
            onCommentClick = { paper ->
                val action = FeedFragmentDirections.actionNavHomeToCommentsFragment(paper.id)
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

                if (!homeViewModel.isCurrentlyLoading && !homeViewModel.hasLoadedAllItems && totalItemCount <= (lastVisibleItemPosition + threshold)) {
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