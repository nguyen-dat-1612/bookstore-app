package com.dat.bookstore_app.presentation.common.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemCategoryBinding
import com.dat.bookstore_app.domain.models.CategoryUiModel
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder

class CategoryAdapter(
    private val onCategorySelected: (CategoryUiModel) -> Unit
) : BaseListAdapter<CategoryUiModel, ItemCategoryBinding, CategoryAdapter.CategoryViewHolder>(
    ItemCategoryBinding::inflate,
    CategoryDiffCallback()
) {

    override fun createViewHolder(binding: ItemCategoryBinding): CategoryViewHolder {
        return CategoryViewHolder(binding)
    }

    inner class CategoryViewHolder(
        binding: ItemCategoryBinding
    ) : BaseViewHolder<CategoryUiModel, ItemCategoryBinding>(binding) {

        override fun bind(item: CategoryUiModel) = with(binding) {
            tvCategoryName.text = item.name
            container.setBackgroundResource(
                if (item.isSelected) com.dat.bookstore_app.R.drawable.bg_category_selected
                else com.dat.bookstore_app.R.drawable.bg_category_unselected
            )
            imgTick.isVisible = item.isSelected

            container.setOnClickListener {
                onCategorySelected(item)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryUiModel>() {
        override fun areItemsTheSame(oldItem: CategoryUiModel, newItem: CategoryUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CategoryUiModel,
            newItem: CategoryUiModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}