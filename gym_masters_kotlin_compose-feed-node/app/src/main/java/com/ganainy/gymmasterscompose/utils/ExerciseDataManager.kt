package com.ganainy.gymmasterscompose.utils

import android.util.Log
import com.ganainy.gymmasterscompose.di.IoDispatcher
import com.ganainy.gymmasterscompose.ui.models.BodyPart
import com.ganainy.gymmasterscompose.ui.models.Equipment
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.TargetMuscle
import com.ganainy.gymmasterscompose.ui.repository.IExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

//data class CachedExerciseDataResult：定义一个数据类，用于存储缓存的锻炼数据结果。
//val exercises: List<Exercise>：一个 Exercise 对象的列表，表示从缓存中获取的所有锻炼数据。
//val bodyParts: List<BodyPart>：一个 BodyPart 对象的列表，表示锻炼涉及的身体部位。
//val targets: List<TargetMuscle>：一个 TargetMuscle 对象的列表，表示锻炼的目标肌肉。
//val equipment: List<Equipment>：一个 Equipment 对象的列表，表示所需的器材。
data class CachedExerciseDataResult(
//    val exercises: List<Exercise>,
    val exercises: List<StaticExercise>,
    val bodyParts: List<BodyPart>,
    val targets: List<TargetMuscle>,
    val equipment: List<Equipment>,
    val staticExercises: List<StaticExercise>
)

// Shared utility class for exercise-related operations

