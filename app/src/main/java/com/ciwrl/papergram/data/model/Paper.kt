package com.ciwrl.papergram.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Paper(
    val id: String,
    val title: String,
    val authors: List<String>,
    val abstractText: String,
    val keywords: String,
    val publishedDate: String,
    val htmlLink: String?,
    val pdfLink: String?
): Parcelable