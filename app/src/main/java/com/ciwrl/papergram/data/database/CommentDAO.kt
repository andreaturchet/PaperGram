package com.ciwrl.papergram.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert
    suspend fun insert(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE paperId = :paperId ORDER BY timestamp DESC")
    fun getCommentsForPaper(paperId: String): Flow<List<CommentEntity>>
}