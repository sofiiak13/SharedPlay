package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.dto.ReactionResponse

class CommentSectionRepositoryImpl(
    private val service: CommentService
) : CommentSectionRepository {

    override suspend fun writeComment(
        content: String,
        parentId: String,
        userId: String,
    ): CommentResponse {
        try {
            return service.createComment(content, parentId, userId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getComment(commentId: String): CommentResponse {
        try {
            return service.getComment(commentId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun editComment(
        commentId: String,
        content: String
    ): CommentResponse {
        try {
            return service.updateComment(commentId, content)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteComment(commentId: String): String {
        try {
            return service.deleteComment(commentId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createReaction(
        commentId: String,
        userId: String,
        reactionType: String
    ): ReactionResponse {
        try {
            return service.createReaction(commentId, userId, reactionType)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getReaction(reactionId: String): ReactionResponse {
        try {
            return service.getReaction(reactionId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateReaction(
        reactionId: String,
        reactionType: String
    ): ReactionResponse {
        try {
            return service.updateReaction(reactionId, reactionType)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteReaction(reactionId: String): String {
        try {
            return service.deleteReaction(reactionId)
        } catch (e: Exception) {
            throw e
        }
    }
}