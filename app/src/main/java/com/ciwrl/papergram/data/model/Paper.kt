package com.ciwrl.papergram.data.model

data class Paper(
    val title: String,
    val authors: List<String>,
    val abstractText: String,
    val keywords: String // O List<String>
)