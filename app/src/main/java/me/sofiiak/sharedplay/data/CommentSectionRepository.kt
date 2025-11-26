package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.dto.ReactionResponse

interface CommentSectionRepository {

    suspend fun getAllComments(songId: String): Result<List<CommentResponse>>
    suspend fun writeComment(comment: CommentResponse): Result<CommentResponse>
    suspend fun editComment(commentId: String, newText: String): Result<CommentResponse>
    suspend fun deleteComment(commentId: String): Result<String>
    suspend fun getLatestComment(songId: String): Result<CommentResponse>
    suspend fun getCommentsCount(songId: String): Int

    suspend fun getReactions(commentId: String): Result<List<ReactionResponse>>
    suspend fun addReaction(reaction: ReactionResponse): Result<ReactionResponse>
    suspend fun deleteReaction(reactionId: String): Result<String>
}
