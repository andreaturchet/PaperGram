package com.ciwrl.papergram.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.data.database.AppDatabase
import com.ciwrl.papergram.data.database.SavedPaperEntity
import com.ciwrl.papergram.data.database.UserLikeEntity
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
    val status: StateFlow<ApiStatus> = _status.asStateFlow()

    private val savedPaperDao = AppDatabase.getDatabase(application).savedPaperDao()
    private val categoryMap: Map<String, String> = Datasource.getMainCategories().flatMap { it.subCategories }.associate { it.code to it.name }
    private val _papers = MutableStateFlow<List<UiPaper>>(emptyList())
    val papers: StateFlow<List<UiPaper>> = _papers.asStateFlow()
    private var currentStartIndex = 0
    private var isCurrentlyLoading = false
    private var hasLoadedAllItems = false
    private val resultsPerPage = 15

    init {
        refreshFeed()
    }
    fun refreshFeed() {
        currentStartIndex = 0
        hasLoadedAllItems = false
        _papers.value = emptyList()
        fetchPapers()
    }

    fun loadMorePapers() {
        if (isCurrentlyLoading || hasLoadedAllItems) {
            return
        }
        fetchPapers()
    }

    private fun fetchPapers() {
        isCurrentlyLoading = true
        _status.value = ApiStatus.LOADING

        viewModelScope.launch {
            try {
                val selectedCategories = UserPreferences.getCategories(getApplication())
                val searchQuery = if (selectedCategories.isNotEmpty()) {
                    selectedCategories.joinToString(separator = " OR ") { "cat:$it" }
                } else {
                    "cat:cs.AI"
                }

                val arxivResponse = RetrofitInstance.arxivApiService.getRecentPapers(
                    searchQuery = searchQuery,
                    maxResults = resultsPerPage,
                    start = currentStartIndex
                )

                if (arxivResponse.isSuccessful && arxivResponse.body() != null) {
                    val newPapers = arxivResponse.body()?.entries?.mapNotNull { it?.let { entry -> mapArxivEntryToPaper(entry) } } ?: emptyList()

                    if (newPapers.isEmpty()) {
                        hasLoadedAllItems = true
                    } else {
                        val newUiPapers = withContext(Dispatchers.IO) {
                            newPapers.map { paper ->
                                UiPaper(paper = paper, isSaved = savedPaperDao.isPaperSavedSync(paper.id))
                            }
                        }
                        _papers.value = _papers.value + newUiPapers
                        currentStartIndex += newPapers.size
                    }
                    _status.value = ApiStatus.DONE
                } else {
                    _status.value = ApiStatus.ERROR
                    Log.e("HomeViewModel", "ArXiv API request failed: ${arxivResponse.message()}")
                }
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                Log.e("HomeViewModel", "Failed to fetch data", e)
            } finally {
                isCurrentlyLoading = false
            }
        }
    }

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
            _papers.update { currentList ->
                currentList.map { uiPaper ->
                    if (uiPaper.paper.id == paper.id) {
                        uiPaper.copy(isSaved = !isCurrentlySaved)
                    } else {
                        uiPaper
                    }
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
            pdfLink = paperPdfLink,
            likeCount = (20..350).random(),
            isLikedByUser = false,
            commentCount = (5..50).random()
        )
    }
}