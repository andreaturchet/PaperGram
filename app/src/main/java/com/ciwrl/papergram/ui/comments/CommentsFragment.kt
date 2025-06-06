package com.ciwrl.papergram.ui.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciwrl.papergram.databinding.FragmentCommentsBinding
import com.ciwrl.papergram.ui.adapter.CommentsAdapter

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsAdapter.submitList(comments)
        }

        // Care i commenti finti
        viewModel.loadCommentsForPaper("fake-id")
    }

    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter()
        binding.recyclerViewComments.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}