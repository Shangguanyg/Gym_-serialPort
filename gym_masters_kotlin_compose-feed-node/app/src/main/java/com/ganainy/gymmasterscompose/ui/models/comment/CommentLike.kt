package com.ganainy.gymmasterscompose.ui.models.comment

import com.google.firebase.Timestamp


/**
 * Data class representing a comment like.
 *
 * @property id The unique identifier for the comment like.
 * @property userId The unique identifier of the user who liked the comment.
 * @property commentId The unique identifier of the comment that was liked.
 * @property postId The unique identifier of the post that the comment belongs to.
 * @property timestamp The timestamp when the post was liked.
 */
// CommentLike.kt 管理评论的点赞功能，包括创建唯一 ID 和转换为实体格式的相关方法
data class CommentLike(
    val id: String = "", // Will be "$userId_$postId"
    val userId: String = "",
    val commentId: String = "",
    val postId: String = "",
    val timestamp: Timestamp = Timestamp.now()
) {
    fun toEntity(): CommentLikeEntity {
        return CommentLikeEntity.fromCommentLike(this)
    }

    companion object {
        fun createId(userId: String, commentId: String, postId: String) =
            "${userId}_${commentId}_${postId}"

        const val COMMENT_ID_FIELD = "commentId" // Collection name for comment likes
        const val COMMENT_LIKES_COLLECTION = "comment_likes" // Collection name for comment likes
    }
}