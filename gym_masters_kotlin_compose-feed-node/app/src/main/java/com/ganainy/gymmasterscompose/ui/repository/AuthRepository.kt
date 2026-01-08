package com.ganainy.gymmasterscompose.ui.repository

import com.ganainy.gymmasterscompose.ui.models.User
import com.ganainy.gymmasterscompose.ui.models.User.Companion.USERS_COLLECTION
import com.ganainy.gymmasterscompose.utils.Utils.generateRandomUsername
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.ganainy.gymmasterscompose.ui.retrofit.AuthApi
import android.util.Log
import com.ganainy.gymmasterscompose.ui.models.UserCreationResponse
import com.ganainy.gymmasterscompose.ui.models.UserCreationRequest
import com.ganainy.gymmasterscompose.ui.models.UserLoginRequest

// 接口定义
// IAuthRepository 接口定义了身份验证操作的合约。
// 它指定了六个方法：用户登录、登出、获取当前用户 ID、创建用户身份验证、创建用户资料和通过 Flow 检查登录状态。
interface IAuthRepository {
    suspend fun signInUser(email: String, password: String): ResultWrapper<FirebaseUser>
    suspend fun signOut(): ResultWrapper<Unit>
    fun getCurrentUserId(): String
    suspend fun createUserAuth(email: String, password: String): Result<FirebaseUser>
    suspend fun createUserProfile(
        email: String,
        displayName: String,
    ): ResultWrapper<User>
    fun isUserLoggedIn(): Flow<Boolean>
    suspend fun createUser(email: String, password: String): ResultWrapper<String>
    suspend fun UserSignIn(email: String, password: String): ResultWrapper<String>
//    @POST("api/user/create")
//    suspend fun createUser(@Body userCreationRequest: UserCreationRequest): Response<UserCreationResponse>

}

// AuthRepository 类通过依赖注入实现了接口，使用。
// 它通过构造函数注入接收 FirebaseAuth 和 FirebaseFirestore 实例。
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authApi: AuthApi,
) : IAuthRepository {

//    data class UserCreationRequest(
//        val email: String,
//        val password: String
//    )

override suspend fun UserSignIn(email: String, password: String): ResultWrapper<String>{
    val request = UserLoginRequest(username = email, password = password, client = "android" )

    return try {
        val response = authApi.userLogin(request)  // 发送 HTTP POST 请求

        // 打印响应的代码和消息
        Log.d("AuthApi", "Response Code: ${response.code()}")
        Log.d("AuthApi", "Response Message: ${response.message()}")

        if (response.isSuccessful) {
            val userLogInResponse = response.body()
            if (userLogInResponse != null) {
                ResultWrapper.Success(userLogInResponse.message) // 将成功消息返回
            } else {
                ResultWrapper.Error(Exception("Response body is null"))
            }
        } else {
            // 处理错误消息的情况
            ResultWrapper.Error(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e("AuthApi", "Error during user creation", e)
        ResultWrapper.Error(e)  // 返回遇到的异常
    } as ResultWrapper<String>

}


    override suspend fun createUser(email: String, password: String): ResultWrapper<String>{
        val request = UserCreationRequest(email = email, password = password)

        return try {
            val response = authApi.createUserInfo(request)  // 发送 HTTP POST 请求

            // 打印响应的代码和消息
            Log.d("AuthApi", "Response Code: ${response.code()}")
            Log.d("AuthApi", "Response Message: ${response.message()}")

            if (response.isSuccessful) {
                val userCreationResponse = response.body()
                if (userCreationResponse != null) {
                    ResultWrapper.Success(userCreationResponse.message) // 将成功消息返回
                } else {
                    ResultWrapper.Error(Exception("Response body is null"))
                }
            } else {
                // 处理错误消息的情况
                ResultWrapper.Error(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("AuthApi", "Error during user creation", e)
            ResultWrapper.Error(e)  // 返回遇到的异常
        } as ResultWrapper<String>
    }



    //getCurrentUserId 方法（第 39-42 行）
    //此方法返回当前已认证用户的。如果没有用户认证，则抛出自定义异常。

    override fun getCurrentUserId(): String {
        Log.d("AuthRepository", "override fun getCurrentUserId()")
//        return auth.currentUser?.uid
//            ?: throw UserNotAuthenticatedException("User is not authenticated")
        return "123123"
    }

    // Custom Exception
    // 自定义异常类（第44-45行）
    // 定义了一个自定义异常类 UserNotAuthenticatedException，用于处理未认证用户场景
    class UserNotAuthenticatedException(message: String) : Exception(message)

    // createUserAuth 方法
    // 此挂起函数处理使用电子邮件和密码创建 Firebase 用户。
    // 它使用 Firebase 的 createUserWithEmailAndPassword 方法，并返回 Kotlin Result 类型以进行错误处理。
    override suspend fun createUserAuth(
        email: String, password: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let { firebaseUser ->
                Result.success(firebaseUser)
            } ?: Result.failure(Exception("User creation successful but user is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // createUserProfile 方法
    // 此方法在成功认证后在 Firestore 中创建用户资料。
    // 它使用生成的用户名、电子邮件、显示名称和时间戳构建一个 User 对象，然后使用自定义的 ResultWrapper 模式将其保存到 Firestore。
    override suspend fun createUserProfile(
        email: String, displayName: String
    ): ResultWrapper<User> {
        return try {
            // Create the User object
            val user = User(
                username = generateRandomUsername(),
                email = email,
                displayName = displayName,
                joinDate = com.google.firebase.Timestamp.now(),
                id = auth.currentUser?.uid ?: throw Exception("User ID not found"),
                profilePictureUrl = null,
                bio = null,
                lastActive = null,
            )

            // Get Firestore document reference
            val userDocRef = firestore.collection(USERS_COLLECTION).document(user.id)

            // Save the User object to Firestore
            userDocRef.set(user).await()

            // Return the success result with the User object
            ResultWrapper.Success(user)
        } catch (e: Exception) {
            // Handle any exception that occurs while saving the user to Firestore
            ResultWrapper.Error(e)
        }
    }



    // 此方法返回一个观察认证状态变化的 Flow。
    // 它使用 callbackFlow 创建一个根据用户认证状态发出布尔值的响应式流。
    override fun isUserLoggedIn(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // 登录方法处理使用电子邮件和密码的用户身份验证
    // 它包括对无效用户和无效凭证场景的特定异常处理，返回包含在 ResultWrapper 中的适当错误消息。
    override suspend fun signInUser(email: String, password: String): ResultWrapper<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let { firebaseUser ->
                ResultWrapper.Success(firebaseUser)
            } ?: ResultWrapper.Error(Exception("Sign in successful but user is null"))
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> ResultWrapper.Error(Exception("com.ganainy.gymmasterscompose.ui.theme.models.User not found"))
                is FirebaseAuthInvalidCredentialsException -> ResultWrapper.Error(Exception("Invalid credentials"))
                else -> ResultWrapper.Error(e)
            }
        }
    }

    // 最后一个方法处理用户登出操作。
    // 它使用 withContext 在 IO 分派器上执行，并将结果包装在 ResultWrapper 中以实现一致的错误处理。
    override suspend fun signOut(): ResultWrapper<Unit> {
        return withContext(Dispatchers.IO) {
             try {
                auth.signOut()
                 ResultWrapper.Success(Unit)
            } catch (e: Exception) {
                 ResultWrapper.Error(e)
            }
        }
    }

}