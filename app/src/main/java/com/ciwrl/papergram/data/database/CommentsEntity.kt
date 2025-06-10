package com.ciwrl.papergram.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val paperId: String,
    val text: String,
    val timestamp: Long,
    val userName: String
)