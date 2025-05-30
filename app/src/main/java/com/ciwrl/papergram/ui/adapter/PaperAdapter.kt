package com.ciwrl.papergram.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.databinding.ItemPaperCardBinding
import com.ciwrl.papergram.ui.home.UiPaper

class PaperAdapter(
    private val onPaperClick: (Paper) -> Unit,
    private val onSaveClick: (Paper, Boolean) -> Unit
) : ListAdapter<UiPaper, PaperAdapter.PaperViewHolder>(DiffCallback) {

    class PaperViewHolder(private val binding: ItemPaperCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uiPaper: UiPaper, onPaperClick: (Paper) -> Unit, onSaveClick: (Paper, Boolean) -> Unit) {
            val paper = uiPaper.paper
            binding.textViewTitle.text = paper.title
            binding.textViewAuthors.text = paper.authors.joinToString(", ")
            binding.textViewAbstract.text = paper.abstractText
            binding.root.setOnClickListener { onPaperClick(paper) }
            binding.imageButtonSave.setOnClickListener { onSaveClick(paper, uiPaper.isSaved) }

            if (uiPaper.isSaved) {
                binding.imageButtonSave.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                binding.imageButtonSave.setImageResource(R.drawable.ic_bookmark_border_24dp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaperViewHolder {
        val binding = ItemPaperCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaperViewHolder, position: Int) {
        val uiPaper = getItem(position)
        holder.bind(uiPaper, onPaperClick, onSaveClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UiPaper>() {
        override fun areItemsTheSame(oldItem: UiPaper, newItem: UiPaper): Boolean {
            return oldItem.paper.id == newItem.paper.id
        }
        override fun areContentsTheSame(oldItem: UiPaper, newItem: UiPaper): Boolean {
            return oldItem == newItem
        }
    }
}