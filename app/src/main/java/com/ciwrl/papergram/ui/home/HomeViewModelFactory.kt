package com.ciwrl.papergram.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ciwrl.papergram.data.database.AppDatabase

/**
 * Factory for creating an instance of [HomeViewModel] with its dependencies.
 */
class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                application = application,
                savedPaperDao = database.savedPaperDao(),
                userLikeDao = database.userLikeDao(),
                commentDao = database.commentDao()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}