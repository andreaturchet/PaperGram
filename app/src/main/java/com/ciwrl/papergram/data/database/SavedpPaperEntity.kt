package com.ciwrl.papergram.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_papers")
data class SavedPaperEntity(
    @PrimaryKey val id: String,
    val title: String,
    val authors: String,
    val abstractText: String,
    val keywords: String,
    val publishedDate: String,
    val htmlLink: String?,
    val pdfLink: String?,
    val imageUrl: String?
)