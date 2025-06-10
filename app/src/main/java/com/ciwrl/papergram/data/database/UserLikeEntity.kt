package com.ciwrl.papergram.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_likes")
data class UserLikeEntity(@PrimaryKey val paperId: String)