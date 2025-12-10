package me.sofiiak.sharedplay.data

import android.util.Log
import jakarta.inject.Inject
import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.dto.ReactionResponse

private const val TAG = "CommentSectionRepositoryImpl"

class CommentSectionRepositoryImpl @Inject constructor (
    private val service: Service
) : CommentSectionRepository {

    override suspend fun getAllComments(songId: String): Result<List<CommentResponse>> =
        runCatching {
            service.getAllComments(songId)
        }.onFailure { error ->
            Log.e(TAG, "getAllComments(${songId}): ", error)
        }

    override suspend fun writeComment(comment: CommentResponse): Result<CommentResponse> =
        runCatching {
            service.createComment(comment)
        }.onFailure { error ->
            Log.e(TAG, "writeComment: ", error)
        }

    override suspend fun editComment(commentId: String, newText: String): Result<CommentResponse> =
        runCatching {
            service.updateComment(commentId, newText)
        }.onFailure { error ->
            Log.e(TAG, "editComment: ", error)
        }

    override suspend fun deleteComment(commentId: String): Result<String> =
        runCatching {
            service.deleteComment(commentId).message
        }.onFailure { error ->
            Log.e(TAG, "deleteComment: ", error)
        }

    override suspend fun getLatestComment(songId: String): Result<CommentResponse> =
        runCatching {
            service.getLatestComment(songId)
        }.onFailure { error ->
            Log.e(TAG, "getLatestComment: ", error)
        }

    override suspend fun getCommentsCount(songId: String): Int {
        getAllComments(songId).onSuccess { comments ->
            return comments.size
        }
        return 0
    }

    override suspend fun getReactions(commentId: String): Result<List<ReactionResponse>> =
        runCatching {
            service.getReactions(commentId)
        }.onFailure { error ->
            Log.e(TAG, "getReactions: ", error)
        }

    override suspend fun addReaction(reaction: ReactionResponse): Result<ReactionResponse> =
        runCatching {
            service.createReaction(reaction)
        }.onFailure { error ->
            Log.e(TAG, "addReaction: ", error)
        }

    override suspend fun deleteReaction(reactionId: String):  Result<String> =
        runCatching {
            service.deleteReaction(reactionId).message
        }.onFailure { error ->
            Log.e(TAG, "deleteReaction: ", error)
        }
}
