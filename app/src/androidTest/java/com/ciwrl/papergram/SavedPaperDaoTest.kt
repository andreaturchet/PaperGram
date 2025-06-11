package com.ciwrl.papergram.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test for the [SavedPaperDao].
 * This class tests the database operations for saving and retrieving papers.
 */
@RunWith(AndroidJUnit4::class)
class SavedPaperDaoTest {

    private lateinit var savedPaperDao: SavedPaperDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usiamo un database in-memory: i dati vengono cancellati quando il processo termina.
        // Questo garantisce che ogni test sia isolato e non influenzi gli altri.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        savedPaperDao = db.savedPaperDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertPaperAndGetAll_retrievesSamePaper() = runTest {
        // Arrange: Crea un'entità di test
        val paper = SavedPaperEntity(
            id = "12345",
            title = "A Test Paper",
            authors = "John Doe",
            abstractText = "This is an abstract.",
            keywords = "testing, android",
            publishedDate = "2025-06-11",
            htmlLink = null,
            pdfLink = null
        )

        // Act: Inserisci il paper nel database
        savedPaperDao.insertPaper(paper)

        // Leggi tutti i paper salvati. Usiamo .first() perché vogliamo solo il primo valore emesso dal Flow.
        val allPapers = savedPaperDao.getAllSavedPapers().first()

        // Assert: Verifica che i dati letti corrispondano a quelli inseriti
        assertTrue("The list of saved papers should not be empty.", allPapers.isNotEmpty())
        assertEquals(paper.id, allPapers[0].id)
        assertEquals(paper.title, allPapers[0].title)
    }
}