package com.ciwrl.papergram.data.model

data class Comment(
    val id: String,
    val userName: String,
    val userAvatarUrl: String, // Per ora no, ma in futuro forse
    val timestamp: String,
    val commentText: String
)