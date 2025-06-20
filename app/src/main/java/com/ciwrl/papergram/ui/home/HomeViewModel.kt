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

    private val paperDeck = mutableListOf<UiPaper>()
    private val categoryMap: Map<String, String> = Datasource.getMainCategories().flatMap { it.subCategories }.associate { it.code to it.name }
    private val paperMapper = PaperMapper(categoryMap)

    private var isCurrentlyLoading = false
    private var currentStartIndex = 0
    private var backgroundPreloadJob: Job? = null

    init {
        refreshFeed()
    }

    fun refreshFeed() {
        if (isCurrentlyLoading) return
        backgroundPreloadJob?.cancel() // Stop any previous preloading
        paperDeck.clear()
        fetchInitialBatch()
    }

    fun loadMorePapers() {
        if (paperDeck.isEmpty()) return

        val papersToTake = paperDeck.take(20)
        paperDeck.removeAll(papersToTake)
        _papers.update { currentList -> currentList + papersToTake }

        if (paperDeck.size < 20) {
            preloadDeck() // Pre-fetch more if the deck is getting low
        }
    }

    private fun fetchInitialBatch() {
        isCurrentlyLoading = true
        _papers.value = emptyList()
        _status.value = ApiStatus.LOADING

        viewModelScope.launch {
            val initialPapers = fetchPapersFromApi(isInitialLoad = true)
            if (initialPapers != null) {
                _papers.value = initialPapers
                _status.value = ApiStatus.DONE
                preloadDeck() // Start preloading the deck in the background
            } else {
                _status.value = ApiStatus.ERROR
            }
            isCurrentlyLoading = false
        }
    }

    private fun preloadDeck() {
        if (isCurrentlyLoading) return
        isCurrentlyLoading = true

        backgroundPreloadJob = viewModelScope.launch {
            // Preload 3 more pages for the deck
            repeat(3) {
                val nextBatch = fetchPapersFromApi(isInitialLoad = false)
                if (nextBatch != null) {
                    paperDeck.addAll(nextBatch.shuffled())
                } else {
                    return@launch // Stop preloading on error
                }
            }
            isCurrentlyLoading = false
        }
    }

    private suspend fun fetchPapersFromApi(isInitialLoad: Boolean): List<UiPaper>? {
        return try {
            val feedMode = UserPreferences.getFeedMode(getApplication())
            var startIndex = if (isInitialLoad) 0 else currentStartIndex
            var sortBy = "lastUpdatedDate"

            if (feedMode == UserPreferences.FEED_MODE_RANDOM) {
                if(isInitialLoad) {
                    val maxRandomOffset = 4000
                    startIndex = (0..maxRandomOffset).random()
                }
                sortBy = "relevance"
            }
            currentStartIndex = startIndex

            val searchQuery = UserPreferences.getCategories(getApplication()).let {
                if (it.isNotEmpty()) it.joinToString(separator = " OR ") { "cat:$it" } else "cat:cs.AI"
            }

            val response = RetrofitInstance.arxivApiService.getRecentPapers(
                searchQuery = searchQuery,
                sortBy = sortBy,
                start = currentStartIndex,
                maxResults = 25
            )

            currentStartIndex += 25

            if (response.isSuccessful && response.body() != null) {
                mapToUiPapers(response.body()!!.entries ?: emptyList())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "API call failed", e)
            null
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