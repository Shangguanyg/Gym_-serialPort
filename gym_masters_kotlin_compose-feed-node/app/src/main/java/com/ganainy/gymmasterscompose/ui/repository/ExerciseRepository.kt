package com.ganainy.gymmasterscompose.ui.repository


import AndroidImageProcessor
import android.content.Context
import android.util.Log
import com.ganainy.gymmasterscompose.di.IoDispatcher
import com.ganainy.gymmasterscompose.prefs.ExerciseDownloadPrefs
import com.ganainy.gymmasterscompose.ui.models.BodyPart
import com.ganainy.gymmasterscompose.ui.models.Equipment
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.TargetMuscle
import com.ganainy.gymmasterscompose.ui.retrofit.ExerciseApi
import com.ganainy.gymmasterscompose.ui.room.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


/*get exercises from the api at https://exercisedb.p.rapidapi.com*/
// The ExerciseRepository.kt 文件作为 Gym Masters 应用中管理锻炼相关数据的综合数据仓库。
// 它充当远程锻炼 API（来自 exercisedb.p.rapidapi.com）和本地 Room 数据库之间的桥梁，实现了具有离线支持的缓存策略

// 接口定义
// 该仓库实现了 IExerciseRepository 接口，
// 该接口定义了所有用于锻炼数据管理的操作，包括获取锻炼、身体部位、设备、目标肌肉以及管理本地保存。
//interface IExerciseRepository: 定义了一个接口，代表获取锻炼资料的逻辑。

//suspend fun getExercises(...): 声明一个挂起函数 (suspend function)，允许在协程内部被调用；参数 limit 和 offset 用于实现分页获取锻炼数据。
//返回值类型: ResultWrapper<List<Exercise>>? 表示返回一个结果包装，里面可能包含一个锻炼列表，可能返回 null。

//总结
//这段代码定义了一个名为 ExerciseRepository 的类，它实现了 IExerciseRepository 接口，负责管理与锻炼相关的数据访问。
//通过实现一系列方法，代码展示了如何使用网络 API 以及本地数据库（Room）来处理和缓存锻炼、身体部位、设备和目标肌肉的信息。
//核心功能：
//网络数据获取和缓存：提供多种方法从服务器获取数据并缓存到本地数据库，实现数据的高效访问。
//反复使用的模式：通过 fetchWithCache 泛型函数，简化了重复的网络和数据库操作，适应性强。
//流式数据观察：使用 Flow 机制来观察和发射数据变化，适合响应式编程。

interface IExerciseRepository {
    suspend fun getExercises(
        limit: Int? = null,
        offset: Int? = null
    ): ResultWrapper<List<StaticExercise>>?

    //后续的函数定义类似，都使用 suspend 修饰符，表示可以在协程中执行，且每个函数负责获取不同的锻炼数据或与锻炼相关的信息。
    suspend fun getExercisesByBodyPart(bodyPart: BodyPart): ResultWrapper<List<StaticExercise>>
    //获取所有身体部位的方法，返回一个 BodyPart 列表。
    suspend fun getBodyPartList(): ResultWrapper<List<BodyPart>>?
    //获取所有设备的方法，返回一个 Equipment 列表。
    suspend fun getEquipmentList(): ResultWrapper<List<Equipment>>?
    //获取目标肌肉列表的方法，返回一个 TargetMuscle 列表。
    suspend fun getTargetList(): ResultWrapper<List<TargetMuscle>>?
    //根据所提供的设备获取相关锻炼的方法。
    suspend fun getExercisesByEquipment(type: Equipment): ResultWrapper<List<StaticExercise>>
    //根据目标肌肉获取相关锻炼的方法。
    suspend fun getExercisesByTarget(target: TargetMuscle): ResultWrapper<List<StaticExercise>>
    //根据锻炼 ID 获取单个锻炼的方法，返回一个包装的单一 Exercise 对象。
    suspend fun getExerciseById(id: String): ResultWrapper<StaticExercise>
    //根据名称获取相关锻炼的方法，返回一个 Exercise 列表。
    suspend fun getExercisesByName(name: String): ResultWrapper<List<StaticExercise>>
    //切换给定锻炼的本地保存状态的方法。
    suspend fun toggleExerciseSaveLocally(exercise: StaticExercise): ResultWrapper<Unit>

    //观察特定锻炼对象的变化，返回一个 Flow 不断发出的 ResultWrapper<Exercise?>。
    suspend fun observeExercise(exerciseId: String): Flow<ResultWrapper<StaticExercise?>>
    //观察所有已保存锻炼的变化，返回一个 Flow 的结果。
    suspend fun observeSavedExercises(): Flow<ResultWrapper<List<StaticExercise>?>>

    /** Fetches all exercises directly from the local Room cache. */
    // 直接从本地 Room 缓存中获取所有练习
    // 从本地数据库缓存中获取所有锻炼的方法。
//    suspend fun getCachedExercises(): ResultWrapper<List<Exercise>>
    suspend fun getCachedExercises(): ResultWrapper<List<StaticExercise>>

