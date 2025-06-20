package com.ciwrl.papergram.ui.comments

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.data.database.AppDatabase
import com.ciwrl.papergram.data.database.CommentEntity
import com.ciwrl.papergram.data.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Manages UI state and business logic for the [CommentsFragment].
 *
 * This ViewModel is responsible for:
 * - Loading hierarchical comment threads (top-level comments and their replies) for a
 * specific paper from the local Room database via [CommentDao].
 * - Combining real comments with a set of hard-coded "fake" comments from [Datasource]
 * for demonstration purposes.
 * - Handling the creation and insertion of new comments, including replies to existing ones,
 * into the local database.
 * - Exposing the final list of comment threads to the UI using a [StateFlow].
 * - Mapping database entities ([CommentEntity]) to UI models ([Comment]), including
 * formatting timestamps for display.
 *
 * @param application The application instance, required for accessing the context.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class CommentsViewModel(application: Application) : AndroidViewModel(application) {

    private val commentDao = AppDatabase.getDatabase(application).commentDao()
    private val userName = UserPreferences.getUserName(application)

    private val _commentThreads = MutableStateFlow<List<CommentThread>>(emptyList())
    val commentThreads: StateFlow<List<CommentThread>> = _commentThreads

    fun loadCommentsForPaper(paperId: String) {
        viewModelScope.launch {
            val fakeComments = Datasource.getFakeComments()
            val fakeThreads = listOf(
                CommentThread(
                    topLevelComment = fakeComments.first { it.id == "fake_1" },
                    replies = listOf(fakeComments.first { it.id == "fake_2" })
                )
            )

            commentDao.getTopLevelCommentsForPaper(paperId)
                .flatMapLatest { topLevelEntities ->
                    if (topLevelEntities.isEmpty()) {
                        flowOf(fakeThreads)
                    } else {
                        val threadFlows = topLevelEntities.map { topLevelEntity ->
                            commentDao.getRepliesForComment(topLevelEntity.id).map { replyEntities ->
                                CommentThread(
                                    topLevelComment = mapEntityToModel(topLevelEntity),
                                    replies = replyEntities.map { mapEntityToModel(it) }
                                )
                            }
                        }
                        combine(threadFlows) { threads ->
                            threads.toList() + fakeThreads
                        }
                    }
                }
                .collect { finalThreadList ->
                    _commentThreads.value = finalThreadList
                }
        }
    }

    fun addComment(paperId: String, text: String, parentId: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = CommentEntity(
                paperId = paperId,
                text = text,
                timestamp = System.currentTimeMillis(),
                userName = userName,
                parentId = parentId
            )
            commentDao.insert(comment)
        }
    }

    private fun mapEntityToModel(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id.toString(),
            userName = entity.userName,
            userAvatarUrl = "",
            timestamp = DateUtils.getRelativeTimeSpanString(entity.timestamp).toString(),
            commentText = entity.text
        )
    }
}