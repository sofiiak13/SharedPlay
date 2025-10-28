package me.sofiiak.sharedplay.data
import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.dto.ReactionResponse
import retrofit2.http.*

interface CommentService {

    @POST("/comment/")
    suspend fun createComment(
        @Query("content") content: String,
        @Query("parentId") parentId: String,
        @Query("userId") userId: String
    ): CommentResponse

    @GET("/comment/{comment_id}")
    suspend fun getComment(
        @Path("comment_id") commentId: String
    ): CommentResponse

    @PATCH("/comment/{comment_id}")
    suspend fun updateComment(
        @Path("comment_id") commentId: String,
        @Query("content") content: String
    ): CommentResponse

    @DELETE("/comment/{comment_id}")
    suspend fun deleteComment(
        @Path("comment_id") commentId: String
    ): String


    @POST("/reaction/")
    suspend fun createReaction(
        @Query("commentId") commentId: String,
        @Query("userId") userId: String,
        @Query("reactionType") reactionType: String
    ): ReactionResponse

    @GET("/reaction/{reaction_id}")
    suspend fun getReaction(
        @Path("reaction_id") reactionId: String
    ): ReactionResponse

    @PATCH("/reaction/{reaction_id}")
    suspend fun updateReaction(
        @Path("reaction_id") reactionId: String,
        @Query("reactionType") reactionType: String
    ): ReactionResponse

    @DELETE("/reaction/{reaction_id}")
    suspend fun deleteReaction(
        @Path("reaction_id") reactionId: String
    ): String
}