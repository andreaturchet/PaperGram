package com.ciwrl.papergram.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The main Room database for the application.
 *
 * This database holds tables for saved papers, user likes, and comments.
 * It is implemented as a singleton to ensure only one instance of the database is
 * ever created.
 *
 * @see SavedPaperEntity
 * @see UserLikeEntity
 * @see CommentEntity
 */

@Database(entities = [SavedPaperEntity::class, UserLikeEntity::class, CommentEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedPaperDao(): SavedPaperDao
    abstract fun userLikeDao(): UserLikeDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE comments ADD COLUMN parent_id INTEGER DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "papergram_database"
                )
                    .addMigrations(MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}