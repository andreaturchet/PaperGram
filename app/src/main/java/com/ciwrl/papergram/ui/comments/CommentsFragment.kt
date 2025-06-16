package com.ciwrl.papergram.ui.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciwrl.papergram.R
import com.ciwrl.papergram.databinding.FragmentCommentsBinding
import com.ciwrl.papergram.ui.adapter.CommentsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var commentsAdapter: CommentsAdapter
    private val args: CommentsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val paperId = args.paperId
        setupRecyclerView(paperId)

        viewModel.loadCommentsForPaper(paperId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.commentThreads.collectLatest { threads ->
                commentsAdapter.submitList(threads)
            }
        }

        binding.fabAddComment.setOnClickListener {
            showAddCommentDialog(paperId, null, null)
        }
    }

    private fun setupRecyclerView(paperId: String) {
        commentsAdapter = CommentsAdapter { commentToReply ->
            val parentId = commentToReply.id.toIntOrNull()

            if (parentId != null) {
                showAddCommentDialog(paperId, parentId, commentToReply.userName)
            } else {
                Toast.makeText(requireContext(), getString(R.string.cannot_reply_to_example), Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerViewComments.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showAddCommentDialog(paperId: String, parentId: Int?, replyToUserName: String?) {
        val editText = TextInputEditText(requireContext())
        val padding = (19 * resources.displayMetrics.density).toInt()
        editText.setPadding(padding, padding, padding, padding)

        val dialogTitle = if (replyToUserName != null) "Rispondi a $replyToUserName" else "Nuovo Commento"
        val prefillText = if (replyToUserName != null) "@$replyToUserName " else ""

        editText.setText(prefillText)
        editText.setSelection(prefillText.length)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setView(editText)
            .setPositiveButton("Invia") { _, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotBlank()) {
                    viewModel.addComment(paperId, text, parentId)
                    setFragmentResult("comment_added", bundleOf("paperId" to paperId))
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}