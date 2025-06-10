package com.ciwrl.papergram.ui.comments

import com.ciwrl.papergram.data.model.Comment

data class CommentThread(
    val topLevelComment: Comment,
    val replies: List<Comment>
)