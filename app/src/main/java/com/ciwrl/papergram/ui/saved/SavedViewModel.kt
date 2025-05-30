package com.ciwrl.papergram.ui.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedViewModel(application: Application) : AndroidViewModel(application) {

    private val savedPaperDao = AppDatabase.getDatabase(application).savedPaperDao()

    val savedPapers = savedPaperDao.getAllSavedPapers()

    fun deletePaper(paperId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                savedPaperDao.deletePaperById(paperId)
            }
        }
    }
}