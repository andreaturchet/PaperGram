package com.ciwrl.papergram.ui.comments

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.data.database.AppDatabase
import com.ciwrl.papergram.data.database.CommentEntity
import com.ciwrl.papergram.data.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CommentsViewModel(application: Application) : AndroidViewModel(application) {

    private val commentDao = AppDatabase.getDatabase(application).commentDao()
    private val userName = UserPreferences.getUserName(application)

    fun getCommentsForPaper(paperId: String): Flow<List<Comment>> {
        val realCommentsFlow = commentDao.getCommentsForPaper(paperId)

        return realCommentsFlow.map { realCommentEntities ->
            val realComments = realCommentEntities.map { entity ->
                Comment(
                    id = entity.id.toString(),
                    userName = entity.userName,
                    userAvatarUrl = "",
                    timestamp = DateUtils.getRelativeTimeSpanString(entity.timestamp).toString(),
                    commentText = entity.text
                )
            }
            realComments + createFakeComments()
        }
    }

    fun addComment(paperId: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = CommentEntity(
                paperId = paperId,
                text = text,
                timestamp = System.currentTimeMillis(),
                userName = userName
            )
            commentDao.insert(comment)
        }
    }

    // Funzione helper per creare una lista di commenti finti
    private fun createFakeComments(): List<Comment> {
        val now = System.currentTimeMillis()
        return listOf(
            Comment(
                id = "fake_1",
                userName = "Marco Rossi",
                userAvatarUrl = "",
                timestamp = DateUtils.getRelativeTimeSpanString(now - TimeUnit.MINUTES.toMillis(15)).toString(),
                commentText = "Analisi molto interessante, specialmente la parte sulla metodologia. Mi chiedo quali potrebbero essere le implicazioni future."
            ),
            Comment(
                id = "fake_2",
                userName = "Giulia Bianchi",
                userAvatarUrl = "",
                timestamp = DateUtils.getRelativeTimeSpanString(now - TimeUnit.HOURS.toMillis(2)).toString(),
                commentText = "Concordo con l'autore, ma vorrei vedere un confronto con i risultati dello studio di Smith et al. del 2022."
            ),
            Comment(
                id = "fake_3",
                userName = "Alessandro Verdi",
                userAvatarUrl = "",
                timestamp = DateUtils.getRelativeTimeSpanString(now - TimeUnit.DAYS.toMillis(1)).toString(),
                commentText = "Questo paper è fondamentale per chiunque lavori in questo campo. Un punto di riferimento."
            )
        )
    }
}