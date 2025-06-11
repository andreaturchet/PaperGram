package com.ciwrl.papergram.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.database.AppDatabase
import com.ciwrl.papergram.data.database.SavedPaperEntity
import com.ciwrl.papergram.data.model.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages UI state and logic for the [PaperDetailFragment].
 *
 * This ViewModel is responsible for:
 * - Checking if a paper is saved in the local database.
 * - Toggling the save state of a paper (saving or deleting it from the database).
 *
 * @param application The application instance, required for accessing the context.
 */

class PaperDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val savedPaperDao = AppDatabase.getDatabase(application).savedPaperDao()

    fun isPaperSaved(paperId: String) = savedPaperDao.isPaperSaved(paperId)

    fun toggleSaveState(paper: Paper, isCurrentlySaved: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isCurrentlySaved) {
                    savedPaperDao.deletePaperById(paper.id)
                } else {
                    val entity = SavedPaperEntity(
                        id = paper.id,
                        title = paper.title,
                        authors = paper.authors.joinToString(", "),
                        abstractText = paper.abstractText,
                        keywords = paper.displayCategories.joinToString(", ") { it.name },
                        publishedDate = paper.publishedDate,
                        htmlLink = paper.htmlLink,
                        pdfLink = paper.pdfLink
                    )
                    savedPaperDao.insertPaper(entity)
                }
            }
        }
    }
}