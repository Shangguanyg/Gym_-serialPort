package com.ganainy.gymmasterscompose.ui.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing cached likes in Room database
 * CachedLikeDao.kt 文件是一个 Room DAO（数据访问对象）接口 ，用于管理本地 Room 数据库中缓存的点赞，以便在 Gym Masters 应用中进行乐观的界面更新。
 */
@Dao
//内部结构
// DAO 被定义为一个用 @Dao 注释的接口，并包含多种数据库作方法 CachedLikeDao.kt：12-13 。
// 它与 CachedLike 实体配合使用，该实体通过 LikeType 枚举支持不同类型的点赞：POST、COMMENT 和 WORKOUT CachedLike.kt：23-27。
// CachedLike 实体本身包含用于跟踪 like 状态的字段，包括 id、userId、targetId、postId、likeType、timestamp、isPending 和 isLikedCachedLike.kt：10-21 。

interface CachedLikeDao {

    //observeLike（）：返回一个流，用于观察特定点赞的状态以进行实时界面更新
    //observeLike 方法作为 Room DAO 方法实现，该方法返回 Flow<CachedLike？> 来观察数据库中类似数据的变化
    //该方法使用 SQL 查询注释，该注释从 cached_likes 表中选择具有与提供的参数（userId、targetId、likeType）匹配的 WHERE 条件的所有列。
    // 它还通过使用逻辑检查 (postId = :postId OR postId IS NULL) 可选的 postId 参数是否与提供的值匹配或为 NULL 来处理它。

    //@Query(...): 这是 Room 提供的注解，用于定义直接在 SQL 中执行的查询。这个注解会将 SQL 查询语句与 DAO 方法关联。
    //查询语句:
    //SELECT * FROM cached_likes: 查询 cached_likes 表中的所有列。它表示这是一个完整的表查询。
    //WHERE userId = :userId: 限制条件，查找特定用户的点赞记录，:userId 指代这个方法的参数 userId。
    //AND targetId = :targetId: 进一步限制，查找特定目标的点赞记录，:targetId 代表相应方法的参数 targetId。
    //AND likeType = :type: 限制点赞类型，这里 :type 是方法参数 type（假设为枚举类型 LikeType）。
    //AND (postId = :postId OR postId IS NULL): 这部分逻辑与其他限制条件一起工作，可以查找特定帖子（通过 postId）的点赞，或接受 postId 为 null 的情况。这在设计点赞功能时很有用，如果某个点赞数据不指向特定帖子（即 postId 为 null），那么该记录也可以被检索。
    @Query("SELECT * FROM cached_likes WHERE userId = :userId AND targetId = :targetId AND likeType = :type AND (postId = :postId OR postId IS NULL)")
    //方法名称: observeLike，表示这个方法用于观察用户的点赞状态。
    //参数:
    //userId: String: 用户的唯一标识符，用于查找与该用户相关的点赞记录。
    //targetId: String: 目标的唯一标识符，通常指的是被点赞的内容（如帖子、评论、图片等）的 ID。
    //type: LikeType: 点赞的类型，表示是在哪种内容上进行的点赞（例如，如果点赞可以针对帖子、图片或其他类型的内容）。
    //postId: String? = null: 可选参数，表示特定帖子的 ID，默认为 null，表示所有与目标相关的点赞记录。
    //返回类型:
    //Flow<CachedLike?>: 返回一个 Flow 对象，Flow 是 Kotlin 协程库中的一部分，提供了一种异步数据流的方式。返回 CachedLike? 表示可能会返回一个 CachedLike 实例，或者是 null。这种方法通常用于观察数据变化，当数据库中的相关数据发生更改时，可以及时通知观察者。
    fun observeLike(userId: String, targetId: String, type: LikeType, postId: String? = null): Flow<CachedLike?>

    //isLiked（）：检查用户当前是否喜欢某个特定商品
    //查询逻辑:
    //这个 SQL 查询检查 cached_likes 表中是否存在记录，符合条件。
    //主要查找用户是否对特定目标和类型的内容点赞。
    //返回类型:
    //返回类型为 Boolean，指示是否存在满足条件的记录（点赞）。
    @Query("SELECT EXISTS(SELECT 1 FROM cached_likes WHERE userId = :userId AND targetId = :targetId AND likeType = :type AND isLiked = 1 AND (postId = :postId OR postId IS NULL))")
    suspend fun isLiked(userId: String, targetId: String, type: LikeType, postId: String? = null): Boolean

    // 插入点赞记录
    // @Insert 注解: 表示这是一个插入操作。
    //onConflict = OnConflictStrategy.REPLACE: 插入时如果发生冲突（比如主键冲突），会替换掉现有的记录。
    //方法参数:
    //like: CachedLike: 传入要插入的 CachedLike 对象。
    //返回类型: 该方法没有返回值，表示插入操作的结果不需要反馈。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: CachedLike)

    // 删除点赞记录
    // 查询逻辑:
    //通过 ID 删除 cached_likes 表中的记录。
    //方法参数:
    //id: String: 指定需要删除的记录的 ID。
    @Query("DELETE FROM cached_likes WHERE id = :id")
    suspend fun deleteLike(id: String)

    // 获取待处理的点赞记录
    // 查询逻辑:
    //从 cached_likes 表中选择所有标记为待处理 (isPending = 1) 的点赞记录。
    //返回类型:
    //List<CachedLike>: 返回一个 CachedLike 对象的列表，包含所有待处理的点赞记录。
    @Query("SELECT * FROM cached_likes WHERE isPending = 1")
    suspend fun getPendingLikes(): List<CachedLike>

    // 观察特定类型的点赞记录
    // 查询逻辑:
    //查询特定用户的点赞记录，以 likeType 和可选的 postId 进行过滤。
    //返回类型:
    //Flow<List<CachedLike?>>: 返回点赞记录的流式列表，允许观察到数据变化。
    @Query("SELECT * FROM cached_likes WHERE userId = :userId AND likeType = :type AND (postId = :postId OR postId IS NULL)")
    fun observeTypeLikes(userId: String, type: LikeType, postId: String? = null): Flow<List<CachedLike?>>

    //按 ID 查询点赞记录
    //查询逻辑:
    //通过点赞 ID 从 cached_likes 表中查询特定的点赞记录。
    //返回类型:
    //CachedLike?: 返回一个 CachedLike 实例，可能为 null 表示没有找到相应 ID 的记录。
    @Query("SELECT * FROM cached_likes WHERE id = :likeId LIMIT 1") // Query by primary key 'id'
    suspend fun getLike(likeId: String): CachedLike? // Returns nullable CachedLike
}