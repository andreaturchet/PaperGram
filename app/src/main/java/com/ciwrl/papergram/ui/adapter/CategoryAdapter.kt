package com.ciwrl.papergram.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.data.model.MainCategory
import com.ciwrl.papergram.databinding.ItemCategoryBinding

data class UiMainCategory(
    val mainCategory: MainCategory,
    var isSelected: Boolean
)

class CategoryAdapter(
    private val onCategoryClicked: (UiMainCategory) -> Unit
) : ListAdapter<UiMainCategory, CategoryAdapter.CategoryViewHolder>(DiffCallback) {

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(uiMainCategory: UiMainCategory, onCategoryClicked: (UiMainCategory) -> Unit) {
            val context = binding.root.context
            val resources = context.resources
            val resourceName = "cat_" + uiMainCategory.mainCategory.name.replace(" ", "_").lowercase()
            val resourceId = resources.getIdentifier(resourceName, "string", context.packageName)
            val localizedCategoryName = if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                uiMainCategory.mainCategory.name // Fallback
            }

            binding.categoryChip.text = "${uiMainCategory.mainCategory.emoji} $localizedCategoryName"

            binding.categoryChip.isChecked = uiMainCategory.isSelected
            binding.categoryChip.setOnClickListener {
                onCategoryClicked(uiMainCategory)
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

    companion object DiffCallback : DiffUtil.ItemCallback<UiMainCategory>() {
        override fun areItemsTheSame(oldItem: UiMainCategory, newItem: UiMainCategory): Boolean {
            return oldItem.mainCategory.name == newItem.mainCategory.name
        }

        override fun areContentsTheSame(oldItem: UiMainCategory, newItem: UiMainCategory): Boolean {
            return oldItem.isSelected == newItem.isSelected
        }
    }
}