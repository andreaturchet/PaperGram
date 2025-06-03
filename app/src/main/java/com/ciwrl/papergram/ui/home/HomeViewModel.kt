package com.ciwrl.papergram.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.data.database.AppDatabase
import com.ciwrl.papergram.data.database.SavedPaperEntity
import com.ciwrl.papergram.data.model.DisplayCategory
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.data.model.api.ArxivEntry
import com.ciwrl.papergram.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiPaper(val paper: Paper, val isSaved: Boolean)
enum class ApiStatus { LOADING, ERROR, DONE }

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableStateFlow(ApiStatus.LOADING)
    val status: StateFlow<ApiStatus> = _status

    private val savedPaperDao = AppDatabase.getDatabase(application).savedPaperDao()
    private val _refreshTrigger = MutableStateFlow(System.currentTimeMillis())

    private val categoryMap: Map<String, String> = Datasource.getMainCategories().flatMap { it.subCategories }.associate { it.code to it.name }

    fun refreshFeed() {
        _refreshTrigger.value = System.currentTimeMillis()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val papers: StateFlow<List<UiPaper>> = _refreshTrigger.flatMapLatest {
        flow {
            _status.value = ApiStatus.LOADING
            try {
                val selectedCategories = UserPreferences.getCategories(getApplication())
                val searchQuery = if (selectedCategories.isNotEmpty()) {
                    selectedCategories.joinToString(separator = " OR ") { "cat:$it" }
                } else {
                    "cat:cs.AI"
                }

                val arxivResponse = RetrofitInstance.arxivApiService.getRecentPapers(
                    searchQuery = searchQuery,
                    maxResults = 20,
                    start = 0
                )

                if (arxivResponse.isSuccessful && arxivResponse.body() != null) {
                    val papersFromArxiv = arxivResponse.body()?.entries?.mapNotNull { it?.let { entry -> mapArxivEntryToPaper(entry) } } ?: emptyList()
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

    private fun mapArxivEntryToPaper(entry: ArxivEntry): Paper {
        val title = entry.title.trim().replace("\\s+".toRegex(), " ")
        val abstract = entry.summary.trim().replace("\\s+".toRegex(), " ")
        val authorsList = entry.authors?.map { it.name } ?: listOf("Autore Sconosciuto")

        val categories = entry.categories?.map { category ->
            if (categoryMap.containsKey(category.term)) {
                DisplayCategory(name = categoryMap[category.term]!!, isTranslated = true)
            } else {
                DisplayCategory(name = category.term, isTranslated = false)
            }
        } ?: emptyList()

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
            displayCategories = categories,
            publishedDate = entry.publishedDate.substringBefore("T"),
            htmlLink = paperHtmlLink,
            pdfLink = paperPdfLink
        )
    }
}