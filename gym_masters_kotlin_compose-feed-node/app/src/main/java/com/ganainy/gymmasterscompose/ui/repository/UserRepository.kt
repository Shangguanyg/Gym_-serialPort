package com.ganainy.gymmasterscompose.ui.repository

import UserDisplayInfo
import android.net.Uri
import android.util.Log
import com.ganainy.gymmasterscompose.Constants.ID
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.User
import com.ganainy.gymmasterscompose.ui.models.User.Companion.PROFILE_PICTURE_URL
import com.ganainy.gymmasterscompose.ui.models.User.Companion.USERS_COLLECTION
import com.ganainy.gymmasterscompose.ui.models.User.Companion.USER_IMAGES
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost.Companion.POSTS_COLLECTION
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost.Companion.POST_CREATOR
import com.ganainy.gymmasterscompose.utils.CustomException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri
import com.google.firebase.firestore.FieldValue

//定义了 IUserRepository 接口和 UserRepository 类，实现了用户管理和关系管理的功能，主要处理用户的身份验证、用户信息的存取、点赞状态等。

// User profile and relationship management
// interface IUserRepository: 定义了一个接口，声明了用户管理所需的多个操作方法，为具体的实现类提供约定。

//：定义一个用于用户数据管理的接口，提供了一组方法来操作用户信息的 API。
interface IUserRepository {

    //suspend fun createUser(...)：声明一个挂起函数，用于创建新用户，返回创建成功后用户 ID 的 ResultWrapper<String>。
    suspend fun createUser(email: String, password: String): ResultWrapper<String>
    //suspend fun getUserPosts(userId: String)：声明一个挂起函数，返回用户帖子数据的流，用于实时观察用户贴子的更新。
    suspend fun getUserPosts(userId: String): Flow<ResultWrapper<List<FeedPost>>>

    //suspend fun updateUser(...)：声明一个挂起函数，用于更新用户信息，更新内容通过 Map 传递。
    suspend fun updateUser(updates: Map<String, Any>): ResultWrapper<Unit>

    //fun getCurrentUserId()：声明一个返回当前用户 ID 的方法。
    fun getCurrentUserId(): String

    //suspend fun getUserFlow(...)：声明一个挂起函数，返回 Flow 类型，以便在用户信息发生变化时发出更新。
    suspend fun getUserFlow(userId: String?): Flow<ResultWrapper<User>> // get notified when user changes

    //suspend fun getUser(userId: String?)：用于一次性读取用户信息的方法。
    suspend fun getUser(userId: String?): ResultWrapper<User> //read user only once, not notified if user changes

    //suspend fun updateUserProfileImage(...)：用于更新用户头像的方法，返回更新成功后的新头像 URL。
    suspend fun updateUserProfileImage(imagePath: String): ResultWrapper<String>

    //abstract fun getUserDisplayInfo()：声明一个方法，用于获取当前用户的展示信息。
    abstract fun getUserDisplayInfo(): UserDisplayInfo
}


