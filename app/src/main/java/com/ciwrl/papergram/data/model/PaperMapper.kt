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
        var paperHtmlLink: String? = null
        var paperPdfLink: String? = null

        entry.links?.forEach { link ->
            if (link.rel == "alternate" && link.type == "text/html") {
                paperHtmlLink = link.href
            } else if (link.type == "application/pdf" && (link.rel == "related" || link.titleAttribute == "pdf")) {
                paperPdfLink = link.href
            }
        }

        return Paper(
            id = paperId,
            title = title,
            authors = authorsList,
            abstractText = abstract,
            displayCategories = categories,
            publishedDate = entry.publishedDate.substringBefore("T"),
            htmlLink = paperHtmlLink,
            pdfLink = paperPdfLink,
            likeCount = (20..350).random(), // probabilmente la logica di like/commenti casuale andrebbe gestita diversamente in produzione
            isLikedByUser = isLiked,
            commentCount = commentCount
        )
    }
}