// class ExerciseDataManager：定义一个负责操作锻炼数据的类。
// @Inject constructor(...)：使用 Dagger Hilt 进行依赖注入。
// private val exerciseRepository: IExerciseRepository：
// 注入一个实现了 IExerciseRepository 接口的对象，用于访问锻炼相关的数据。
// @IoDispatcher private val ioDispatcher: CoroutineDispatcher：
// 注入一个 CoroutineDispatcher，用于在 IO 线程执行任务，确保不会阻塞主线程。
class ExerciseDataManager @Inject constructor(
    private val exerciseRepository: IExerciseRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    // suspend fun loadCachedExerciseData()：定义一个挂起函数，异步加载缓存的锻炼数据，
    // 返回一个 Result<CachedExerciseDataResult> 类型的结果。
    //= coroutineScope { ... }：使用 coroutineScope 创建一个新的协程作用域，确保挂起函数内部的所有协程在调用结束之前均被挂起。
    suspend fun loadCachedExerciseData(): Result<CachedExerciseDataResult> = coroutineScope {
        //并行获取缓存数据
        try {
            // Fetch all cached data concurrently
            // try { ... }：使用 try-catch 块捕获可能的异常。
            // val exercisesDeferred 等：使用 async 在后台并发调用数据仓库的多个方法，
            // val exercisesDeferred = async { ... }：使用 async 并发执行多个数据库查询，通过 exerciseRepository 获取缓存的数据。这种方式提升了 I/O 效率。
            // 获取缓存的锻炼数据、身体部位、目标和设备。返回的结果是一个 Deferred 对象，可以在稍后的时间点获得结果。
            val exercisesDeferred = async { exerciseRepository.getCachedExercises() }
            val bodyPartsDeferred = async { exerciseRepository.getCachedBodyParts() }
            val targetsDeferred = async { exerciseRepository.getCachedTargets() }
            val equipmentDeferred = async { exerciseRepository.getCachedEquipment() }
            val staticExercisesDeferred =  async { exerciseRepository.getCachedStaticExercises() }


            // Await results
            // val exercisesResult 等：使用 await() 方法等待所有并发任务完成，并获取结果。如果任何一个任务失败，会抛出异常。
            val exercisesResult = exercisesDeferred.await()
            val bodyPartsResult = bodyPartsDeferred.await()
              val targetsResult = targetsDeferred.await()
            val equipmentResult = equipmentDeferred.await()
            val staticExercisesResult = staticExercisesDeferred.await()

            // Check for errors in any fetch
            // listOf(...)：将所有结果放入一个列表中。
            // filterIsInstance<ResultWrapper.Error>()：过滤出所有的错误结果。
            // firstOrNull()?.let { errorResult -> ... }：如果表中有错误，获取第一个错误并返回一个失败的结果，传递错误异常。
            listOf(exercisesResult, bodyPartsResult, targetsResult, equipmentResult)
                .filterIsInstance<ResultWrapper.Error>()
                .firstOrNull()?.let { errorResult ->
                    return@coroutineScope Result.failure(errorResult.exception)
                }

            // All successful, extract data (assuming Success type)
            // val exercises = ... 等：如果所有操作都成功，将结果转换为 ResultWrapper.Success，提取出数据部分。
            val exercises = (exercisesResult as ResultWrapper.Success).data
            val bodyParts = (bodyPartsResult as ResultWrapper.Success).data
              val targets = (targetsResult as ResultWrapper.Success).data
            val equipment = (equipmentResult as ResultWrapper.Success).data
            val staticExercises = (staticExercisesResult as ResultWrapper.Success).data

            Log.d("ExerciseDataManager", "CachedExerciseDataResult Success.")
            Result.success(CachedExerciseDataResult(exercises, bodyParts, targets, equipment, staticExercises))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //suspend fun filterExercises(...)：定义一个挂起函数，用于过滤锻炼列表。该方法接受：
    //exercises: List<Exercise>：要过滤的锻炼列表。
    //query: String = ""：搜索查询，默认为空字符串。
    //bodyPart, target, equipment：可选参数，分别用于过滤身体部位、目标肌肉和设备。
    suspend fun filterExercises(
        //表示要过滤的锻炼列表
        exercises: List<Exercise>,
        //一个可选的字符串参数，默认为空字符串，用于搜索锻炼名称。
        query: String = "",
        //可选参数，表示过滤时依据的身体部位，可以为 null。
        bodyPart: BodyPart? = null,
        //可选参数，表示按目标肌肉进行过滤，同样可以为 null。
        target: TargetMuscle? = null,
        //可选参数，表示按设备进行过滤，也可以为 null。
        equipment: Equipment? = null
    ):
            //withContext(ioDispatcher)：使用指定的调度器执行此挂起函数，确保在 IO 线程中进行操作。
            //这对于避免阻塞主线程及处理长时间运行操作相当重要。
            List<Exercise> = withContext(ioDispatcher) {
        //使用 filter 函数对传入的 exercises 列表进行过滤。
        //在过滤过程中，定义多个条件：
        //matchesBodyPart：如果 bodyPart 有值，检查锻炼的身体部位是否匹配；否则返回 true。
        //matchesTarget：与目标肌肉相似。
        //matchesEquipment：检查锻炼所需的设备。
        //matchesQuery：如果查询为空或锻炼名称包含查询，则返回 true。
        //只保留同时满足所有条件的锻炼项目。
        exercises.filter { exercise ->
            //val matchesBodyPart：检查锻炼的身体部位是否与传入的 bodyPart 相匹配。
            //bodyPart?.let {...}：如果 bodyPart 不为 null，检查 exercise.bodyPart 是否等于 bodyPart.name；
            //如果为 null，则结果默认为 true，表示不进行身体部位过滤。
            val matchesBodyPart = bodyPart?.let { exercise.bodyPart == it.name } ?: true
            //            val matchesTarget：与上述逻辑类似，检查锻炼目标是否与传入的 target 相匹配。
            //            结果也是在 target 为 null 时默认为 true。
            val matchesTarget = target?.let { exercise.target == it.name } ?: true
            //val matchesEquipment：使用相似的模式，检查锻炼的设备是否与传入的 equipment 匹配。
            //这里的逻辑也保证在 equipment 为 null 的时候返回 true。
            val matchesEquipment = equipment?.let { exercise.equipment == it.name } ?: true
            //val matchesQuery：检查搜索查询是否与锻炼名称匹配。
            //如果 query 字符串为空，说明不过滤，否则它会检查 exercise.name 是否包含 query 字符串，并忽略大小写（ignoreCase = true）。
            val matchesQuery = query.isEmpty() || exercise.name.contains(query, ignoreCase = true)

            //返回最终的布尔值，结合了所有过滤条件，允许的锻炼项目只有在所有条件满足时才会被保留下来。
            matchesBodyPart && matchesTarget && matchesEquipment && matchesQuery
        }
    }

    suspend fun filterStaticExercises(
        staticExercises: List<StaticExercise>,
        query: String = "",
        bodyPart: BodyPart? = null,
        target: TargetMuscle? = null,
        equipment: Equipment? = null
    ): List<StaticExercise> = withContext(ioDispatcher) {
        staticExercises.filter { staticExercise ->
//            val matchesBodyPart = bodyPart?.let { staticExercise.bodyPart == it.name } ?: true
//            val matchesTarget = target?.let { staticExercise.target == it.name } ?: true
//            val matchesEquipment = equipment?.let { staticExercise.equipment == it.name } ?: true
//            val matchesQuery = query.isEmpty() || staticExercise.name.contains(query, ignoreCase = true)
            val matchesBodyPart =  true
            val matchesTarget =  true
            val matchesEquipment =  true
            val matchesQuery = true
            matchesBodyPart && matchesTarget && matchesEquipment && matchesQuery
        }
    }
}


// Data class to hold exercise data results
// data class ExerciseDataResult：定义一个数据类，用于持有锻炼数据的综合结果。
// val bodyParts, val targets, val equipment, val exercises：分别表示身体部位、目标肌肉、设备和锻炼的列表。
data class ExerciseDataResult(
    val bodyParts: List<BodyPart>,
    val targets: List<TargetMuscle>,
    val equipment: List<Equipment>,
    val exercises: List<StaticExercise>
)