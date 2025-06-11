package com.ciwrl.papergram.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the `saved_papers` table.
 * Provides methods for interacting with saved paper data in the database.
 */

@Dao
interface SavedPaperDao {

    /**
     * Inserts a paper into the saved_papers table. If the paper already exists, it is replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: SavedPaperEntity)

    /**
     * Deletes a saved paper from the database by its ID.
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM saved_papers WHERE id = :paperId")
    suspend fun deletePaperById(paperId: String): Int

    /**
     * Observes a single paper's saved state.
     * @return A [Flow] that emits true if the paper is saved, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM saved_papers WHERE id = :paperId)")
    fun isPaperSaved(paperId: String): Flow<Boolean>

    /**
     * Checks synchronously if a paper is saved. Should be called from a background thread.
     * @return A boolean that is true if the paper is saved, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM saved_papers WHERE id = :paperId)")
    fun isPaperSavedSync(paperId: String): Boolean

    /**
     * Retrieves all saved papers from the database, ordered by title.
     * @return A [Flow] emitting a list of all [SavedPaperEntity] objects.
     */
    @Query("SELECT * FROM saved_papers ORDER BY title ASC")
    fun getAllSavedPapers(): Flow<List<SavedPaperEntity>>
}