    /** Fetches the body part list directly from the local Room cache. */
    //直接从本地 Room 缓存中获取身体部位列表
    suspend fun getCachedBodyParts(): ResultWrapper<List<BodyPart>>

    /** Fetches the equipment list directly from the local Room cache. */
    //直接从本地 Room 缓存中获取设备列表
    suspend fun getCachedEquipment(): ResultWrapper<List<Equipment>>

    /** Fetches the target muscle list directly from the local Room cache. */
    //直接从本地 Room 缓存中获取目标肌肉列表
    suspend fun getCachedTargets(): ResultWrapper<List<TargetMuscle>>

    //直接从本地 Room 缓存中获取目标肌肉列表
    suspend fun getCachedStaticExercises(): ResultWrapper<List<StaticExercise>>

    /**
     * Attempts to fetch all exercises from the API using pagination and cache them locally.
     * Checks a flag to prevent re-downloading after the first successful completion.
     *
     * @param forceRefresh If true, ignores the completion flag and attempts download again.
     * @return ResultWrapper indicating success (Unit) or failure. Success means the process finished,
     *         even if some pages failed (errors will be logged). Failure indicates a critical error.
     */
    //* 尝试使用分页从 API 获取所有练习，并将其缓存在本地。
    //* 检查标志，以防止在第一次成功完成后重新下载。
    //*
    //* @param forceRefresh 如果为 true，则忽略完成标志并尝试再次下载。
    //* @return ResultWrapper 指示成功（Unit）或失败。成功表示过程完成，
    //*                       即使某些页面失败（错误将被记录）。失败表示发生严重错误。
    suspend fun fetchAllExercisesAndCache(forceRefresh: Boolean = false): ResultWrapper<Unit>


}


//依赖注入
//具体实现使用了 Hilt 依赖注入，接收：
//AppDatabase 用于本地存储
//ExerciseApi 用于远程数据获取
//IoDispatcher 用于后台操作
//Context 用于图像处理
//ExerciseDownloadPrefs 用于跟踪下载完成

