package com.ciwrl.papergram.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.data.model.Category
import com.ciwrl.papergram.databinding.ItemCategoryBinding

data class UiCategory(
    val category: Category,
    var isSelected: Boolean
)

class CategoryAdapter(
    private val onCategoryClicked: (UiCategory) -> Unit
) : ListAdapter<UiCategory, CategoryAdapter.CategoryViewHolder>(DiffCallback) {

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uiCategory: UiCategory, onCategoryClicked: (UiCategory) -> Unit) {
            binding.categoryChip.text = uiCategory.category.name
            binding.categoryChip.isChecked = uiCategory.isSelected
            binding.categoryChip.setOnClickListener {
                onCategoryClicked(uiCategory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val uiCategory = getItem(position)
        holder.bind(uiCategory, onCategoryClicked)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UiCategory>() {
        override fun areItemsTheSame(oldItem: UiCategory, newItem: UiCategory): Boolean {
            return oldItem.category.code == newItem.category.code
        }

        override fun areContentsTheSame(oldItem: UiCategory, newItem: UiCategory): Boolean {
            return oldItem == newItem
        }
    }
}