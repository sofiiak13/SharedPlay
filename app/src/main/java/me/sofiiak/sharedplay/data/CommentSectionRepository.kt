package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.dto.ReactionResponse

interface CommentSectionRepository {

    suspend fun writeComment(
        content: String,
        parentId: String,
        userId: String,
    ): CommentResponse

    suspend fun getComment(commentId: String): CommentResponse

    suspend fun editComment(
        commentId: String,
        content: String
    ): CommentResponse

    suspend fun deleteComment(commentId: String): String

    suspend fun createReaction(
        commentId: String,
        userId: String,
        reactionType: String
    ): ReactionResponse

    suspend fun getReaction(reactionId: String): ReactionResponse

    suspend fun updateReaction(
        reactionId: String,
        reactionType: String
    ): ReactionResponse

    suspend fun deleteReaction(reactionId: String): String
}
