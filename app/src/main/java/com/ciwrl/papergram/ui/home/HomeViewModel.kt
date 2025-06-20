package com.ciwrl.papergram.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.data.database.CommentDao
import com.ciwrl.papergram.data.database.SavedPaperDao
import com.ciwrl.papergram.data.database.UserLikeDao
import com.ciwrl.papergram.data.database.SavedPaperEntity
import com.ciwrl.papergram.data.database.UserLikeEntity
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.data.model.PaperMapper
import com.ciwrl.papergram.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.ciwrl.papergram.data.model.api.ArxivEntry

/**
 * Manages the UI state and business logic for the [FeedFragment].
 *
 * This ViewModel is responsible for:
 * - Fetching papers from the ArXiv API based on user's selected categories.
 * - Handling pagination to load more papers as the user scrolls.
 * - Managing the loading and error states of the API calls.
 * - Interacting with the local database to handle saving/liking papers.
 * - Providing the list of papers to the UI via a StateFlow.
 *
 * @param application The application instance, required for accessing the context.
 */

data class UiPaper(val paper: Paper, val isSaved: Boolean)
enum class ApiStatus { LOADING, ERROR, DONE }

class HomeViewModel(
    application: Application,
    private val savedPaperDao: SavedPaperDao,
    private val userLikeDao: UserLikeDao,
    private val commentDao: CommentDao
) : AndroidViewModel(application) {

    private val _status = MutableStateFlow(ApiStatus.LOADING)
    val status: StateFlow<ApiStatus> = _status.asStateFlow()

    private val _papers = MutableStateFlow<List<UiPaper>>(emptyList())
    val papers: StateFlow<List<UiPaper>> = _papers.asStateFlow()

    private val categoryMap: Map<String, String> = Datasource.getMainCategories().flatMap { it.subCategories }.associate { it.code to it.name }
    private val paperMapper = PaperMapper(categoryMap)

    private var isCurrentlyLoading = false
    private var hasLoadedAllItems = false
    private var currentStartIndex = 0

    init {
        refreshFeed()
    }

    fun refreshFeed() {
        if (isCurrentlyLoading) return

        hasLoadedAllItems = false
        _papers.value = emptyList()

        // In random mode, we pick a new random start index. In chronological, we start from 0.
        val feedMode = UserPreferences.getFeedMode(getApplication())
        if (feedMode == UserPreferences.FEED_MODE_RANDOM) {
            val maxRandomOffset = 5000
            currentStartIndex = (0..maxRandomOffset).random()
        } else {
            currentStartIndex = 0
        }

        fetchPapers()
    }

    fun loadMorePapers() {
        if (!isCurrentlyLoading && !hasLoadedAllItems) {
            fetchPapers()
        }
    }

    fun searchPapersByTitle(titleQuery: String) {
        if (titleQuery.isBlank()) {
            refreshFeed()
            return
        }
        hasLoadedAllItems = false
        _papers.value = emptyList()
        currentStartIndex = 0
        val apiQuery = "ti:\"$titleQuery\""
        fetchPapers(customQuery = apiQuery, isSearch = true)
    }

    private fun fetchPapers(customQuery: String? = null, isSearch: Boolean = false) {
        isCurrentlyLoading = true
        // Set loading status only if the list is currently empty
        if (_papers.value.isEmpty()) {
            _status.value = ApiStatus.LOADING
        }

        viewModelScope.launch {
            try {
                val feedMode = UserPreferences.getFeedMode(getApplication())
                var sortBy = "lastUpdatedDate"

                if (feedMode == UserPreferences.FEED_MODE_RANDOM && !isSearch) {
                    sortBy = "relevance"
                }

                val searchQuery = customQuery ?: UserPreferences.getCategories(getApplication()).let {
                    if (it.isNotEmpty()) it.joinToString(separator = " OR ") { "cat:$it" } else "cat:cs.AI"
                }

                val arxivResponse = RetrofitInstance.arxivApiService.getRecentPapers(
                    searchQuery = searchQuery,
                    sortBy = sortBy,
                    start = currentStartIndex,
                    maxResults = 25 // Always fetch small, fast batches
                )

                if (arxivResponse.isSuccessful && arxivResponse.body() != null) {
                    val newArxivEntries = arxivResponse.body()?.entries ?: emptyList()

                    if (newArxivEntries.isEmpty()) {
                        hasLoadedAllItems = true
                    } else {
                        val newUiPapers = mapToUiPapers(newArxivEntries)
                        _papers.update { currentList -> currentList + newUiPapers }
                        currentStartIndex += newArxivEntries.size
                    }
                    _status.value = ApiStatus.DONE
                } else {
                    _status.value = ApiStatus.ERROR
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "API call failed", e)
                _status.value = ApiStatus.ERROR
            } finally {
                isCurrentlyLoading = false
            }
        }
    }

    private suspend fun mapToUiPapers(entries: List<ArxivEntry>): List<UiPaper> {
        return withContext(Dispatchers.IO) {
            entries.mapNotNull { entry ->
                val paperId = entry.id.substringAfterLast('/').substringBeforeLast('v')
                val isLiked = userLikeDao.isLikedSync(paperId)
                val isSaved = savedPaperDao.isPaperSavedSync(paperId)
                val realCommentCount = commentDao.getCommentCountForPaperSync(paperId)
                val totalCommentCount = realCommentCount + Datasource.getFakeComments().size
                val paper = paperMapper.mapArxivEntryToPaper(entry, isLiked, totalCommentCount)
                UiPaper(paper = paper, isSaved = isSaved)
            }
        }
    }

    // Le altre funzioni (toggleLike, toggleSave, etc.) rimangono invariate
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
                    if (uiPaper.paper.id == paper.id) uiPaper.copy(isSaved = !isCurrentlySaved) else uiPaper
                }
            }
        }
    }

    fun toggleLikeState(paper: Paper) {
        viewModelScope.launch {
            val isCurrentlyLiked = withContext(Dispatchers.IO) { userLikeDao.isLikedSync(paper.id) }
            withContext(Dispatchers.IO) {
                if (isCurrentlyLiked) {
                    userLikeDao.delete(paper.id)
                } else {
                    userLikeDao.insert(UserLikeEntity(paper.id))
                }
            }
            _papers.update { currentList ->
                currentList.map { uiPaper ->
                    if (uiPaper.paper.id == paper.id) {
                        val newLikeCount = if (isCurrentlyLiked) paper.likeCount - 1 else paper.likeCount + 1
                        val newPaperState = paper.copy(isLikedByUser = !isCurrentlyLiked, likeCount = newLikeCount)
                        uiPaper.copy(paper = newPaperState)
                    } else {
                        uiPaper
                    }
                }
            }
        }
    }

    fun refreshCommentCountForPaper(paperId: String) {
        viewModelScope.launch {
            val fakeCommentCount = Datasource.getFakeComments().size
            val newRealCount = withContext(Dispatchers.IO) { commentDao.getCommentCountForPaperSync(paperId) }
            val totalCount = newRealCount + fakeCommentCount
            _papers.update { currentList ->
                currentList.map { uiPaper ->
                    if (uiPaper.paper.id == paperId) {
                        uiPaper.copy(paper = uiPaper.paper.copy(commentCount = totalCount))
                    } else {
                        uiPaper
                    }
                }
            }
        }
    }
}