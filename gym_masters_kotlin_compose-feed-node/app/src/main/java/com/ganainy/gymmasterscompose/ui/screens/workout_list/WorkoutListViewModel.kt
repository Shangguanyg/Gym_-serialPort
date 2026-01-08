package com.ganainy.gymmasterscompose.ui.screens.workout_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.repository.ILikeRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.IWorkoutRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.ui.room.LikeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ganainy.gymmasterscompose.ui.models.ExerciseRecord
import kotlinx.coroutines.flow.first

//@HiltViewModel: 这个注解表明该类是一个 Hilt 管理的 ViewModel。Hilt 会自动处理依赖注入。
//构造函数:
//@Inject constructor(...): Hilt 在创建此 ViewModel 实例时自动提供依赖。
//private val workoutRepository: IWorkoutRepository: 注入锻炼相关的仓库，用于处理获取、上传和删除锻炼等操作。
//private val userRepository: IUserRepository: 用于处理用户相关的信息，例如获取当前用户 ID。
//private val likeRepository: ILikeRepository: 用于处理与点赞相关的业务逻辑。

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: IWorkoutRepository,
    private val userRepository: IUserRepository,
    private val likeRepository: ILikeRepository
) : ViewModel() {

    //    _uiData: 使用 MutableStateFlow 存储 ViewModel 的内部 UI 状态，初始值为 WorkoutListUiData 的空实例。这个变量将用于管理锻炼列表的状态数据。
    //    uiData: 将 _uiData 转换为只读的 StateFlow，供外部观察，以确保 UI 根据状态变化自动更新。
    //    核心状态通过一个私有的 MutableStateFlow 进行管理，该状态流持有 WorkoutListUiData
    private val _uiData = MutableStateFlow(WorkoutListUiData())
    var uiData = _uiData.asStateFlow()

    //init 块: 在 ViewModel 初始化时调用，设置初始状态。
    //loadWorkouts(...): 根据初始的排序类型加载锻炼列表数据。
    init {
        loadWorkouts(_uiData.value.sortType)

        // map { data -> ... }: 使用 map 转换 StateFlow 的内容，根据用户的搜索查询过滤锻炼列表：
        // if (data.searchQuery.isEmpty()): 如果搜索框为空，则返回原始数据。
        // filter { workoutWithStatus -> ... }: 如果搜索框不为空，则筛选出包含搜索查询的锻炼标题，忽略大小写。
        // stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkoutListUiData()):
        // 将结果转换为 StateFlow 并在 ViewModel 生命周期内分享状态，确保在订阅期间保持活动的流。

        // For local search filtering
        // Transform the uiState data state flow to filter workouts based on the search query.
        // If the search query is empty, return the original data. Otherwise, filter the workouts
        // whose titles contain the search query (case-insensitive).
        uiData = _uiData.asStateFlow()
            .map { data ->
                if (data.searchQuery.isEmpty()) {
                    data
                } else {
                    data.copy(
                        workoutWithStatusList = data.workoutWithStatusList.filter { workoutWithStatus ->
                            workoutWithStatus.workout.title.contains(
                                data.searchQuery,
                                ignoreCase = true
                            )
                        }
                    )
                }
            }
            //

            // Share the state flow while subscribed, with an initial value of an empty DiscoverData object.
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                WorkoutListUiData()
            )


        // Listen for sort changes (require new firebase call)
        //viewModelScope.launch: 在 ViewModel 的生命作用域内启动一个协程。
        //_uiData.map { it.sortType }: 从 _uiData 中提取当前的排序类型。
        //distinctUntilChanged(): 防止在相同排序类型不变时重复触发后续操作，只在变化时执行。
        //collect { sortType -> loadWorkouts(sortType) }: 收集排序类型，若发生变化时重新加载锻炼列表。
        viewModelScope.launch {
            _uiData
                .map { it.sortType }
                .distinctUntilChanged()
                .collect { sortType ->
                    loadWorkouts(sortType)
                }
        }


    }


    //    方法定义:
    //    toggleWorkoutLike(workout: Workout): 切换锻炼的点赞状态。
    //    viewModelScope.launch: 在 ViewModel 协程中执行操作。
    //    获取用户 ID: 从用户仓库中获取当前用户的 ID。
    //    调用点赞操作: 调用 likeRepository 切换喜欢状态。
    //    错误处理: 如果出现异常，则更新 UI 状态为错误状态。
    fun toggleWorkoutLike(workout: Workout) = viewModelScope.launch {
        try {
            val userId = userRepository.getCurrentUserId()
            likeRepository.toggleLike(
                targetId = workout.id,
                type = LikeType.WORKOUT,
                userId = userId,
            )
        } catch (e: Exception) {
            _uiData.update { it.copy(isLoading = false, error = e.message) }
        }
    }


    //方法定义:
    //toggleWorkoutSave(workout: Workout): 切换锻炼的保存状态。
    //获取用户 ID: 从用户仓库中获取当前用户的 ID。
    //调用保存操作: 调用 workoutRepository 的操作来保存锻炼状态，返回结果。
    //状态处理:
    //如果保存成功：
    //检查锻炼的保存状态。
    //根据状态选择是保存还是删除运动到本地的操作。
    fun toggleWorkoutSave(workout: Workout) = viewModelScope.launch {
        try {
            val userId = userRepository.getCurrentUserId()
            val result = workoutRepository.toggleWorkoutSave(workout, userId)

            if (result is ResultWrapper.Success) {
                Log.d("Workout", "result is ResultWrapper.Success in toggleWorkoutSave")
                val isSaved = workoutRepository.isWorkoutSavedByUser(
                    workout.id,
                    userId
                ) as? ResultWrapper.Success
                if (isSaved?.data == true) {
                    workoutRepository.saveWorkoutLocally(workout)
                } else {
                    workoutRepository.deleteWorkoutLocally(workout.id)
                }
            }
        } catch (e: Exception) {
            _uiData.update { it.copy(isLoading = false, error = e.message) }
        }
    }


    //挂起函数:
    //loadWorkouts(sortType: SortType): 加载锻炼列表，并根据排序类型进行处理。
    //日志记录: 打印开始加载锻炼集合的调试信息。
    //更新加载状态: 将 isLoading 设置为 true，清除之前的错误信息。
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadWorkouts(sortType: SortType) = viewModelScope.launch {
        try {
            Log.d("Workout", "Starting workout collection")
            _uiData.update { it.copy(isLoading = true, error = null) }
            // Get user ID once
            // 获取用户 ID: 调用用户仓库的方法，只需获取一次当前用户 ID。
            val userId = userRepository.getCurrentUserId()

            // Get workouts flow and convert to StateFlow
            // 获取锻炼数据流: 调用锻炼仓库的方法并获得按类型排序的锻炼流，转换为 StateFlow，保持其在 ViewModel 生命周期内。
            // 状态流管理: SharingStarted.WhileSubscribed(5000): 当有至少一个订阅者时共享，并在没有订阅者时在 5 秒后关闭。
            Log.d("Workout", "run to workoutRepository.getWorkouts(sortType)")
            val workoutsFlow = workoutRepository.getSelfWorkouts()
            workoutsFlow
                .flatMapLatest { workoutResult ->
                    when (workoutResult) {
                        is ResultWrapper.Success -> processWorkouts(workoutResult.data, userId)
                        is ResultWrapper.Error -> {
                            _uiData.update {
                                it.copy(
                                    isLoading = false,
                                    error = workoutResult.exception.message,
                                    workoutWithStatusList = emptyList()
                                )
                            }
                            flowOf(emptyList())
                        }
                        is ResultWrapper.Loading -> {
                            _uiData.update { it.copy(isLoading = true) }
                            flowOf(emptyList())
                        }
                    }
                }
                .collect { workoutWithStatusList ->
                    // 更新 UI state，使用 workoutWithStatusList
                    _uiData.update {
                        it.copy(
                            isLoading = false,
                            workoutWithStatusList = workoutWithStatusList,
                            sortType = sortType
                        )
                    }
                }




        } catch (e: Exception) {
            Log.e("WorkoutViewModel", "Error loading workouts", e)
            _uiData.update {
                it.copy(
                    isLoading = false,
                    error = e.message,
                    workoutWithStatusList = emptyList()
                )
            }
        }
    }


