package com.ganainy.gymmasterscompose.ui.repository

import android.net.Uri
import android.util.Log
import com.ganainy.gymmasterscompose.Constants
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutEntity.Companion.WORKOUTS_COLLECTION
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutEntity.Companion.WORKOUTS_METRICS
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutEntity.Companion.WORKOUTS_TIMESTAMP
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutEntity.Companion.WORKOUT_LIKES_COUNT
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutEntity.Companion.WORKOUT_SAVE_COUNT
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutLike
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutLike.Companion.WORKOUT_LIKES_COLLECTION
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutSave
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutSave.Companion.WORKOUT_SAVES_COLLECTION
import com.ganainy.gymmasterscompose.ui.models.workout.toWorkout
import com.ganainy.gymmasterscompose.ui.models.workout.toWorkoutEntity
import com.ganainy.gymmasterscompose.ui.room.AppDatabase
import com.ganainy.gymmasterscompose.ui.screens.workout_list.SortType
import com.ganainy.gymmasterscompose.utils.Utils.generateRandomId
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.core.net.toUri
import com.ganainy.gymmasterscompose.ui.retrofit.ExerciseApi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.ganainy.gymmasterscompose.ui.models.ExerciseRecord

// Workout and exercise management
// interface IWorkoutRepository: 定义了一个接口，列出了仓库应实现的所有功能。
interface IWorkoutRepository {
    suspend fun getWorkout(workoutId: String): Workout?
    suspend fun uploadWorkoutWithImage(workout: Workout, imagePath: String): ResultWrapper<Unit>
    suspend fun deleteWorkout(workoutId: String): ResultWrapper<Unit>
    suspend fun isWorkoutLikedByUser(workoutId: String, userId: String): ResultWrapper<Boolean>
    suspend fun isWorkoutSavedByUser(workoutId: String, userId: String): ResultWrapper<Boolean>
    suspend fun toggleWorkoutSave(workout: Workout, userId: String): ResultWrapper<Unit>
    suspend fun getWorkouts(sortType: SortType, limit: Int = 10): Flow<ResultWrapper<List<Workout>>>
    suspend fun deleteWorkoutCoverImage(imageUrl: String): ResultWrapper<Unit>
    suspend fun saveWorkoutLocally(workout: Workout): ResultWrapper<Unit>
    suspend fun deleteWorkoutLocally(workoutId: String): ResultWrapper<Unit>
    suspend fun getLocalWorkouts(): ResultWrapper<List<Workout>>
    suspend fun getLocalWorkoutsFlow(): Flow<ResultWrapper<List<Workout>>>
    suspend fun getSelfWorkouts(): Flow<ResultWrapper<List<Workout>>>
    fun getWorkoutFlow(workoutId: String): Flow<ResultWrapper<Workout?>>
}

