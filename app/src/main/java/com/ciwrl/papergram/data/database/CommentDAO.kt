package com.ciwrl.papergram.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert
    suspend fun insert(comment: CommentEntity)


    @Query("SELECT * FROM comments WHERE paperId = :paperId AND parent_id IS NULL ORDER BY timestamp DESC")
    fun getTopLevelCommentsForPaper(paperId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE parent_id = :commentId ORDER BY timestamp ASC")
    fun getRepliesForComment(commentId: Int): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments WHERE paperId = :paperId")
    fun getCommentCountForPaperSync(paperId: String): Int
}