//    private fun processWorkouts(workouts: List<Workout>, userId: String): Flow<List<WorkoutWithStatus>> {
//        // Handle empty workout list case
//        if (workouts.isEmpty()) {
//            Log.d("Workout", "workouts.isEmpty() in processWorkouts")
//            return flowOf(emptyList())
//        }
//        //map: 为每个锻炼创建有关其状态的流。
//        //combine: 组合两个流的结果：
//        //likeRepository.observeLikeStatus(...): 观察特定锻炼的点赞状态流。
//        //流构造函数: 检查该锻炼是否被当前用户保存并发出结果。
//        // Create flows for each workout's status
//        val statusFlows = workouts.map { workout ->
//            combine(
//                likeRepository.observeLikeStatus(
//                    targetId = workout.id,
//                    type = LikeType.WORKOUT,
//                    userId = userId
//                ),
//                flow {
//                    val isSaved = (workoutRepository.isWorkoutSavedByUser(workout.id, userId)
//                            as? ResultWrapper.Success)?.data ?: false
//                    emit(isSaved)
//                }
//            ) { isLiked, isSaved ->
//                WorkoutWithStatus(
//                    workout = workout,
//                    isLiked = isLiked,
//                    isSaved = isSaved
//                )
//            }
//        }
//        //return combine(...): 将所有状态流结合在一起形成一个流列表，代表锻炼与其状态（是否被点赞、是否被保存）。
//        return combine(statusFlows) { it.toList() }
//    }

        //processWorkouts(...): 处理锻炼列表并与用户 ID 结合，生成流。
        //处理空列表情况: 如果没有锻炼，则返回一个空列表的流。
    private fun processWorkouts(workouts: List<Workout>, userId: String): Flow<List<WorkoutWithStatus>> {
        // Handle empty workout list case
        if (workouts.isEmpty()) {
            Log.d("Workout", "workouts.isEmpty() in processWorkouts")
            return flowOf(emptyList())
        }
        //map: 为每个锻炼创建有关其状态的流。
        //combine: 组合两个流的结果：
        //likeRepository.observeLikeStatus(...): 观察特定锻炼的点赞状态流。
        //流构造函数: 检查该锻炼是否被当前用户保存并发出结果。
        // Create flows for each workout's status
        val statusFlows = workouts.map { workout ->
            combine(
                likeRepository.observeLikeStatus(
                    targetId = workout.id,
                    type = LikeType.WORKOUT,
                    userId = userId
                ),
                flow {
                    val isSaved = (workoutRepository.isWorkoutSavedByUser(workout.id, userId)
                            as? ResultWrapper.Success)?.data ?: false
                    emit(isSaved)
                }
            ) { isLiked, isSaved ->
                WorkoutWithStatus(
                    workout = workout,
                    isLiked = isLiked,
                    isSaved = isSaved
                )
            }
        }
        //return combine(...): 将所有状态流结合在一起形成一个流列表，代表锻炼与其状态（是否被点赞、是否被保存）。
        return combine(statusFlows) { it.toList() }
    }




    //updateSearchQuery(query: String): 更新用户输入的搜索查询，并将其应用于 UI 状态数据。
    fun updateSearchQuery(query: String) {
        _uiData.update { it.copy(searchQuery = query) }
    }


    //retry(): 调用之前定义的 loadWorkouts 方法重新加载锻炼列表，通常在出现错误或网络问题时使用。
    fun retry() {
        loadWorkouts(_uiData.value.sortType)
    }

    //updateSortPreference(sortType: SortType): 更新用户选择的锻炼列表排序方式。
    fun updateSortPreference(sortType: SortType) {
        _uiData.update { it.copy(sortType = sortType) }
    }
}

