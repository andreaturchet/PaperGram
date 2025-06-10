package com.ciwrl.papergram.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val paperId: String,
    val text: String,
    val timestamp: Long,
    val userName: String,

    @ColumnInfo(name = "parent_id")
    val parentId: Int? = null
)