package com.ganainy.gymmasterscompose.ui.models.post

import android.os.Parcelable
import com.ganainy.gymmasterscompose.utils.Utils.generateRandomId
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


/**
 * Data class representing a feed post.
 *
 * @property id The unique identifier of the post.
 * @property content The content of the post.
 * @property imagePathList A list of local paths to images associated with the post.
 * @property imageUrlList A list of URLs to images associated with the post.
 * @property createdAt The timestamp when the post was created.
 * @property tags A list of hashtags associated with the post.
 * @property postMetrics The statistics of the post (likes, comments, shares).
 * @property postCreator The creator of the post.
 */

// FeedPost.kt 文件包含三个主要数据类：
// FeedPost 数据类 (第23-44行)
//
// 作用：表示社交媒体动态帖子的核心数据模型
// 主要属性包括：帖子ID、内容、图片路径列表、图片URL列表、创建时间、标签、帖子统计数据、帖子创建者信息
// 实现了 Parcelable 接口，支持在 Android 组件间传递 FeedPost.kt:22-32
// 包含伴生对象，定义了 Firestore 数据库相关的常量和ID生成方法 FeedPost.kt:33-43
@Parcelize
data class FeedPost(
    val id: String = "",
    val content: String = "",
    val imagePathList: List<String> = emptyList(),
    val imageUrlList: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val tags: List<String> = emptyList(),
    val postMetrics: PostMetrics = PostMetrics(),
    val postCreator: PostCreator = PostCreator()
) : Parcelable {
    companion object {
        const val POST = "post"
        const val POST_METRICS = "postMetrics"
        const val POST_LIKES = "likes"
        const val POST_CREATOR = "postCreator"
        const val CREATOR_ID = "creatorId"
        const val POST_TAGS = "tags"
        const val POSTS_COLLECTION = "posts" // Collection name for posts
        const val CREATED_AT = "createdAt"
        fun createId(): String = generateRandomId(POST)
    }
}

/**
 * Data class representing the statistics of a post.
 *
 * @property postId The unique identifier of the post.
 * @property likes The number of likes the post has received.
 * @property comments The number of comments on the post.
 * @property shares The number of times the post has been shared.
 */
//    //PostMetrics 数据类 (第54-65行)
//    //
//    //作用：存储帖子的统计数据
//    //包含：帖子ID、点赞数、评论数、分享数
//    //同样实现 Parcelable 接口 FeedPost.kt:54-60
//    //定义了统计数据相关的常量 FeedPost.kt:61-64
@Parcelize
data class PostMetrics(
    val postId: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0
) : Parcelable {
    companion object {
        const val POST_METRICS_LIKES = "likes"
        const val POST_METRICS_COMMENTS = "comments"
    }
}


/**
 * Data class representing the creator of a post.
 *
 * @property id The unique identifier of the post creator.
 * @property displayName The display name of the post creator.
 * @property profilePictureUrl The URL of the profile picture of the post creator.
 */
//PostCreator 数据类 (第75-79行)
//
//作用：存储帖子创建者的基本信息
//包含：创建者ID、显示名称、头像URL
//实现 Parcelable 接口 FeedPost.kt:75-79
@Parcelize
data class PostCreator(
    val id: String = "",
    val displayName: String = "",
    val profilePictureUrl: String = ""
) : Parcelable