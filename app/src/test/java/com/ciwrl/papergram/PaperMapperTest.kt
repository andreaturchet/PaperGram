package com.ciwrl.papergram.data.model

import com.ciwrl.papergram.data.model.api.ArxivAuthor
import com.ciwrl.papergram.data.model.api.ArxivCategory
import com.ciwrl.papergram.data.model.api.ArxivEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PaperMapperTest {

    private lateinit var paperMapper: PaperMapper

    @Before
    fun setUp() {
        // Arrange: Crea una mappa di categorie finta per il test
        val categoryMap = mapOf("cs.AI" to "Artificial Intelligence")
        paperMapper = PaperMapper(categoryMap)
    }

    @Test
    fun `mapArxivEntryToPaper should map all fields correctly`() {
        // Arrange: Crea un oggetto ArxivEntry finto con dati di input
        val fakeEntry = ArxivEntry(
            id = "http://arxiv.org/abs/2401.12345v1",
            title = "  Test Title with extra spaces  ",
            summary = "This is the abstract.",
            authors = listOf(ArxivAuthor("John Doe")),
            categories = listOf(ArxivCategory("cs.AI"), ArxivCategory("stat.ML")),
            publishedDate = "2025-06-11T12:00:00Z"
        )

        // Act: Esegui la funzione di mapping
        val paper = paperMapper.mapArxivEntryToPaper(fakeEntry, isLiked = false, commentCount = 5)

        // Assert: Verifica che ogni campo dell'oggetto Paper sia stato mappato correttamente
        assertEquals("2401.12345", paper.id)
        assertEquals("Test Title with extra spaces", paper.title)
        assertEquals("This is the abstract.", paper.abstractText)
        assertEquals("John Doe", paper.authors.first())
        assertEquals(5, paper.commentCount)
        assertEquals("2025-06-11", paper.publishedDate)

        // Verifica la logica delle categorie
        assertEquals(2, paper.displayCategories.size)
        assertEquals("Artificial Intelligence", paper.displayCategories.first().name)
        assertTrue(paper.displayCategories.first().isTranslated)
        assertEquals("stat.ML", paper.displayCategories.last().name)
        assertEquals(false, paper.displayCategories.last().isTranslated)
    }
}