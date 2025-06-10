package com.ciwrl.papergram.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: UserLikeEntity)

    @Query("DELETE FROM user_likes WHERE paperId = :paperId")
    suspend fun delete(paperId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM user_likes WHERE paperId = :paperId)")
    fun isLiked(paperId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM user_likes WHERE paperId = :paperId)")
    fun isLikedSync(paperId: String): Boolean
}