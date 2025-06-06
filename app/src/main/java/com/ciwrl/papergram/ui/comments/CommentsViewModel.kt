package com.ciwrl.papergram.ui.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ciwrl.papergram.data.model.Comment
import java.util.UUID

class CommentsViewModel : ViewModel() {

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    fun loadCommentsForPaper(paperId: String) {
        val fakeComments = listOf(
            Comment(
                id = UUID.randomUUID().toString(),
                userName = "Andrea",
                userAvatarUrl = "",
                timestamp = "2 ore fa",
                commentText = "Articolo molto interessante, specialmente la sezione sulla metodologia. Grazie per la condivisione!"
            ),
            Comment(
                id = UUID.randomUUID().toString(),
                userName = "Giulia",
                userAvatarUrl = "",
                timestamp = "1 ora fa",
                commentText = "Qualcuno ha provato a replicare i risultati? Sarei curioso di vedere i dati grezzi."
            ),
            Comment(
                id = UUID.randomUUID().toString(),
                userName = "Marco",
                userAvatarUrl = "",
                timestamp = "23 minuti fa",
                commentText = "Penso che ci sia un piccolo errore nella formula (3). Il coefficente non dovrebbe essere al quadrato?"
            )
        )
        _comments.value = fakeComments
    }
}