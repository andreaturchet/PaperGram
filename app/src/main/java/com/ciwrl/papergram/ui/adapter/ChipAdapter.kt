package com.ciwrl.papergram.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.databinding.ItemChipBinding
import com.google.android.material.chip.Chip

class ChipAdapter : ListAdapter<String, ChipAdapter.ChipViewHolder>(DiffCallback) {

    class ChipViewHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryName: String) {
            (binding.root as? Chip)?.text = categoryName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val categoryName = getItem(position)
        holder.bind(categoryName)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}