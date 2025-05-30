package com.ciwrl.papergram.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.data.database.SavedPaperEntity
import com.ciwrl.papergram.databinding.ItemPaperSavedBinding

class SavedPaperAdapter(
    private val onPaperClick: (SavedPaperEntity) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<SavedPaperEntity, SavedPaperAdapter.SavedPaperViewHolder>(DiffCallback) {

    class SavedPaperViewHolder(private val binding: ItemPaperSavedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            paperEntity: SavedPaperEntity,
            onPaperClick: (SavedPaperEntity) -> Unit,
            onDeleteClick: (String) -> Unit
        ) {
            binding.textViewTitle.text = paperEntity.title
            binding.textViewAuthors.text = paperEntity.authors
            binding.textViewKeywords.text = paperEntity.keywords
            binding.root.setOnClickListener { onPaperClick(paperEntity) }
            binding.imageButtonDelete.setOnClickListener { onDeleteClick(paperEntity.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPaperViewHolder {
        val binding = ItemPaperSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedPaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedPaperViewHolder, position: Int) {
        val paper = getItem(position)
        holder.bind(paper, onPaperClick, onDeleteClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SavedPaperEntity>() {
        override fun areItemsTheSame(oldItem: SavedPaperEntity, newItem: SavedPaperEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedPaperEntity, newItem: SavedPaperEntity): Boolean {
            return oldItem == newItem
        }
    }
}