package com.ciwrl.papergram.data.model

import com.ciwrl.papergram.data.model.api.ArxivEntry

/**
 * A helper class responsible for mapping API data models to UI data models.
 * This decouples the ViewModel from the specific structure of the API response.
 */
class PaperMapper(private val categoryMap: Map<String, String>) {

    fun mapArxivEntryToPaper(entry: ArxivEntry, isLiked: Boolean, commentCount: Int): Paper {
        val title = entry.title.trim().replace("\\s+".toRegex(), " ")
        val abstract = entry.summary.trim().replace("\\s+".toRegex(), " ")
        val authorsList = entry.authors?.map { it.name } ?: listOf("Unknown Author")
        val categories = entry.categories?.map { category ->
            if (categoryMap.containsKey(category.term)) {
                DisplayCategory(name = categoryMap[category.term]!!, isTranslated = true)
            } else {
                DisplayCategory(name = category.term, isTranslated = false)
            }
        } ?: emptyList()

        val paperId = entry.id.substringAfterLast('/').substringBeforeLast('v')
        val paperHtmlLink = "https://arxiv.org/abs/$paperId"
        val paperPdfLink = "https://arxiv.org/pdf/$paperId"

        return Paper(
            id = paperId,
            title = title,
            authors = authorsList,
            abstractText = abstract,
            displayCategories = categories,
            publishedDate = entry.publishedDate.substringBefore("T"),
            htmlLink = paperHtmlLink,
            pdfLink = paperPdfLink,
            likeCount = (20..350).random(),
            isLikedByUser = isLiked,
            commentCount = commentCount
        )
    }
}