/*
* Data class to store the status of a workout (liked, saved) along with the workout object.
*/
// 数据类定义: 将锻炼对象与其状态（是否点赞、是否保存）结合，以便在 UI 中高效展示。
data class WorkoutWithStatus(
    val workout: Workout,
    val isLiked: Boolean,
    val isSaved: Boolean
)

data class ExerciseRecordWithStatus(
    val exerciseRecord: ExerciseRecord,
    val isLiked: Boolean,
    val isSaved: Boolean
)

//枚举定义: 定义不同类型的排序选项，提供给用户选择。
enum class SortType {
    NEWEST,
    MOST_LIKED,
    MOST_SAVED
}

//数据类定义: 封装了锻炼列表 UI 状态，包括多个属性提供锻炼的完整信息。
//属性:
//workoutWithStatusList: 存储当前可用锻炼的列表。
//localWorkouts: 存储本地锻炼。
//searchQuery: 当前搜索查询。
//sortType: 当前用户选择的排序方式。
//isLoading: 表示当前是否正在加载。
//error: 存储可能的错误信息。
data class WorkoutListUiData(
    val workoutWithStatusList: List<WorkoutWithStatus> = emptyList(),
//    val exerciseRecordWithStatus: List<ExerciseRecordWithStatus> = emptyList(),
    val localWorkouts: List<Workout> = emptyList(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.NEWEST,
    val isLoading: Boolean = false,
    val error: String? = null
)