//@Inject: 表明这个类的构造函数可以被依赖注入框架（如 Dagger 或 Hilt）调用。
//依赖注入：注入了几个必要的构件：
//AppDatabase: Room 数据库实例，用于操作本地数据库。
//ExerciseApi: 用于与后端 API 通信的接口。
//CoroutineDispatcher: 用于执行 IO 操作的调度器，确保不会阻塞主线程。
//Context: Android 上下文，提供应用环境的信息。
//ExerciseDownloadPrefs: 用于存储下载偏好的类，管理下载的状态。
class ExerciseRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val exerciseApi: ExerciseApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val context: Context,
    private val exerciseDownloadPrefs: ExerciseDownloadPrefs
) : IExerciseRepository {

    //核心组件
    //该仓库包含几个关键组件：
    //缓存超时和分页的常量
    //CACHE_TIMEOUT: 设置缓存的过期时间为 24 小时，通常用于控制数据的时效性。
    //EXERCISE_PAGE_SIZE: 定义每次请求的最大锻炼数量，符合 API 的限制。
    // companion object：定义一个伴生对象，包含类的静态常量。
    // CACHE_TIMEOUT：定义一个缓存超时时间为 24 小时的常量。
    // EXERCISE_PAGE_SIZE：定义 API 允许的最大页面大小为 100。
    companion object {
        private const val CACHE_TIMEOUT = 24 * 60 * 60 * 1000L // 24 hours
        private const val EXERCISE_PAGE_SIZE = 100 // Max allowed by API
    }

    // AndroidImageProcessor 用于处理 GIF 到图像的转换
    // imageProcessor: 实例化一个处理器类，用于处理锻炼图像。
    val imageProcessor = AndroidImageProcessor(context)

    // 直接引用练习 DAO
    // exerciseDao: 获取 DAO 接口，用于直接与数据库进行交互。
    private val exerciseDao = appDatabase.exerciseDao()

    // Generic function to handle API calls with caching
    // 通用缓存策略
    // 该仓库通过 fetchWithCache 函数实现了复杂的缓存机制
    // 此函数：
    // 首先检查本地缓存
    //如果缓存为空或无效，则回退到网络调用
    //在保存前处理练习图像 ExerciseRepository.kt:98-114

    //优雅地处理错误，在可能的情况下返回缓存数据

    //描述: 这是一个通用方法，用于先从本地数据库查询数据，如果数据不存在或满足条件就从网络获取数据，再将获取到的数据保存到数据库中。
    //参数：
    //dbQuery: 从数据库查询数据的操作。
    //networkCall: 从网络获取数据的操作。
    //saveCallResult: 将获取的数据存入数据库的操作。
    //shouldFetch: 判断是否需要从网络获取数据的条件，默认是如果数据为 null 或者是一个空列表来决定。
    private suspend fun <T> fetchWithCache(
        dbQuery: suspend () -> T?,
        networkCall: suspend () -> T?,
        saveCallResult: suspend (T) -> Unit, // DAO operation passed in
        shouldFetch: (T?) -> Boolean = { it == null || (it is List<*> && it.isEmpty()) } // List check fixed
    ): ResultWrapper<T> = withContext(ioDispatcher) {
        try {
            //使用 withContext(ioDispatcher): 确保在指定的 IO 调度器上执行代码，避免阻塞主线程。
            //从数据库查询数据: 首先尝试查询本地缓存数据。
            //判断是否需要网络调用。
            //网络调用并处理: 通过网络获取数据，并在必要时处理锻炼的图像（通常是用来从 GIF URL 获取图像路径）。
            //保存数据到本地: 获取到处理后的结果，保存到数据库。
            //异常处理: 捕获异常以记录错误信息并尝试返回缓存数据作为后备。

            //val cachedData = dbQuery()：从数据库获取缓存的数据。
            val cachedData = dbQuery()
            //如果缓存的数据不为 null 且 shouldFetch 返回 false，则直接返回缓存数据的成功结果。
            if (cachedData != null && !shouldFetch(cachedData)) {
                return@withContext ResultWrapper.Success(cachedData)
            }

            //进行网络调用 networkCall()，并将结果赋值给 networkResult。
            //如果结果是 null，则返回一个表示网络获取失败的错误结果。
            val networkResult = networkCall() ?: // Return error immediately if network call returns null
            return@withContext ResultWrapper.Error(Exception("Failed to fetch data from network (result is null)"))


            // Process images for exercises *before* saving
            // 根据数据类型处理图像。这里检查 networkResult 是否为 List 类型。
            // 使用 filterIsInstance<Exercise>() 过滤出所有 Exercise 类型的实例，并处理每个锻炼项以获取图像路径。
            val processedResult = if (networkResult is List<*>) {
                val processedList = networkResult.filterIsInstance<StaticExercise>().map { exercise ->
                    val imagePath = processExerciseImageAndGetPath(exercise)
                    exercise.copy(screenshotPath = imagePath)
                }
                // Reconstruct result type if necessary (e.g., if networkResult wasn't just List<Exercise>)
                // This assumes networkResult IS List<Exercise> for simplicity here
                processedList as T
            }
            // 如果 networkResult 是 Exercise 类型，则获取其图像路径，并通过 copy 方法更新 screenshotPath。

            else if (networkResult is StaticExercise) {
                val imagePath = processExerciseImageAndGetPath(networkResult)
                networkResult.copy(screenshotPath = imagePath) as T
            }
            //如果不是上述类型，直接返回 networkResult。
            else {
                networkResult // No processing needed for non-exercise types
            }


            // Save the potentially processed result
            // 将处理后的结果调用 saveCallResult，它将执行具体的插入或更新操作。
            saveCallResult(processedResult) // Call the specific DAO operation (insert/update)

            // 返回一个表示成功永远结果的 ResultWrapper，包含处理后的结果。
            ResultWrapper.Success(processedResult) // Return processed result

        }
        // catch 块会处理所有异常。
        // 记录日志以提供异常信息和上下文。
        catch (e: Exception) {
            Log.e("ExerciseRepository", "Error in fetchWithCache", e)
            // Try returning cache even on network/processing error
            // 尝试在错误情况下再次获取缓存数据，使用 try-catch 以防止额外的错误。
            val cachedDataOnError = try { dbQuery() } catch (e: Exception) { null }
            // 如果缓存数据不为 null 且 shouldFetch 返回 false，则返回缓存数据的成功结果。
            // 否则，返回先前捕获的异常作为错误结果。
            if (cachedDataOnError != null && !shouldFetch(cachedDataOnError)) {
                ResultWrapper.Success(cachedDataOnError)
            } else {
                ResultWrapper.Error(e)
            }
        }
    }

    //直接缓存访问
    //该仓库提供直接缓存访问方法，完全绕过网络调用，适用于快速离线访问之前缓存的文件。
    //override suspend fun getCachedExercises(): ResultWrapper<List<Exercise>>：重写接口 IExerciseRepository 中的 getCachedExercises 方法，
    // 该方法返回一个包装锻炼列表的结果。
    //= withContext(ioDispatcher) { ... }：在指定的协程调度器中执行该方法，确保在 IO 线程中执行，避免阻塞主线程。
    override suspend fun getCachedExercises(): ResultWrapper<List<StaticExercise>> = withContext(ioDispatcher) {
        //try { ... }：使用 try-catch 捕获可能的异常。
        //val exercises = exerciseDao.getAllExercises()：调用 DAO 的 getAllExercises() 方法，直接查询 Room 数据库，获取所有已缓存的锻炼记录。

        try {
           // val exercises = exerciseDao.getAllExercises() // Directly query Room
            val exercises = exerciseDao.getStaticExercises() // Directly query Room
            //如果查询成功，将结果封装在 ResultWrapper.Success 中返回，表示操作成功，并包含获取的锻炼列表。
            ResultWrapper.Success(exercises)
        }
        //如果查询失败，则捕获异常。
        //记录错误日志，指出在尝试从 Room 获取已缓存锻炼时发生了错误。
        //返回一个 ResultWrapper.Error，其中包含异常信息，以方便调用者处理。
        catch (e: Exception) {
            Log.e("ExerciseRepository", "Error getting cached exercises from Room", e)
            ResultWrapper.Error(e)
        }
    }

    // override suspend fun getCachedBodyParts(): ResultWrapper<List<BodyPart>>：
    // 此方法用于获取所有身体部位的缓存记录，并返回一个 ResultWrapper。
    override suspend fun getCachedBodyParts(): ResultWrapper<List<BodyPart>> = withContext(ioDispatcher) {
        try {
            val bodyParts = appDatabase.bodyPartListDao().getBodyPartList() ?: emptyList()
            ResultWrapper.Success(bodyParts)
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Error getting cached body parts from Room", e)
            ResultWrapper.Error(e)
        }
    }

    override suspend fun getCachedStaticExercises(): ResultWrapper<List<StaticExercise>> = withContext(ioDispatcher) {
        try {
            val staticExercisesParts = exerciseDao.getStaticExercises() ?: emptyList()
            ResultWrapper.Success(staticExercisesParts)
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Error getting cached body parts from Room", e)
            ResultWrapper.Error(e)
        }
    }

    override suspend fun getCachedEquipment(): ResultWrapper<List<Equipment>> = withContext(ioDispatcher) {
        try {
            val equipment = appDatabase.equipmentDao().getAll() ?: emptyList()
            ResultWrapper.Success(equipment)
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Error getting cached equipment from Room", e)
            ResultWrapper.Error(e)
        }
    }

    override suspend fun getCachedTargets(): ResultWrapper<List<TargetMuscle>> = withContext(ioDispatcher) {
        try {
            val targets = appDatabase.targetDao().getAll() ?: emptyList()
            ResultWrapper.Success(targets)
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Error getting cached targets from Room", e)
            ResultWrapper.Error(e)
        }
    }

    // 批量数据下载
    // 功能: 从 API 获取所有锻炼信息，并缓存到本地，并且有强制刷新的选项。
    // 流程控制: 控制下载处理的详细逻辑，包括分页的请求，错误处理，图像处理，和数据库写入。
//    此方法的具体实现比较复杂，通常包括：
//    检查是否需要强制刷新，如果不是，则检查下载标志位是否已经完成。
//    进行分页请求，直到所有页面都被获取。
//    成功获取数据后，将结果写入数据库。

    // override suspend fun fetchAllExercisesAndCache(forceRefresh: Boolean): ResultWrapper<Unit>: 这是对接口 IExerciseRepository 中方法的实现。
    // 定义了一个挂起函数，传入一个布尔参数 forceRefresh 用于强制刷新。
    // withContext(ioDispatcher): 这个函数在指定的协程上下文中执行其代码块。ioDispatcher 通常用于处理阻塞的 IO 操作，确保不会阻塞主线程。
    // fetchAllExercisesAndCache 方法以有效的方式处理从 API 获取锻炼数据，并提供了必要的缓存和错误处理机制。
    // 通过分步骤的方式，它确保了在网络请求、数据库操作和图像处理中的稳健性和优雅的错误管理。
    // 这种实现方式提供了良好的用户体验和数据一致性。整个过程以日志记录的方式提供了清晰的执行路径，方便后期调试和监控。
    override suspend fun fetchAllExercisesAndCache(forceRefresh: Boolean): ResultWrapper<Unit> = withContext(ioDispatcher) {

        // 1. Check if download was already completed (unless forced)
        //检查是否需要强制刷新。如果没有强制刷新并且下载标志位表示下载已完成，则跳过下载。
        //Log.d(...): 记录日志信息，表明下载已完成并且将跳过这一过程。
        //return@withContext ResultWrapper.Success(Unit): 返回一个成功的结果，表示不需要进行任何操作，直接返回。
        if (!forceRefresh && exerciseDownloadPrefs.isInitialDownloadComplete()) {
            Log.d("ExerciseRepository", "Initial exercise download already completed. Skipping.")
            return@withContext ResultWrapper.Success(Unit)
        }
        // Optional: Check if DB has *any* data, maybe don't clear if resuming?
        // For simplicity, we assume a full download attempt each time until success flag is set.

        //Log.d(...): 记录开始下载过程的信息。
        //变量定义:
        //offset: 从 API 获取数据的偏移量，初始化为 0。
        //fetchMore: 用于控制循环继续获取更多数据。
        //page: 当前请求的页码，初始化为 1。
        //totalFetched: 记录已经获取的总数据量，初始化为 0。
        //lastPageError: 用于标记最后一页请求是否出现错误，避免在出现持久性错误时进入死循环。
        Log.d("ExerciseRepository", "Starting initial exercise download process...")
        var offset = 0
        var fetchMore = true
        var page = 1
        var totalFetched = 0
        var lastPageError = false // Flag to prevent infinite loop on persistent error

        //开始一个 while 循环： 只要 fetchMore 为真，就继续尝试获取数据。
        //Log.d(...): 记录当前请求的页码、偏移量和每页的限制数量。
        while (fetchMore) {
            Log.d("ExerciseRepository", "Fetching page $page (Offset: $offset, Limit: $EXERCISE_PAGE_SIZE)")
            try {
                // 2. Call API directly (bypass fetchWithCache)
                //try 块：开始捕捉可能出现的异常。
                //exerciseApi.getExercises(...): 调用 API 获取锻炼数据，并进行分页，传入当前的 limit 和 offset。

                //val exercisesFromApi = exerciseApi.getExercises(limit = EXERCISE_PAGE_SIZE, offset = offset)
                val exercisesFromApi = exerciseApi.getStaticExercisesList()

                //检查 API 返回：如果获取的数据为 null 或空列表。
                //Log.d(...): 记录获取数据为空的情况，假设已到达锻炼数据的末尾。
                //fetchMore = false: 若为空，设置 fetchMore 为 false，结束数据获取过程。

                if (exercisesFromApi != null) {
                    if (exercisesFromApi.data.isNullOrEmpty()) {
                        Log.d("ExerciseRepository", "Fetched empty list or null. Assuming end of exercises.")
                        fetchMore = false // Stop if API returns empty list
                    } else {
                        fetchMore = false
                                  // totalFetched += exercisesFromApi.data.size: 更新已获取的锻炼总数。
//                                //Log.d(...): 记录本页成功获取的锻炼数量及目前为止的总数。
                                totalFetched += exercisesFromApi.data.size
                                Log.d("ExerciseRepository", "Fetched ${exercisesFromApi.data.size} exercises for page $page. Total so far: $totalFetched")

                                // 3. Process images and prepare for DB insert
                                //map { .. }: 通过 map 函数处理 API 返回的锻炼列表。
                                //  图像路径处理: 尝试通过 imageProcessor.getImagePathFromGif(...) 从 GIF URL 获取图像路径，并处理可能的异常。
                                //  错误处理: 如果处理图像出现错误，记录该错误并设置 imagePath 为 null，继续处理下一个锻炼。
                                //  exercise.copy(...): 创建一个新的锻炼对象，设置其 screenshotPath 字段为之前获取的图像路径。
                                val exercisesToInsert = exercisesFromApi.data.map { exercise ->
                                    val imagePath = try {
                                        exercise.gifUrl = "https://pic1.imgdb.cn/item/68c5558f58cb8da5c8a7299c.jpg"
                                        imageProcessor.getImagePathFromGif(exercise.gifUrl)
                                    } catch (e: Exception) {
                                        Log.e("ExerciseRepository", "Error processing image for exercise ${exercise.id}", e)
                                        null // Continue without image if processing fails
                                    }
                                    exercise.copy(screenshotPath = imagePath) // Set path before insert
                                }
//
//                                // 4. Save the fetched batch to Room DB
//                                //保存数据: 尝试将处理后的锻炼列表插入到 Room 数据库中。
//                                //成功插入: 如果插入成功，记录日志显示插入的锻炼数量。
//                                //异常处理: 如果插入失败，记录错误信息并设置 lastPageError 为 true，以避免在数据库连续失败的情况下发生死循环。
                                try {
                                    //exerciseDao.insertAll(exercisesToInsert)
                                    exerciseDao.insertAllStaticExercise(exercisesToInsert)

                                    Log.d("ExerciseRepository", "Successfully inserted ${exercisesToInsert.size} exercises from page $page.")
                                } catch(dbError: Exception) {
                                    Log.e("ExerciseRepository", "Error inserting exercises from page $page into Room DB", dbError)
                                    // Decide if DB error is critical. Maybe continue? For now, let's continue.
                                    lastPageError = true // Avoid infinite loop if DB keeps failing
                                }

//                                // 5. Check if this was the last page
//                                // 检查是否为最后一页: 如果返回的数据大小小于允许的页大小，认为已到达所有锻炼的终点。
//                                //更新偏移量和页码: 如果未到达终点，更新偏移量和页数，为下一次请求做准备。
                                if (exercisesFromApi.data.size < EXERCISE_PAGE_SIZE) {
                                    Log.d("ExerciseRepository", "Fetched less than page size (${exercisesFromApi.data.size} < $EXERCISE_PAGE_SIZE). Assuming end of exercises.")
                                    fetchMore = false
                                } else {
                                    // Prepare for next iteration
                                    // 如果当前页没有结束，更新 offset 和 page 的值，以准备下次请求。
                                    // 重置 lastPageError 为 false，表示上次请求成功。
                                    offset += EXERCISE_PAGE_SIZE
                                    page++
                                    lastPageError = false // Reset error flag after successful page
                                    // Optional delay to avoid rate limiting
                                    // delay(200) // Delay for 200ms
                                }
                    }
                }

            } catch (e: Exception) {
                //捕捉异常: 如果在请求过程中发生异常，记录错误并进行后续处理。
                //重复错误处理: 如果在同一页发生的错误，设置 fetchMore 为 false，终止下载过程，并返回失败的结果。
                //继续处理: 如果不是持续发生的错误，设置 lastPageError 为 true，以便在错误恢复后重新尝试。

                Log.e("ExerciseRepository", "Error fetching exercises on page $page (Offset: $offset)", e)
                // Decide how to handle API errors: stop? retry page? skip page?
                // For robustness, let's log and stop the loop to avoid hammering a failing API.
                // Set a flag if the error persists on the same page.

                // 如果上次请求已经出错，记录错误并结束请求循环。
                // 否则，标记这次请求出错，允许下一次请求继续。
                // 这里还记录当前页的错误，并决定是否继续请求。
                if(lastPageError) {
                    Log.e("ExerciseRepository", "Repeated error fetching page $page. Aborting download.")
                    fetchMore = false // Abort on repeated error for the same page
                    return@withContext ResultWrapper.Error(Exception("Failed to fetch page $page after retry", e))
                } else {
                    lastPageError = true
                    // Optional: Add a longer delay before potentially retrying the same offset
                    // delay(1000)
                    // Or just break: fetchMore = false
                    Log.w("ExerciseRepository", "Continuing after error on page $page. Will retry offset $offset or stop if repeated.")
                    // Let's break here to be safe. User can retry manually if needed.
                    fetchMore = false
                    // 表示成功，返回 ResultWrapper.Success。
                    return@withContext ResultWrapper.Error(Exception("Failed to fetch page $page", e))

                }
            }
        } // end while

        // 6. Mark download as complete only if we exited the loop without a critical error ending it prematurely
        //终止条件: 如果下载循环正常完成（没有碰到持续性的错误），记录下载结果，
        // 并且通过 exerciseDownloadPrefs.setInitialDownloadComplete(true) 更新状态以标记下载已完成。
        //错误终止: 在遇到持续性错误的情况下，记录错误并返回一个错误结果，表示操作失败。
        if (!lastPageError) { // Check if loop completed normally or stopped due to empty page
            Log.d("ExerciseRepository", "Finished exercise download loop. Total fetched (approx): $totalFetched")
            exerciseDownloadPrefs.setInitialDownloadComplete(true)
            return@withContext ResultWrapper.Success(Unit)
        } else {
            Log.e("ExerciseRepository", "Exercise download loop aborted due to persistent error.")
            // Don't set the completion flag
            // Return the last error encountered if needed, or a generic error
            return@withContext ResultWrapper.Error(Exception("Exercise download aborted due to errors."))
        }
    }

    // 方法签名
    // override suspend fun getExercises(limit: Int?, offset: Int?): ResultWrapper<List<Exercise>>：
    // 重写接口中的 getExercises 函数，具有可选的 limit 和 offset 参数，并返回一个封装包含 Exercise 列表的结果
    // = fetchWithCache(...)：调用 fetchWithCache 泛型函数，以处理网络请求和缓存逻辑。
    override suspend fun getExercises(limit: Int?, offset: Int?): ResultWrapper<List<StaticExercise>> =
        fetchWithCache(
            //数据库查询
            //dbQuery = { exerciseDao.getExercisesPage(limit ?: 10, offset ?: 0) }：
            //这是一个 lambda 表达式，表示从数据库查询的操作。
            //使用 exerciseDao.getExercisesPage(...) 获取分段的锻炼列表，如果 limit 为 null，则默认为 10，offset 默认为 0。
            dbQuery = { exerciseDao.getExercisesPage(limit ?: 10, offset ?: 0) }, // Assuming a paginated query exists

            //网络请求
            //networkCall = { exerciseApi.getExercises(limit, offset) }：
            //这是另一个 lambda 表达式，用于定义网络请求。
            //调用 exerciseApi.getExercises(...) 获取数据，使用传入的 limit 和 offset。
            networkCall = { exerciseApi.getExercises(limit, offset) },

            //保存结果
            //saveCallResult = { exercises -> exerciseDao.insertAll(exercises as List<Exercise>) }：
            //使用 lambda 表达式指定如何将结果保存到数据库。
            //这将执行 insertAll 方法，将获取到的锻炼列表插入到 Room 数据库中。
            saveCallResult = { exercises -> exerciseDao.insertAll(exercises as List<StaticExercise>) } // Pass insertAll
        )

    // 方法签名
    // override suspend fun getExercisesByBodyPart(bodyPart: BodyPart):
    // ResultWrapper<List<Exercise>>：重写接口中的 getExercisesByBodyPart 函数，根据 BodyPart 获取相关锻炼数据，并返回一个封装的 Exercise 列表。
    override suspend fun getExercisesByBodyPart(bodyPart: BodyPart): ResultWrapper<List<StaticExercise>> =
        //fetchWithCache 方法调用
        //方法的整体结构和 getExercises 类似，只是在数据库查询和网络调用中传入了身体部位的名称。
        fetchWithCache(
            dbQuery = { exerciseDao.getExercisesByBodyPart(bodyPart.name) },
            networkCall = { exerciseApi.getExercisesByBodyPart(bodyPart.name) },
            saveCallResult = { exercises -> exerciseDao.insertAll(exercises as List<StaticExercise>) } // Pass insertAll
        )

    //方法签名
    //override suspend fun getBodyPartList(): ResultWrapper<List<BodyPart>>：重写接口中的 getBodyPartList 函数，用于获取身体部位的列表并返回。
    override suspend fun getBodyPartList(): ResultWrapper<List<BodyPart>> =
        //fetchWithCache 方法调用
        //查询部分直接从 DAO 获取身体部位的列表。
        //网络调用中，获取从 API 返回的身体部位列表并映射到 BodyPart 对象。
        //保存结果时，将获取的数据插入到数据库中的身体部位表。
        fetchWithCache(
            dbQuery = { appDatabase.bodyPartListDao().getBodyPartList() },
            networkCall = { exerciseApi.getBodyPartList().map { BodyPart(it) } },
            saveCallResult = { appDatabase.bodyPartListDao().insertBodyPartList(it as List<BodyPart>) }
        )

    // 获取设备列表的方法
    // 方法签名
    // override suspend fun getEquipmentList(): ResultWrapper<List<Equipment>>：重写接口中的 getEquipmentList 函数，用于获取设备列表并返回。
    override suspend fun getEquipmentList(): ResultWrapper<List<Equipment>> =
        //fetchWithCache 方法调用
        //使用 equipmentDao 的 getAll() 方法获取设备列表，进行网络请求获取设备数据并映射为 Equipment 对象。
        //将结果插入到设备数据表。
        fetchWithCache(
            dbQuery = { appDatabase.equipmentDao().getAll() },
            networkCall = { exerciseApi.getEquipmentList().map { Equipment(it) } },
            saveCallResult = { appDatabase.equipmentDao().insertAll(it as List<Equipment>) }
        )

    //获取目标肌肉列表的方法
    //方法签名
    //override suspend fun getTargetList(): ResultWrapper<List<TargetMuscle>>：重写接口中的 getTargetList，用于获取目标肌肉的列表并返回。
    override suspend fun getTargetList(): ResultWrapper<List<TargetMuscle>> =
        //fetchWithCache 方法调用
        //使用 targetDao 的 getAll() 方法获取目标肌肉。
        //网络调用时调用 API 并将返回映射为 TargetMuscle 对象，然后保存到数据库中。
        fetchWithCache(
            dbQuery = { appDatabase.targetDao().getAll() },
            networkCall = { exerciseApi.getTargetList().map { TargetMuscle(it) } },
            saveCallResult = { appDatabase.targetDao().insertAll(it as List<TargetMuscle>) }
        )

    //根据设备获取锻炼的方法
    //方法签名
    //override suspend fun getExercisesByEquipment(type: Equipment):
    //ResultWrapper<List<Exercise>>：重写接口中的 getExercisesByEquipment，根据设备获取相关锻炼并返回。
    override suspend fun getExercisesByEquipment(type: Equipment): ResultWrapper<List<StaticExercise>> =
        //fetchWithCache 方法调用
        //使用 exerciseDao.getExercisesByEquipment 查询锻炼，结合设备名称。
        //网络调用时调用 API 并保存返回的锻炼数据。
        fetchWithCache(
            dbQuery = { exerciseDao.getExercisesByEquipment(type.name) },
            networkCall = { exerciseApi.getExercisesByEquipment(type.name) },
            saveCallResult = { exercises -> exerciseDao.insertAll(exercises as List<StaticExercise>) }
        )

    //根据目标获取锻炼的方法
    //方法签名
    //override suspend fun getExercisesByTarget(target: TargetMuscle): ResultWrapper<List<Exercise>>：
    //重写接口中的 getExercisesByTarget，根据目标肌肉获取锻炼并返回。
    //fetchWithCache 方法调用
    //查询和保存的逻辑与前述方法类似，调用相应 DAO 和 API 方法。
    override suspend fun getExercisesByTarget(target: TargetMuscle): ResultWrapper<List<StaticExercise>> =
        fetchWithCache(
            dbQuery = { exerciseDao.getExercisesByTarget(target.name) },
            networkCall = { exerciseApi.getExercisesByTarget(target.name) },
            saveCallResult = { exercises -> exerciseDao.insertAll(exercises as List<StaticExercise>) }
        )

    //根据 ID 获取锻炼的方法
//    方法签名
//    override suspend fun getExerciseById(id: String): ResultWrapper<Exercise>：重写接口中根据 ID 获取锻炼的方法。
//    fetchWithCache 方法调用
//    利用 ID 查询单个锻炼并在需要时执行网络调用。
    override suspend fun getExerciseById(id: String): ResultWrapper<StaticExercise> =
        fetchWithCache(
            dbQuery = { exerciseDao.getExerciseById(id) },
            networkCall = { exerciseApi.getExerciseById(id) },
            saveCallResult = { exercise -> exerciseDao.insert(exercise as StaticExercise) } // Pass insert
        )

    //根据名称获取锻炼的方法
    //方法签名
    //override suspend fun getExercisesByName(name: String): ResultWrapper<List<Exercise>>：根据名称查询锻炼的方法。
    //fetchWithCache 方法调用
    //查询数据库或通过网络请求获取并保存锻炼数据。
    override suspend fun getExercisesByName(name: String): ResultWrapper<List<StaticExercise>> =
        fetchWithCache(
            dbQuery = { exerciseDao.getExercisesByName(name) },
            networkCall = { exerciseApi.getExercisesByName(name) },
            saveCallResult = { exercises -> exerciseDao.insertAll(exercises as List<StaticExercise>) }
        )

    //切换锻炼保存状态的方法
    //方法签名
    //override suspend fun toggleExerciseSaveLocally(exercise: Exercise): ResultWrapper<Unit>：重写接口方法，用于切换锻炼的本地保存状态。
    //切换逻辑
    //exercise.isSavedLocally = !exercise.isSavedLocally：反转锻炼的保存状态
    override suspend fun toggleExerciseSaveLocally(exercise: StaticExercise): ResultWrapper<Unit> {
        exercise.isSavedLocally = !exercise.isSavedLocally
        //尝试更新数据库中的锻炼状态，返回操作结果。
        return try {
            exerciseDao.update(exercise)
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    //观察单个锻炼的方法
    //功能: 观察锻炼变化的流，实时更新 UI。
    //使用 Flow: 以“流”的方式持续监听数据库中的数据变化。

    //方法签名
    //override suspend fun observeExercise(exerciseId: String): Flow<ResultWrapper<Exercise?>>：重写接口方法，用于观察特定锻炼的变化。
    //流的实现
    //使用 flow { ... } 创建一个流，在这个流内处理锻炼的观察。
    override suspend fun observeExercise(exerciseId: String): Flow<ResultWrapper<StaticExercise?>> {
        return flow {
            //通过 DAO 方法观察锻炼，使用 collect 收集数据并将结果发射为 Success。
            try {
                appDatabase.exerciseDao().observeExerciseById(exerciseId).collect { exercise ->
                    emit(ResultWrapper.Success(exercise))
                }
            }
            //捕获可能的异常并将错误发射为错误结果。
            catch (e: Exception) {
                emit(ResultWrapper.Error(e))
            }
        }
    }

    // 观察已保存锻炼的方法
//    方法签名
//    override suspend fun observeSavedExercises(): Flow<ResultWrapper<List<Exercise>?>：重写接口观察所有已保存锻炼的方法。
//    流的实现
//    使用 flow { ... } 创建一个流，在流内从 DAO 中观察已保存的锻炼。
    override suspend fun observeSavedExercises(): Flow<ResultWrapper<List<StaticExercise>?>> {
        return flow {
            //收集观察到的已保存锻炼数据，并将结果发射为 Success。
            try {
                appDatabase.exerciseDao().observeSavedExercises().collect { exercises ->
                    emit(ResultWrapper.Success(exercises))
                }
            }
            //捕获异常并发射错误结果。
            catch (e: Exception) {
                emit(ResultWrapper.Error(e))
            }
        }
    }

    // private suspend fun processExerciseImage(exercise: Exercise):
    // Exercise：定义一个私有的挂起函数，用于处理锻炼的图像，并返回更新后的锻炼对象。

    private suspend fun processExerciseImage(exercise: StaticExercise): StaticExercise {
        // Try to get image path, either from cache or by downloading gifUrl and image of it to disk
        // 调用图像处理器的 getImagePathFromGif 方法，处理 gifUrl 并获取图像路径。
        val imagePath = imageProcessor.getImagePathFromGif(exercise.gifUrl)

        //如果图像路径不为 null，则创建一个新的锻炼对象并更新图像路径。随后，将更新的锻炼保存到数据库，并返回更新后的锻炼对象。
        //如果处理失败，则直接返回原始锻炼对象。
        return if (imagePath != null) {
            val updatedExercise = exercise.copy(screenshotPath = imagePath)
            appDatabase.exerciseDao().update(updatedExercise)
            updatedExercise
        } else {
            exercise
        }
    }

    //处理图像和获取路径的方法
//    方法签名
//    private suspend fun processExerciseImageAndGetPath(exercise: Exercise): String?：一个私有的挂起函数，用于处理锻炼的图像并返回映射的图像路径。

    private suspend fun processExerciseImageAndGetPath(exercise: StaticExercise): String? {
        //尝试通过图像处理器获取图像路径。
        return try {
            imageProcessor.getImagePathFromGif(exercise.gifUrl)
        } catch (e: Exception) {
            //捕获异常并记录错误，将处理失败后的返回值设置为 null。
            Log.e("ExerciseRepository", "Error processing image for exercise ${exercise.id}", e)
            null
        }
    }


}