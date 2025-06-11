package com.ciwrl.papergram.ui.home

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ciwrl.papergram.data.database.CommentDao
import com.ciwrl.papergram.data.database.SavedPaperDao
import com.ciwrl.papergram.data.database.UserLikeDao
import com.ciwrl.papergram.data.model.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    // Mock delle dipendenze
    private lateinit var mockSavedPaperDao: SavedPaperDao
    private lateinit var mockUserLikeDao: UserLikeDao
    private lateinit var mockCommentDao: CommentDao
    private lateinit var mockApplication: Application

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockSavedPaperDao = mock()
        mockUserLikeDao = mock()
        mockCommentDao = mock()
        mockApplication = mock()

        viewModel = HomeViewModel(mockApplication, mockSavedPaperDao, mockUserLikeDao, mockCommentDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleSaveState should update paper saved status and call dao`() = runTest {
        // Arrange: Prepariamo lo stato iniziale del test
        val fakePaper = Paper("1", "Test Paper", emptyList(), "", emptyList(), "", null, null, 0, false, 0)
        val uiPaper = UiPaper(paper = fakePaper, isSaved = false)

        // Imposta lo stato iniziale del Flow nel ViewModel
        (viewModel.papers as MutableStateFlow).value = listOf(uiPaper)

        // Act: Esegui l'azione di salvataggio
        viewModel.toggleSaveState(fakePaper, isCurrentlySaved = false)

        // Assert: Verificai risultati
        val updatedUiPaper = viewModel.papers.value.first()
        assertTrue("The paper's isSaved flag should be true after saving.", updatedUiPaper.isSaved)

        // Verifica anche che il metodo corretto del DAO sia stato chiamato
        verify(mockSavedPaperDao).insertPaper(any())


        // Act 2: Esegui l'azione di rimozione
        viewModel.toggleSaveState(fakePaper, isCurrentlySaved = true)

        // Assert 2: Verifica i nuovi risultati
        val finalUiPaper = viewModel.papers.value.first()
        assertFalse("The paper's isSaved flag should be false after removing.", finalUiPaper.isSaved)
        verify(mockSavedPaperDao).deletePaperById(fakePaper.id)
    }
}