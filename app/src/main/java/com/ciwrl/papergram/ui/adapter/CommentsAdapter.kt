package com.ciwrl.papergram.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.data.model.Comment
import com.ciwrl.papergram.databinding.ItemCommentThreadBinding
import com.ciwrl.papergram.databinding.ViewCommentReplyBinding
import com.ciwrl.papergram.ui.comments.CommentThread

class CommentsAdapter(
    private val onReplyClick: (Comment) -> Unit
) : ListAdapter<CommentThread, CommentsAdapter.CommentThreadViewHolder>(DiffCallback) {

    class CommentThreadViewHolder(private val binding: ItemCommentThreadBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(thread: CommentThread, onReplyClick: (Comment) -> Unit) {
            bindCommentData(binding.topLevelComment, thread.topLevelComment, onReplyClick)

            binding.repliesContainer.removeAllViews()
            val inflater = LayoutInflater.from(binding.root.context)

            thread.replies.forEach { reply ->
                val replyBinding = ViewCommentReplyBinding.inflate(inflater, binding.repliesContainer, false)
                bindCommentData(replyBinding, reply, onReplyClick)
                binding.repliesContainer.addView(replyBinding.root)
            }
        }

        private fun bindCommentData(binding: ViewCommentReplyBinding, comment: Comment, onReplyClick: (Comment) -> Unit) {
            binding.commentUserName.text = comment.userName
            binding.commentTimestamp.text = comment.timestamp
            binding.commentText.text = comment.commentText
            binding.buttonReply.setOnClickListener { onReplyClick(comment) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentThreadViewHolder {
        val binding = ItemCommentThreadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentThreadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentThreadViewHolder, position: Int) {
        val thread = getItem(position)
        holder.bind(thread, onReplyClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CommentThread>() {
        override fun areItemsTheSame(oldItem: CommentThread, newItem: CommentThread): Boolean {
            return oldItem.topLevelComment.id == newItem.topLevelComment.id
        }

        override fun areContentsTheSame(oldItem: CommentThread, newItem: CommentThread): Boolean {
            return oldItem == newItem
        }
    }
}