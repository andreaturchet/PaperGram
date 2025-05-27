package com.ciwrl.papergram

data class Paper(
    val title: String,
    val authors: List<String>,
    val abstractText: String,
    val keywords: String // O List<String>
)