//class UserRepository：定义一个类，用于实现 IUserRepository 接口。
//@Inject constructor(...)：使用 Dagger Hilt 进行依赖注入。封装了对 Firebase Auth、Firestore 和 Storage 的依赖。
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : IUserRepository {


    //    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)：定义一个私有的可变状态流，用于管理当前用户的信息，可以为 null。
    //    在 init 块中：
    //    初始化 _currentUser 的值为当前用户。
    //    添加 Firebase Auth 状态监听器，以便在用户认证状态变化时更新 _currentUser。
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)

    init {
        // Initialize current user
        _currentUser.value = auth.currentUser

        // Listen for auth changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    /**
     * Retrieves the current user's ID.
     */
//    override fun getCurrentUserId(): String =
//        _currentUser.value?.uid ?: throw Exception("User auth not found")

    // override fun getCurrentUserId(): String：重写接口的方法，返回当前用户 ID。
    // 这里硬编码了一个示例 ID，实际实现应该返回 _currentUser 的 ID。
    override fun getCurrentUserId(): String = "123123"

    // --- getUser (One-time read) ---
    // override suspend fun getUser(...)：重写接口的方法，用于根据用户 ID 获取用户信息。
    // val effectiveUserId = userId ?: try { ... }：确定有效的用户 ID。
    // 如果 userId 为 null，则调用 getCurrentUserId() 获取当前用户 ID。如果未登录，则返回错误。
    override suspend fun getUser(userId: String?): ResultWrapper<User> {
        // Determine the effective user ID
        val effectiveUserId = userId ?: try {
            getCurrentUserId()
        } catch (e: AuthRepository.UserNotAuthenticatedException) {
            return ResultWrapper.Error(e) // Return error if not logged in and no ID provided
        }

        //        return withContext(Dispatchers.IO) { ... }：确保在 IO 线程中执行网络或数据库访问。
        //        val userDocRef = firestore.collection(USERS_COLLECTION).document(effectiveUserId)：获取指向 Firestore 中用户文档的引用。
        //        val snapshot = userDocRef.get().await()：异步获取用户文档的快照。


        return withContext(Dispatchers.IO) { // Keep IO dispatcher for network/DB access
            try {
                // Reference to the user document in Firestore
                val userDocRef = firestore.collection(USERS_COLLECTION).document(effectiveUserId)

                // Retrieve the user document snapshot
                val snapshot = userDocRef.get().await()

                // Get the user details from the snapshot
                // 检查文档是否存在。
                //如果存在，尝试将文档转换为 User 对象。
                //如果转换成功，返回成功的结果；否则记录错误并返回错误信息。
                //如果快照不存在，返回一个错误，表示未能找到用户的详细信息。
                if (snapshot.exists()) {
                    val userDetails = snapshot.toObject(User::class.java)
                    if (userDetails != null) {
                        ResultWrapper.Success(userDetails)
                    } else {
                        Log.e("UserRepository", "Failed to convert Firestore document ${snapshot.id} to User object.")
                        ResultWrapper.Error(Exception("Failed to parse user details for ID: $effectiveUserId"))
                    }
                } else {
                    ResultWrapper.Error(Exception("User details not found for ID: $effectiveUserId"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error fetching user $effectiveUserId", e)
                ResultWrapper.Error(e) // Return failure result with the exception
            }
        }
    }

    //    createUser(...)：重写接口的方法，负责创建新用户。
    //    使用 try-catch 处理过程中的异常。
    //    尝试使用 Firebase Auth 创建用户并异步等待结果。
    //    返回成功时封装用户 ID 的结果，如果ID为 null，则抛出异常。
    //    处理异常并返回错误。
    override suspend fun createUser(email: String, password: String): ResultWrapper<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            ResultWrapper.Success(
                result.user?.uid
                    ?: throw Exception("com.ganainy.gymmasterscompose.ui.theme.models.User ID is null")
            )
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }



    // --- getUserFlow (Real-time updates) ---
    //     getUserFlow(...)：提供实时更新用户的数据流。
    //    使用 callbackFlow 创建一个新的流，允许通过 LiveData 监视用户状态变化。
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUserFlow(userId: String?): Flow<ResultWrapper<User>> = callbackFlow {
        //确定有效的用户 ID，当传入的 userId 为 null 时，调用 getCurrentUserId() 获取。
        val effectiveUserId = userId ?: try {
            getCurrentUserId()
        } catch (e: AuthRepository.UserNotAuthenticatedException) {
            trySend(ResultWrapper.Error(e))
            close(e) // Close flow on error
            return@callbackFlow
        }

        // Reference to the user document
        // 在 Firestore 中为用户文档添加快照监听器，以便当该文档发生更改时，会自动推送更新。
        val userDocRef = firestore.collection(USERS_COLLECTION).document(effectiveUserId)
        var listenerRegistration: ListenerRegistration? = null

        try {
            // Listener to handle document changes and errors
            listenerRegistration = userDocRef.addSnapshotListener { snapshot, error ->
                //如果发生错误，使用 trySend 返回错误的信息，并记录日志。
                if (error != null) {
                    trySend(ResultWrapper.Error(error))
                    Log.e("UserRepository", "Error listening to user $effectiveUserId", error)
                    // Consider closing the flow here depending on error type: close(error)
                    return@addSnapshotListener
                }

                //                处理快照数据，检查是否存在。
                //                如果快照有效，则尝试将其转换为 User 对象，成功后推送成功结果；如果失败，推送解析错误。
                //                如果快照无效，则推送“用户未找到”的错误。
                if (snapshot != null && snapshot.exists()) {
                    val user = try { snapshot.toObject(User::class.java) } catch (e: Exception) { null }
                    if (user != null) {
                        trySend(ResultWrapper.Success(user))
                    } else {
                        Log.e("UserRepository", "Failed converting user snapshot ${snapshot.id}")
                        trySend(ResultWrapper.Error(CustomException(R.string.error_parsing_user_data)))
                    }
                } else {
                    // Document doesn't exist or snapshot is null
                    trySend(ResultWrapper.Error(CustomException(R.string.user_not_found)))
                }
            }
        } catch (e: Exception) {
            // Catch exceptions during listener setup
            trySend(ResultWrapper.Error(e))
            close(e)
        }

        // Remove the listener when the flow is closed
        //         使用 awaitClose { ... } 定义在流关闭时执行的逻辑。
        //        日志记录信息并注销监听器，以防止内存泄漏。
        awaitClose {
            Log.d("UserRepository", "Removing listener for user $effectiveUserId")
            listenerRegistration?.remove()
        }
    }

    // --- getUserPosts (Real-time updates) ---
    // override suspend fun getUserPosts(userId: String)：重写接口中根据用户 ID 获取其帖子的方法，返回 Flow 类型，实现实时更新。
    override suspend fun getUserPosts(userId: String): Flow<ResultWrapper<List<FeedPost>>> = callbackFlow {
        //        val postsCollection：获取 Firestore 中帖子集合的引用。
        //        使用 Firestore 的查询功能进行匹配用户的帖子的 creatorID。
        val postsCollection = firestore.collection(POSTS_COLLECTION)
        var listenerRegistration: ListenerRegistration? = null

        //为 Firestore 查询添加快照监听器以接收更新。
        //处理异常并推送错误结果。
        //将获得的帖子快照映射到 FeedPost 类型，处理转换异常，并最终推送结果。
        try {
            // Query posts where the nested creator ID matches
            val query = postsCollection.whereEqualTo("$POST_CREATOR.id", userId)
                // Add ordering by creation date
                .orderBy(FeedPost.CREATED_AT, Query.Direction.DESCENDING)

            listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultWrapper.Error(error))
                    Log.e("UserRepository", "Error listening to posts for user $userId", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(FeedPost::class.java) } catch (e: Exception) {
                            Log.e("UserRepository", "Failed converting post snapshot ${doc.id}", e)
                            null
                        }
                    }
                    trySend(ResultWrapper.Success(posts))
                } else {
                    // Snapshot is null, might indicate an issue or simply no data
                    trySend(ResultWrapper.Success(emptyList()))
                }
            }
        } catch (e: Exception) {
            trySend(ResultWrapper.Error(e))
            close(e)
        }

        awaitClose {
            Log.d("UserRepository", "Removing listener for posts of user $userId")
            listenerRegistration?.remove()
        }
    }


    //    override suspend fun updateUser(...)：重写接口更新用户的方法，接收一个更新属性的映射。
    //    尝试获取当前用户 ID，并处理未认证状态的异常。

    override suspend fun updateUser(updates: Map<String, Any>): ResultWrapper<Unit> {
        Log.e("UserRepository", "override suspend fun updateUser")
        val uid = try { getCurrentUserId() } catch (e: Exception) {
            return ResultWrapper.Error(AuthRepository.UserNotAuthenticatedException("User is not authenticated"))
        }

        return try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(uid)
            userDocRef.update(updates).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user $uid", e)
            ResultWrapper.Error(e)
        }
    }

    //override suspend fun updateUserProfileImage(...)：提供更新用户头像的方法，实现用户头像的上传并更新 Firestore。
    //第一段代码获取当前用户 ID 是相似的逻辑，处理未认证用户的异常。
    override suspend fun updateUserProfileImage(imagePath: String): ResultWrapper<String> {
        val uid = try { getCurrentUserId() } catch (e: Exception) {
            return ResultWrapper.Error(AuthRepository.UserNotAuthenticatedException("User is not authenticated"))
        }

        // Path in Firebase Storage (no change needed here)
        val userImageStorageRef = storage.reference
            .child(USER_IMAGES).child(uid + "_" + imagePath.substringAfterLast("/")) // Include UID for uniqueness

        return try {
            // Upload file to Storage (no change needed here)
            userImageStorageRef.putFile(imagePath.toUri()).await()
            val downloadUrl = userImageStorageRef.downloadUrl.await().toString()

            try {
                // --- Update Firestore ---
                val userDocRef = firestore.collection(USERS_COLLECTION).document(uid)
                // Update only the profilePictureUrl field
                userDocRef.update(PROFILE_PICTURE_URL, downloadUrl).await()
                ResultWrapper.Success(downloadUrl)
            } catch (dbError: Exception) {
                // If Firestore update fails, delete the uploaded file from Storage
                try {
                    Log.w("UserRepository", "Firestore update failed for profile pic, attempting to delete Storage file.")
                    userImageStorageRef.delete().await()
                } catch (deleteError: Exception) {
                    Log.e("UserRepository", "Failed to delete profile pic from Storage after DB error", deleteError)
                }
                throw dbError // Re-throw the database error
            }
        } catch (e: Exception) {
            // Catch exceptions from Storage upload OR the re-thrown DB error
            Log.e("UserRepository", "Error updating profile image for user $uid", e)
            ResultWrapper.Error(e)
        }
    }


    override fun getUserDisplayInfo(): UserDisplayInfo {
        val currentUser = _currentUser.value
        return if (currentUser != null) {
            UserDisplayInfo(
                displayName = currentUser.displayName ?: "Unknown",
                profileImageUrl = currentUser.photoUrl?.toString() ?: ""
            )
        } else {
            UserDisplayInfo()
        }
    }



    /*todo
    // Update last active periodically
    viewModelScope.launch {
        while (true) {
            userRepository.updateUserLastActive()
            delay(5 * 60 * 1000) // Update every 5 minutes
        }
    }*/
     suspend fun updateUserLastActive() {
        val uid = try { getCurrentUserId() } catch (e: Exception) { return } // Fail silently if not logged in

        try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(uid)
            // Use Firestore server timestamp for accuracy and consistency
            userDocRef.update(User.LAST_ACTIVE, FieldValue.serverTimestamp()).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to update last active time for user $uid", e)
        }
    }


}