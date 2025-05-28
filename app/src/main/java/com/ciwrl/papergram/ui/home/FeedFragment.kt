package com.ciwrl.papergram.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// import android.widget.ProgressBar // Per dopo
// import android.widget.TextView // Per dopo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.ui.adapter.PaperAdapter

class FeedFragment : Fragment() {

    private lateinit var viewPagerFeed: ViewPager2
    private lateinit var paperAdapter: PaperAdapter
    private lateinit var homeViewModel: HomeViewModel

    private var paperList: MutableList<Paper> = mutableListOf()

    // private lateinit var progressBar: ProgressBar
    // private lateinit var errorTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        viewPagerFeed = view.findViewById(R.id.viewPagerFeed)
        // progressBar = view.findViewById(R.id.progressBar)
        // errorTextView = view.findViewById(R.id.errorTextView)

        paperAdapter = PaperAdapter(paperList)
        viewPagerFeed.adapter = paperAdapter
        viewPagerFeed.orientation = ViewPager2.ORIENTATION_VERTICAL

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        setupObservers()
    }

    private fun setupObservers() {
        homeViewModel.papers.observe(viewLifecycleOwner) { papers ->
            paperList.clear()
            paperList.addAll(papers)
            paperAdapter.notifyDataSetChanged()
            Log.d("FeedFragment", "Paper list updated, ${papers.size} papers received.")
        }

        homeViewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                ApiStatus.LOADING -> {
                    // progressBar.visibility = View.VISIBLE
                    // errorTextView.visibility = View.GONE
                    Log.d("FeedFragment", "API Status: LOADING")
                }
                ApiStatus.ERROR -> {
                    // progressBar.visibility = View.GONE
                    // errorTextView.visibility = View.VISIBLE
                    // errorTextView.text = homeViewModel.errorMessage.value ?: "Unknown error"
                    Log.e("FeedFragment", "API Status: ERROR - ${homeViewModel.errorMessage.value}")
                }
                ApiStatus.DONE -> {
                    // progressBar.visibility = View.GONE
                    // errorTextView.visibility = View.GONE
                    Log.d("FeedFragment", "API Status: DONE")
                }
                null -> {
                    // Handle null status if necessary
                }
            }
        }
    }
}