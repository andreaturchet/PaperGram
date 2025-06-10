package com.ciwrl.papergram.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: SavedPaperEntity)
    
    @Query("DELETE FROM saved_papers WHERE id = :paperId")
    suspend fun deletePaperById(paperId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM saved_papers WHERE id = :paperId)")
    fun isPaperSaved(paperId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_papers WHERE id = :paperId)")
    fun isPaperSavedSync(paperId: String): Boolean

    @Query("SELECT * FROM saved_papers ORDER BY title ASC")
    fun getAllSavedPapers(): Flow<List<SavedPaperEntity>>
}