// class WorkoutRepository: 实现了 IWorkoutRepository 接口的具体类。
//依赖注入:
//@Inject constructor(...): 使用 Hilt 处理依赖项的注入。
//private val firestore: FirebaseFirestore: Firestore 数据库的实例，用于与 Firebase 后端进行交互。
//private val storage: FirebaseStorage: Firebase 存储服务的实例，用于管理图片等数据的上传和下载。
//private val appDatabase: AppDatabase: Room 数据库的实例，用于管理本地存储。
//private val hashtagRepository: IHashtagRepository: 引入标签仓库，用于管理相关的标签操作。
class WorkoutRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val appDatabase: AppDatabase,
    private val hashtagRepository: IHashtagRepository,
    private val exerciseApi: ExerciseApi
) : IWorkoutRepository {

    // Firestore Collection References
    // 集合引用:
    // 通过 Firestore 获取与锻炼、点赞、保存等相关的集合引用，方便后续进行增删改查操作。
    private val workoutsCollection = firestore.collection(WORKOUTS_COLLECTION)
    private val workoutLikesCollection = firestore.collection(WORKOUT_LIKES_COLLECTION)
    private val workoutSavesCollection = firestore.collection(WORKOUT_SAVES_COLLECTION)

    //Method Signature:
    //getWorkout(workoutId: String): Workout?: 定义一个挂起的函数，接受一个锻炼 ID，并返回一个锻炼对象或 null。
    //try ... catch: 捕获可能发生的异常。
    //snapshot: 获取 Firestore 中指定文档的快照。
    //snapshot.exists(): 检查该文档是否存在。
    //toObject(Workout::class.java): 转换文档快照为 Workout 对象。
    //Log.w(...): 如果文档不存在，记录警告信息并返回 null。
    //Log.e(...): 如果捕获到异常，记录错误并返回 null。

    override suspend fun getWorkout(workoutId: String): Workout? {
        return try {
            val snapshot = workoutsCollection.document(workoutId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(Workout::class.java)
            } else {
                Log.w("WorkoutRepository", "Workout document $workoutId does not exist.")
                null
            }
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error fetching workout: $workoutId", e)
            null // Return null on error
        }
    }


    //Method Signature:
    //getWorkoutFlow(workoutId: String): Flow<ResultWrapper<Workout?>>: 定义一个返回流的函数，允许观察锻炼的变化。
    //callbackFlow { ... }: 创建一个可收集流，适用于异步事件。
    //val docRef = workoutsCollection.document(workoutId): 获取目标文档的引用。
    //ListenerRegistration?: 声明一个可选的监听器注册，用于控制 Firestore 监听器的生命周期。
    //try { listenerRegistration = ... }: 设置文档的快照监听器来监控更新。
    //addSnapshotListener { ... }: 添加方法以异步接收文档更新。
    override fun getWorkoutFlow(workoutId: String): Flow<ResultWrapper<Workout?>> = callbackFlow {
        Log.d("Workout", "getWorkoutFlow with workoutId $workoutId")
        val docRef = workoutsCollection.document(workoutId)
        var listenerRegistration: ListenerRegistration? = null
        try {
            listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
                //处理监听器的返回
                //if (error != null): 如果发生错误，返回错误信息。
                //if (snapshot != null && snapshot.exists()): 如果快照存在，则处理并发送成功结果。
                //trySend(ResultWrapper.Success(...)): 将成功的结果（解析后的 Workout 对象）发送到流中。
                //else: 如果文档不存在，发送空值包装对象。
                if (error != null) {
                    trySend(ResultWrapper.Error(error))
                    Log.e("Workout", "Error listening to workout $workoutId", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    try {
                        trySend(ResultWrapper.Success(snapshot.toObject(Workout::class.java)))
                    } catch (e: Exception) {
                        Log.e(
                            "Workout",
                            "Error converting workout snapshot $workoutId",
                            e
                        )
                        trySend(ResultWrapper.Error(Exception("Error parsing workout data", e)))
                    }
                } else {
                    // Document doesn't exist
                    trySend(ResultWrapper.Success(null))
                }
            }
        }
        //错误处理: 如果在设置监听器时发生异常，发送错误并关闭流。
        //awaitClose { ... }: 在关闭流时执行清理操作，确保文档的监听被取消。

        catch (e: Exception) {
            trySend(ResultWrapper.Error(e))
            close(e)
        }
        awaitClose {
            Log.d("Workout", "Removing listener for workout $workoutId")
            listenerRegistration?.remove()
        }
    }

    //方法签名:
    //uploadWorkoutWithImage(workout: Workout, imagePath: String): ResultWrapper<Unit>: 定义一个上传锻炼和图片的挂起函数，返回上传结果。
    override suspend fun uploadWorkoutWithImage(
        workout: Workout,
        imagePath: String
    ): ResultWrapper<Unit> {
        // 1. Handle image upload (no change needed here)
        // uploadWorkoutCoverImage(imagePath): 调用单独的方法上传封面图片。
        // 结果判断: 根据图片上传的结果返回相应的 URL，如果没有图像则返回一个空字符串的成功结果。
        val imageUrlResult = if (imagePath.isNotEmpty()) {
            uploadWorkoutCoverImage(imagePath)
        } else {
            ResultWrapper.Success("") // Empty string if no image
        }

        val imageUrl = when(imageUrlResult) {
            is ResultWrapper.Success -> imageUrlResult.data
            is ResultWrapper.Error -> return ResultWrapper.Error(imageUrlResult.exception) // Return early on upload failure
            is ResultWrapper.Loading -> return ResultWrapper.Error(Exception("Image upload still loading - unexpected"))
        }

        // 2. Prepare Workout data
        // 创建新的锻炼对象: 调用 copy 方法以生成一个包含图像 URL 的新 Workout 对象，同时将标签统一为小写，去除空白并去重。
        val workoutWithImage = workout.copy(
            imageUrl = imageUrl,
            // Normalize tags if needed
            tags = workout.tags.map { it.lowercase().trim() }.filter { it.isNotEmpty() }.distinct()
            // Ensure metrics are initialized if part of the model being saved
        )

        // 3. Prepare Firestore WriteBatch
        // firestore.batch(): 创建一个批量操作对象，允许在一次操作中执行多个读写请求。
        //batch.set(...): 将新的锻炼对象添加到批处理操作中，以便在 Firestore 存储。
        //添加标签更新: 调用标签仓库的 addHashtagUpdatesToBatch 方法，将相关的标签操作也添加到批处理操作中。
        //batch.commit().await(): 提交批量操作并等待完成。
        val batch = firestore.batch()
        try {
            // Define document reference
            //
            val workoutDocRef = workoutsCollection.document(workoutWithImage.id)

            // Add operations to batch:
            // a) Set the workout document
            batch.set(workoutDocRef, workoutWithImage)

            // b) Add hashtag updates using the refactored repository
            // 添加标签更新: 调用标签仓库的 addHashtagUpdatesToBatch 方法，将相关的标签操作也添加到批处理操作中。
            hashtagRepository.addHashtagUpdatesToBatch(batch, workoutWithImage.tags)

            // 4. Commit the batch
            //batch.commit().await(): 提交批量操作并等待完成。
            batch.commit().await()
            Log.d("WorkoutRepository", "Workout ${workoutWithImage.id} uploaded successfully.")
            return ResultWrapper.Success(Unit)

        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error committing workout upload batch for ${workoutWithImage.id}", e)
            // TODO:  deleting uploaded image if batch fails?
            return ResultWrapper.Error(e)
        }
    }

    /**
     * Uploads a workout cover image to Firebase Storage.
     *
     * This function uploads a workout cover image to Firebase Storage and returns the download URL of the uploaded image.
     * If the upload fails, it returns an error.
     *
     * @param imagePath The local file path of the cover image to be uploaded.
     * @return A ResultWrapper containing the download URL of the uploaded image if successful, or an error if the operation fails.
     */
    private suspend fun uploadWorkoutCoverImage(imagePath: String): ResultWrapper<String> {
        // Use a more specific path if desired, e.g., including workout ID if known beforehand
        val imageRef = storage.reference.child(Constants.WORKOUT_COVER_IMAGES)
            .child(generateRandomId(Constants.COVER_IMAGE)) // Or use workout ID + timestamp
        return try {
            imageRef.putFile(imagePath.toUri()).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            ResultWrapper.Success(downloadUrl)
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Failed to upload workout cover image: $imagePath", e)
            ResultWrapper.Error(e)
        }
    }


    override suspend fun deleteWorkout(workoutId: String): ResultWrapper<Unit> {
        // 1. Get workout data first to access tags and image URL
        val workout = getWorkout(workoutId) // Use the Firestore implementation
        if (workout == null) {
            Log.w("WorkoutRepository", "Cannot delete workout $workoutId: Not found.")
            return ResultWrapper.Error(Exception("Workout not found"))
        }

        val batch = firestore.batch()
        return try {
            // 2. Find associated likes and saves (consider doing this less frequently or via Functions)
            // This adds read cost to every delete.
            val likesQuery = workoutLikesCollection.whereEqualTo(WorkoutLike.WORKOUT_ID_FIELD, workoutId).get().await()
            val savesQuery = workoutSavesCollection.whereEqualTo(WorkoutSave.WORKOUT_ID_FIELD, workoutId).get().await()
            val likeRefsToDelete = likesQuery.documents.map { it.reference }
            val saveRefsToDelete = savesQuery.documents.map { it.reference }

            // 3. Define main document reference
            val workoutDocRef = workoutsCollection.document(workoutId)

            // 4. Add operations to batch:
            // a) Delete workout document
            batch.delete(workoutDocRef)

            // b) Delete associated likes
            likeRefsToDelete.forEach { batch.delete(it) }
            // c) Delete associated saves
            saveRefsToDelete.forEach { batch.delete(it) }
            Log.d("WorkoutRepository", "Prepared batch to delete ${likeRefsToDelete.size} likes and ${saveRefsToDelete.size} saves for workout $workoutId")


            // d) Decrement/delete hashtags using the refactored repository
            hashtagRepository.addHashtagDecrementToBatch(batch, workout.tags)

            // 5. Commit the batch
            batch.commit().await()
            Log.d("WorkoutRepository", "Workout $workoutId and associated data deleted from Firestore.")

            // 6. Delete cover image from Storage (after successful batch commit)
            if (workout.imageUrl.isNotEmpty()) {
                deleteWorkoutCoverImage(workout.imageUrl) // Call the existing storage delete method
            }

            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error deleting workout $workoutId", e)
            ResultWrapper.Error(e)
        }
    }




    /**
     * Deletes a workout's cover image from storage
     * @param imageUrl The URL of the image to delete
     * @return ResultWrapper indicating success or failure
     */
    override suspend fun deleteWorkoutCoverImage(imageUrl: String): ResultWrapper<Unit> {
        return try {
            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }


    override suspend fun isWorkoutLikedByUser(workoutId: String, userId: String): ResultWrapper<Boolean> {
        return try {
            val likeDocId = WorkoutLike.createId(userId, workoutId)
            val snapshot = workoutLikesCollection.document(likeDocId).get().await()
            ResultWrapper.Success(snapshot.exists())
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error checking like status for workout $workoutId by user $userId", e)
            ResultWrapper.Error(e)
        }
    }




    override suspend fun isWorkoutSavedByUser(workoutId: String, userId: String): ResultWrapper<Boolean> {
        return try {
//            val saveDocId = WorkoutSave.createId(userId, workoutId)
//            val snapshot = workoutSavesCollection.document(saveDocId).get().await()
//            ResultWrapper.Success(snapshot.exists())
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error checking save status for workout $workoutId by user $userId", e)
            ResultWrapper.Error(e)
        }
    }

    override suspend fun toggleWorkoutSave(workout: Workout, userId: String): ResultWrapper<Unit> {
        val saveDocId = WorkoutSave.createId(userId, workout.id)
        val batch = firestore.batch()
        return try {
            // Check current backend state
            Log.d("Workout", "try in toggleWorkoutSave")
            val isCurrentlySavedResult = isWorkoutSavedByUser(workout.id, userId)
            val isCurrentlySaved = when(isCurrentlySavedResult) {
                is ResultWrapper.Success -> isCurrentlySavedResult.data
                is ResultWrapper.Error -> throw isCurrentlySavedResult.exception // Propagate error
                is ResultWrapper.Loading -> throw IllegalStateException("isWorkoutSavedByUser returned Loading")
            }

            // Document references
            val saveDocRef = workoutSavesCollection.document(saveDocId)
            val workoutDocRef = workoutsCollection.document(workout.id)
            val metricsSaveCountPath = "$WORKOUTS_METRICS.$WORKOUT_SAVE_COUNT"

            if (isCurrentlySaved) {
                // --- Unsave Logic ---
                batch.delete(saveDocRef)
                batch.update(workoutDocRef, metricsSaveCountPath, FieldValue.increment(-1))
                Log.d("WorkoutRepository", "Prepared batch to UNSAVE workout $workout.id for user $userId")

            } else {
                // --- Save Logic ---
                val saveObject = WorkoutSave(
                    id = saveDocId,
                    userId = userId,
                    timestamp = Timestamp.now(),
                    workoutId = workout.id
                )
                batch.set(saveDocRef, saveObject)
                batch.update(workoutDocRef, metricsSaveCountPath, FieldValue.increment(1))
                Log.d("WorkoutRepository", "Prepared batch to SAVE workout $workout.id for user $userId")
            }

            // Commit the batch
            batch.commit().await()
            Log.d("WorkoutRepository", "Workout save toggled successfully for workout ${workout.id}")
            ResultWrapper.Success(Unit)

        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error toggling workout save for workout ${workout.id}", e)
            ResultWrapper.Error(e)
        }
    }


    /**
     * Deletes a workout from local storage
     * @param workoutId The ID of the workout to delete
     * @return ResultWrapper indicating success or failure
     */
    override suspend fun deleteWorkoutLocally(workoutId: String): ResultWrapper<Unit> {
        return try {
            appDatabase.workoutDao().deleteWorkoutById(workoutId)
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    /**
     * Retrieves all workouts stored locally
     * @return ResultWrapper containing list of locally stored workouts
     */
    override suspend fun getLocalWorkouts(): ResultWrapper<List<Workout>> {
        return try {
            val localWorkouts = appDatabase.workoutDao().getAllWorkouts().map { it.toWorkout() }
            ResultWrapper.Success(localWorkouts)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    override suspend fun getLocalWorkoutsFlow(): Flow<ResultWrapper<List<Workout>>> = callbackFlow {
        try {
            appDatabase.workoutDao().getAllWorkoutsFlow().collectLatest { workoutEntities ->
                workoutEntities.map { workoutEntity -> workoutEntity.toWorkout() }.also {
                    send(ResultWrapper.Success(it))
                }
            }
        } catch (e: Exception) {
            send(ResultWrapper.Error(e))
        }
    }

//    override suspend fun getSelfWorkouts(): ResultWrapper<List<ExerciseRecord>?> {
//        return try {
//            val localWorkouts  = exerciseApi.getDynamicsExercisesRecordList()?.data
//            ResultWrapper.Success(localWorkouts)
////            trySend(ResultWrapper.Success(localWorkouts))
//        } catch (e: Exception) {
//            ResultWrapper.Error(e)
//        }
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getSelfWorkouts(): Flow<ResultWrapper<List<Workout>>> = callbackFlow {
        // Emit loading state
        trySend(ResultWrapper.Loading())

        try {
            // 调用 API 获取数据
            val localWorkouts = exerciseApi.getDynamicsExercisesRecordList()?.data

            // 如果成功获取数据，发送成功的结果
            trySend(ResultWrapper.Success(localWorkouts ?: emptyList()))
        } catch (e: Exception) {
            // 如果发生异常，发送错误的结果
            trySend(ResultWrapper.Error(e))
        }

        // 关闭流
        awaitClose {
            // 可以在这里添加清理代码，例如取消任何相关操作
            Log.d("WorkoutRepository", "Self workouts flow closed.")
        }
    }

//    override suspend fun getSelfWorkouts(): Flow<ResultWrapper<List<ExerciseRecord>?>> = callbackFlow {
//        trySend(ResultWrapper.Loading())
//         try {
//            val localWorkouts  = exerciseApi.getDynamicsExercisesRecordList()?.data
//            trySend(ResultWrapper.Success(localWorkouts))
//        } catch (e: Exception) {
//            ResultWrapper.Error(e)
//        }
//    }

    //@OptIn(ExperimentalCoroutinesApi::class)：这是一个注解，表示这个函数使用了实验性的协程 API。开发者需要同意使用这些 API。
    //override suspend fun getWorkouts(...)：这是对接口方法的实现，表明这是一个挂起函数，能够在协程中调用。
    //sortType: SortType：表示排序类型的参数，可能是自定义的 SortType 枚举。
    //limit: Int：限制返回的最大锻炼数量。
    //Flow<ResultWrapper<List<Workout>>>：该方法返回一个 Flow 对象，包装了在流中的结果，可以是一个成功的锻炼列表或错误。
    //= callbackFlow { ... }：使用 callbackFlow 创建一个 Flow，允许发送数据和响应事件，结合回调处理。
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getWorkouts(sortType: SortType, limit: Int): Flow<ResultWrapper<List<Workout>>> = callbackFlow {

        Log.d("Workout", "override suspend fun getWorkouts")
        //        trySend(...)：用于发送一个值到 Flow，表示当前状态为加载中。
        //        ResultWrapper.Loading()：表示正在加载的状态，用于通知观察者。
        trySend(ResultWrapper.Loading()) // Emit loading state
        //var query: Query = workoutsCollection：声明一个变量 query，并将其初始化为 workoutsCollection。这个变量用于构建 Firestore 的查询。
       //初始化查询: 将 workoutsCollection 赋值给 query 变量，这个变量是用于构建查询的基础。
        var query: Query = workoutsCollection // Base query

        // Apply sorting based on sortType
        //         使用 when 表达式，根据 sortType 决定如何应用排序：
        //        SortType.NEWEST：按时间戳降序排序，最近的锻炼排在前面。
        //        SortType.MOST_LIKED：按喜欢计数降序排序，最受欢迎的锻炼排在前面。
        //        SortType.MOST_SAVED：按保存计数降序排序，被保存最多的锻炼排在前面。
        query = when (sortType) {
            SortType.NEWEST -> query.orderBy(WORKOUTS_TIMESTAMP, Query.Direction.DESCENDING)
            SortType.MOST_LIKED -> query.orderBy("$WORKOUTS_METRICS.$WORKOUT_LIKES_COUNT", Query.Direction.DESCENDING)
            SortType.MOST_SAVED -> query.orderBy("$WORKOUTS_METRICS.$WORKOUT_SAVE_COUNT", Query.Direction.DESCENDING)
        }

        // Apply limit
        // 应用限制: 将查询结果限制为传入的 limit 参数（转换为 Long 类型），确保不会返回超过指定数量的记录。
        query = query.limit(limit.toLong())

        //注册监听器: 创建一个可为空的 ListenerRegistration 变量，用于跟踪 Firestore 监听器的注册状态。
        var listenerRegistration: ListenerRegistration? = null
        //添加监听器: 使用 addSnapshotListener 监听查询结果的变化。snapshot 表示获取到的数据快照，error 表示可能发生的错误。
        try {
            Log.d("Workout", "trySend in getWorkouts")
            listenerRegistration = query.addSnapshotListener { snapshot, error ->
                // 错误处理: 如果发生错误，则发送 ResultWrapper.Error 到流中，并记录错误日志。
                // return@addSnapshotListener 表示如果有错误，停止执行当前监听器逻辑。
                if (error != null) {
                    trySend(ResultWrapper.Error(error))
                    Log.e("WorkoutRepository", "Error listening to workouts (sort: $sortType)", error)
                    return@addSnapshotListener
                }
                //快照处理: 当快照存在时，开始处理找到的文档列表。通过 mapNotNull 对每个文档执行操作，将非 null 的结果收集起来。
                if (snapshot != null) {
                    // 文档转换: 尝试将文档转换为 Workout 对象，捕获转换过程中的任何异常。
                    // 如果转换失败，记录错误并返回 null，从而确保只收集成功转换的文档。
                    val workouts = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(Workout::class.java) } catch (e: Exception) {
                            Log.e("WorkoutRepository", "Error converting workout doc ${doc.id}", e)
                            null
                        }
                    }
                    // No need to reverse here as Firestore ordering handles it
                    // 发送成功状态: 将成功获取的 workouts 列表发送到流中，使用 ResultWrapper.Success 表示成功状态。
                    Log.d("Workout", "override suspend fun getWorkouts")
                    trySend(ResultWrapper.Success(workouts))
                } else {
                    //快照为空处理: 如果快照为空，发送一个包含空列表的成功状态，表示没有找到锻炼数据。
                    trySend(ResultWrapper.Success(emptyList()))
                }
            }
        } catch (e: Exception) {
            // Error setting up the listener
            // 监听器设置错误处理: 如果在设置监听器时发生错误，捕获该异常，发送错误状态并关闭流。
            Log.d("Workout", "catch (e: Exception) in getWorkouts ")
            trySend(ResultWrapper.Error(e))
            close(e)
        }

        awaitClose {
            //等待关闭: awaitClose 用于挂起协程直到流的收集者关闭流。在这里，删除监听器并记录日志，表示监听器已被移除。
            Log.d("WorkoutRepository", "Removing listener for workouts (sort: $sortType)")
            listenerRegistration?.remove()
        }
    }


    /**
     * Saves a workout to local storage for offline access
     * @param workout The workout to save locally
     * @return ResultWrapper indicating success or failure
     */
    override suspend fun saveWorkoutLocally(workout: Workout): ResultWrapper<Unit> {
        return try {
            appDatabase.workoutDao().insertWorkout(workout.toWorkoutEntity())
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }


}

