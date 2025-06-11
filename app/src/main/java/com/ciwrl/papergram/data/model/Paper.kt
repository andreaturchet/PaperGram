package com.ciwrl.papergram.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a single scientific paper entity as displayed in the UI.
 *
 * This is the primary data model for the application, containing all necessary
 * information for display in feeds and detail views. It is designed to be parcelable
 * to be passed between fragments.
 *
 * @property id The unique identifier for the paper, derived from the ArXiv ID.
 * @property title The title of the paper.
 * @property authors A list of author names.
 * @property abstractText The summary or abstract of the paper.
 * @property displayCategories A list of categories associated with the paper.
 * @property publishedDate The publication date of the paper.
 * @property htmlLink An optional URL to the paper's HTML abstract page.
 * @property pdfLink An optional URL to the paper's PDF file.
 * @property likeCount The number of likes the paper has received.
 * @property isLikedByUser A boolean indicating if the current user has liked the paper.
 * @property commentCount The total number of comments on the paper.
 */

@Parcelize
data class Paper(
    val id: String,
    val title: String,
    val authors: List<String>,
    val abstractText: String,
    val displayCategories: List<DisplayCategory>,
    val publishedDate: String,
    val htmlLink: String?,
    val pdfLink: String?,
    var likeCount: Int,
    var isLikedByUser: Boolean,
    val commentCount: Int

): Parcelable