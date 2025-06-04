package com.ciwrl.papergram.data.model
data class Category(
    val name: String,
    val code: String
)

data class MainCategory(
    val name: String,
    val emoji: String,
    val subCategories: List<Category>
)