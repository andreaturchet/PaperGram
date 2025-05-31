package com.ciwrl.papergram.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.database.AppDatabase
import com.ciwrl.papergram.data.database.SavedPaperEntity
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.data.model.api.ArxivEntry
import com.ciwrl.papergram.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// Stato per la UI
data class UiPaper(val paper: Paper, val isSaved: Boolean)
enum class ApiStatus { LOADING, ERROR, DONE }

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableStateFlow(ApiStatus.LOADING)
    val status: StateFlow<ApiStatus> = _status

    private val savedPaperDao = AppDatabase.getDatabase(application).savedPaperDao()
    private val arxivApi = RetrofitInstance.arxivApiService

    val papers: StateFlow<List<UiPaper>> = flow {
        _status.value = ApiStatus.LOADING
        try {
            val arxivResponse = RetrofitInstance.arxivApiService.getRecentPapers(
                searchQuery = "cat:cs.AI",
                maxResults = 20,
                start = 0
            )

            if (arxivResponse.isSuccessful && arxivResponse.body() != null) {
                val papersFromArxiv = arxivResponse.body()?.entries?.map { mapArxivEntryToPaper(it) } ?: emptyList()

                emit(papersFromArxiv)
                _status.value = ApiStatus.DONE
            } else {
                _status.value = ApiStatus.ERROR
                Log.e("HomeViewModel", "ArXiv API request failed: ${arxivResponse.message()}")
                emit(emptyList<Paper>())
            }
        } catch (e: Exception) {
            _status.value = ApiStatus.ERROR
            Log.e("HomeViewModel", "Failed to fetch data", e)
            emit(emptyList<Paper>())
        }
    }.combine(savedPaperDao.getAllSavedPapers()) { apiPapers, savedPapers ->
        val savedIds = savedPapers.map { it.id }.toSet()
        apiPapers.map { paper ->
            UiPaper(paper = paper, isSaved = savedIds.contains(paper.id))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun toggleSaveState(paper: Paper, isCurrentlySaved: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isCurrentlySaved) {
                    savedPaperDao.deletePaperById(paper.id)
                } else {
                    val entity = SavedPaperEntity(
                        id = paper.id, title = paper.title, authors = paper.authors.joinToString(", "),
                        abstractText = paper.abstractText, keywords = paper.keywords,
                        publishedDate = paper.publishedDate, htmlLink = paper.htmlLink, pdfLink = paper.pdfLink,
                        imageUrl = paper.imageUrl
                    )
                    savedPaperDao.insertPaper(entity)
                }
            }
        }
    }

    private fun mapArxivEntryToPaper(entry: ArxivEntry): Paper {
        val title = entry.title.trim().replace("\\s+".toRegex(), " ")
        val abstract = entry.summary.trim().replace("\\s+".toRegex(), " ")
        val authorsList = entry.authors?.map { it.name } ?: listOf("Autore Sconosciuto")
        val keywordsString = entry.categories?.joinToString(", ") { it.term } ?: "N/A"
        val fullIdWithVersion = entry.id.trim().removePrefix("http://arxiv.org/abs/")
        val paperId = fullIdWithVersion.substringBeforeLast('v')

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
            keywords = keywordsString,
            publishedDate = entry.publishedDate.substringBefore("T"),
            htmlLink = paperHtmlLink,
            pdfLink = paperPdfLink,
            imageUrl = null
        )
    }
}