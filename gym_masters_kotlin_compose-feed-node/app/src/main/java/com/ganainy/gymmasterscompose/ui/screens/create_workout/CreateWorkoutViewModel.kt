package com.ganainy.gymmasterscompose.ui.screens.create_workout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.Constants
import com.ganainy.gymmasterscompose.ui.models.BodyPart
import com.ganainy.gymmasterscompose.ui.models.Equipment
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.TargetMuscle
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutExercise
import com.ganainy.gymmasterscompose.ui.repository.IAuthRepository
import com.ganainy.gymmasterscompose.ui.repository.IWorkoutRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.ui.shared_components.ExerciseListItemData
import com.ganainy.gymmasterscompose.utils.ExerciseDataManager
import com.ganainy.gymmasterscompose.utils.Utils.extractHashtags
import com.ganainy.gymmasterscompose.utils.Utils.generateRandomId
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//The CreateWorkoutViewModel.kt 文件是一个全面的 Android ViewModel，
// 用于管理 Gym Masters 应用中创建自定义锻炼计划的州和业务逻辑。

//这个 ViewModel 展现了现代 Android 开发的最佳实践：
//
//清晰的架构分层：使用内部类组织相关功能
//响应式编程：自动观察状态变化并更新派生状态
//统一的错误处理：通过 handleOperation 统一管理异步操作
//完整的验证系统：确保数据完整性
//依赖注入：便于测试和解耦
//状态管理：使用 StateFlow 进行响应式状态管理
//该 ViewModel 是创建锻炼功能的核心，协调了数据加载、用户交互、验证和上传等所有业务逻辑。

// State Management
// ViewModel 采用结构化架构，使用密封接口和数据类进行状态管理。

//状态管理接口定义
//定义了 UiState 密封接口，包含
sealed interface UiState {
    //DataState 密封接口：定义了四种状态（Loading、WorkoutSetup、ExerciseListSetup、Error）
    sealed interface DataState {
        object Loading : DataState
        object WorkoutSetup : DataState
        object ExerciseListSetup : DataState
        data class Error(val message: String) : DataState
    }

    //WorkoutUiState 数据类：包含完整的UI状态，包括搜索查询、过滤器、练习列表、验证状态等
    data class WorkoutUiState(
        val workoutState: DataState = DataState.Loading,
        val searchQuery: String = "",
        val currentTag: String = "",
        val bodyPartFilter: BodyPart? = null,
        val equipmentFilter: Equipment? = null,
        val targetFilter: TargetMuscle? = null,
        val bodyPartList: List<BodyPart> = emptyList(),
        val equipmentList: List<Equipment> = emptyList(),
        val targetList: List<TargetMuscle> = emptyList(),
        //val exerciseList: List<Exercise> = emptyList(), //all exercises from api or local db
        val exerciseList: List<StaticExercise> = emptyList(), //all exercises from api or local db
        val availableDifficulties: List<String> = listOf(
            "Beginner",
            "Intermediate",
            "Advanced",
            "Expert"
        ),
        //val filteredExerciseList: List<Exercise> = emptyList(),
        val filteredExerciseList: List<StaticExercise> = emptyList(),
        val selectedExercise: WorkoutExercise? = null,
        var workout: Workout = Workout(
            id = generateRandomId(Constants.WORKOUT),
            difficulty = "Beginner"
        ),
        val validationState: ValidationState = ValidationState(),
        val showExerciseDialog: Boolean = false,
        val isUploadEnabled: Boolean = false,
        val showFilterSheet: Boolean = false,
    )

    //ValidationState 数据类：管理表单验证错误信息
    data class ValidationState(
        val setsError: String? = null,
        val repsError: String? = null,
        val restError: String? = null
    ) {
        val hasErrors: Boolean
            get() = setsError != null || repsError != null || restError != null
    }
}


//WorkoutValidator 对象实现了验证逻辑：
//
//定义了 ValidationRule 密封类和 NonZero 规则
//validate 方法检查 sets、reps 和 rest 字段是否为零
object WorkoutValidator {
    sealed class ValidationRule<T> {
        abstract fun validate(value: T): String?

        class NonZero : ValidationRule<Int>() {
            override fun validate(value: Int) =
                if (value == 0) "Value cannot be empty" else null
        }
    }

    private val rules = mapOf(
        "sets" to ValidationRule.NonZero(),
        "reps" to ValidationRule.NonZero(),
        "rest" to ValidationRule.NonZero()
    )
    //validate 方法检查 sets、reps 和 rest 字段是否为零
    fun validate(workoutExercise: WorkoutExercise): UiState.ValidationState {
        return UiState.ValidationState(
            setsError = rules["sets"]?.validate(workoutExercise.sets),
            repsError = rules["reps"]?.validate(workoutExercise.reps),
            restError = rules["rest"]?.validate(workoutExercise.restBetweenSets)
        )
    }
}

// ViewModel
// ViewModel 使用 @HiltViewModel 注解，并通过构造函数注入接收依赖，包括一个 ExerciseDataManager、IAuthRepository 和 IWorkoutRepository。

//ViewModel 类声明和依赖注入

//CreateWorkoutViewModel
//ViewModel 类使用 @HiltViewModel 注解，通过构造函数注入三个依赖：
//
//ExerciseDataManager：管理练习数据
//IAuthRepository：认证仓库
//IWorkoutRepository：锻炼仓库


@HiltViewModel
class CreateWorkoutViewModel @Inject constructor(
    private val exerciseDataManager: ExerciseDataManager,
    private val authRepository: IAuthRepository,
    private val workoutRepository: IWorkoutRepository,
) : ViewModel() {


    /**
     * Private state flow for the UI state.
     */
    //核心状态通过一个 MutableStateFlow 管理，该状态包含完整的 UI 状态，包括锻炼数据、运动列表、过滤器、验证状态和 UI 标志。
    private val _uiState = MutableStateFlow(UiState.WorkoutUiState())

    val uiState: StateFlow<UiState.WorkoutUiState> = _uiState.asStateFlow()


    //在初始化过程中，它加载初始数据并设置对锻炼字段变化的响应式观察。
    init {
        loadInitialData()
        observeWorkoutFieldChanges()
    }

    /**
     * Observes changes in the workout fields and updates the UI state accordingly.
     *
     * This function monitors the `uiState` for changes. It computes whether the upload button should be enabled
     * based on the workout title and the list of workout exercises. It also updates the workout tags by extracting
     * hashtags from both the workout title and description. The updated values are then applied to the UI state.
     */
    //响应式状态观察
    //ViewModel 通过观察锻炼字段的变化来实现响应式编程，并自动更新派生状态，如上传按钮启用和标签提取

    //observeWorkoutFieldChanges 方法实现响应式编程：
    //
    //监听 UI 状态变化
    //自动计算上传按钮是否启用（基于标题和练习列表）
    //从标题和描述中提取标签
    //使用 launchIn(viewModelScope) 在 ViewModel 生命周期内运行
    private fun observeWorkoutFieldChanges() {
        uiState
            .onEach { state ->
                // Compute whether the upload is enabled
                val isUploadEnabled = state.workout.title.isNotEmpty() &&
                        state.workout.workoutExerciseList.isNotEmpty()

                // Update tags by extracting hashtags from both title and description
                val updatedWorkout = state.workout.copy(
                    tags = (extractHashtags(state.workout.title) +
                            extractHashtags(state.workout.description)).distinct()

                )

                // Update the UI state with both computed values
                _uiState.update { currentState ->
                    currentState.copy(
                        isUploadEnabled = isUploadEnabled,
                        workout = updatedWorkout
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    // Operations Management
    // 操作管理
    // 定义了操作管理模式：
    // Operation 密封类定义操作类型
    private sealed class Operation {
        object UploadWorkout : Operation()
        object LoadInitialData : Operation()
    }
    //handleOperation 方法统一处理异步操作的加载状态和错误处理
    private fun handleOperation(operation: Operation, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                setStateForOperation(operation, UiState.DataState.Loading)
                block()
                setStateForOperation(operation, UiState.DataState.WorkoutSetup)
            } catch (e: Exception) {
                setStateForOperation(
                    operation,
                    UiState.DataState.Error(e.message ?: "Unknown error")
                )
            }
        }
    }

    //setStateForOperation 根据操作类型更新相应的状态
    private fun setStateForOperation(operation: Operation, state: UiState.DataState) {
        _uiState.update { currentState ->
            when (operation) {
                is Operation.UploadWorkout -> currentState.copy(workoutState = state)
                is Operation.LoadInitialData -> currentState.copy(workoutState = state)
            }
        }
    }

    // Data Loading
    // loadInitialData 方法：
    //使用 handleOperation 包装异步操作
    //调用 exerciseDataManager.loadCachedExerciseData() 加载缓存数据
    //成功时更新 UI 状态，包括身体部位、目标肌肉、器械、练习列表
    //设置当前用户为锻炼创建者
    private fun loadInitialData() = handleOperation(Operation.LoadInitialData) {
        exerciseDataManager.loadCachedExerciseData()
            .onSuccess { result ->
                _uiState.update { state ->
                    state.copy(
                        bodyPartList = result.bodyParts,
                        targetList = result.targets,
                        equipmentList = result.equipment,
                        exerciseList = result.exercises,
                        filteredExerciseList = result.exercises,
                        workout = state.workout.copy(creatorId = authRepository.getCurrentUserId())
                    )
                }
                Log.d("CreateWorkoutViewModel", "loadCachedExerciseData in CreateWorkoutViewModel Success.")
            }
            .onFailure { throw it }
    }


    // Exercise Management
    // 管理练习相关操作：
    inner class ExerciseManager {

        //addWorkoutExercise 方法
        //验证练习数据
        //如果验证失败，更新验证状态并返回
        //成功时添加练习到列表，自动设置顺序号
        fun addWorkoutExercise(newWorkoutExercise: WorkoutExercise?) {
            newWorkoutExercise ?: return

            val validationState = WorkoutValidator.validate(newWorkoutExercise)
            if (validationState.hasErrors) {
                _uiState.update { it.copy(validationState = validationState) }
                return
            }
            _uiState.update { state ->

                newWorkoutExercise.order = state.workout.workoutExerciseList.size + 1

                state.copy(
                    selectedExercise = null,
                    showExerciseDialog = false,
                    validationState = UiState.ValidationState(),
                    workout = state.workout.copy(
                        workoutExerciseList = state.workout.workoutExerciseList + newWorkoutExercise
                    ),
                )
            }
        }

        //deleteWorkoutExercise 方法
        //删除指定练习
        //重新排序剩余练习
        //如果删除的是当前选中练习，清空选中状态
        fun deleteWorkoutExercise(workoutExercise: WorkoutExercise) =
            _uiState.update { state ->
                state.copy(
                    // delete exercise and update the order of the remaining exercises
                    workout = state.workout.copy(
                        workoutExerciseList = state.workout.workoutExerciseList
                            .filter { it != workoutExercise }
                            .mapIndexed { index, exercise ->
                                exercise.copy(order = index + 1)
                            }
                    ),
                    selectedExercise = if (state.selectedExercise == workoutExercise) null else state.selectedExercise
                )
            }

        // setSelectedExercise：设置选中练习并显示对话框
        fun setSelectedExercise(exercise: StaticExercise?) {
            val workoutExercise = exercise?.let { WorkoutExercise(it) }
            _uiState.update {
                it.copy(
                    selectedExercise = workoutExercise,
                    showExerciseDialog = true
                )
            }
        }

        // editWorkoutExercise：编辑练习
        fun editWorkoutExercise(workoutExercise: WorkoutExercise?) {
            _uiState.update { it.copy(selectedExercise = workoutExercise) }
        }

        // dismissAddExerciseDialog：关闭对话框
        fun dismissAddExerciseDialog() {
            _uiState.update { it.copy(showExerciseDialog = false) }
        }
    }

    // Filter Management
    // 管理过滤功能
    inner class FilterManager {
        // onQueryChange：更新搜索查询并应用过滤
        fun onQueryChange(query: String) = viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query) }
            applyFilters()
        }

        // applyFilters：根据当前过滤条件过滤练习列表
        fun applyFilters() {
            val state = _uiState.value
            val filteredExercises = state.exerciseList.filter { exercise ->
                val matchesSearch = state.searchQuery.isBlank() ||
                        exercise.name.contains(state.searchQuery, ignoreCase = true)
                val matchesBodyPart = state.bodyPartFilter == null ||
                        exercise.bodyPart == state.bodyPartFilter.name
                val matchesEquipment = state.equipmentFilter == null ||
                        exercise.equipment == state.equipmentFilter.name
                val matchesTarget = state.targetFilter == null ||
                        exercise.target == state.targetFilter.name

                matchesSearch && matchesBodyPart && matchesEquipment && matchesTarget
            }

            _uiState.update { it.copy(filteredExerciseList = filteredExercises) }
        }

        //身体部位滤器变更方法
        fun onBodyPartFilterChange(bodyPart: BodyPart?) {
            _uiState.update { it.copy(bodyPartFilter = bodyPart) }
            applyFilters()
        }

        //
        fun onEquipmentFilterChange(equipment: Equipment?) {
            _uiState.update { it.copy(equipmentFilter = equipment) }
            applyFilters()
        }

        fun onTargetMuscleFilterChange(targetMuscle: TargetMuscle?) {
            _uiState.update { it.copy(targetFilter = targetMuscle) }
            applyFilters()
        }

        //clearFilters：清空所有过滤条件
        fun clearFilters() {
            _uiState.update {
                it.copy(
                    bodyPartFilter = null,
                    equipmentFilter = null,
                    targetFilter = null,
                    searchQuery = ""
                )
            }
            applyFilters()
        }

        //showFilterSheet/hideFilterSheet：控制过滤器界面显示
        fun hideFilterSheet() {
            _uiState.update { it.copy(showFilterSheet = false) }
        }

        fun showFilterSheet() {
            _uiState.update { it.copy(showFilterSheet = true) }
        }


    }


    // Workout Management
    // 管理锻炼相关操作
    inner class WorkoutManager {

        //uploadWorkout 方法
        //设置创建时间戳
        //调用仓库上传锻炼和图片
        //处理成功和失败情况
        //成功时重置 UI 状态并执行回调
        fun uploadWorkout(onSuccess: () -> Unit) = handleOperation(Operation.UploadWorkout) {
            val workout = _uiState.value.workout.copy(
                dateCreated = Timestamp.now()
            )
            val result = workoutRepository.uploadWorkoutWithImage(workout, workout.imagePath)

            when (result) {
                is ResultWrapper.Success -> {
                    resetUiState()
                    onSuccess()
                }
                is ResultWrapper.Error -> throw result.exception
                else -> Unit
            }
        }

        // updateWorkout：函数式更新锻炼
        fun updateWorkout(update: (Workout) -> Workout) {
            _uiState.update { state ->
                state.copy(workout = update(state.workout))
            }
        }

        // editWorkout：直接编辑锻炼
        fun editWorkout(workout: Workout) {
            _uiState.update { it.copy(workout = workout) }
        }


    }

    // resetUiState 方法
    //重置 UI 状态到初始值
    //生成新的锻炼 ID
    //重新加载初始数据
    fun resetUiState() {
        _uiState.value = UiState.WorkoutUiState().copy(
            workout = Workout(
                id = generateRandomId(Constants.WORKOUT),
                creatorId = authRepository.getCurrentUserId()
            )
        )
        loadInitialData()
    }

    // toggle between setup workout general settings page and exercise list page
    // 在锻炼设置和练习列表界面之间切换
    fun toggleExerciseWorkoutListShow() {
        _uiState.update { state ->
            state.copy(
                workoutState =
                    if (state.workoutState == UiState.DataState.ExerciseListSetup) {
                        UiState.DataState.WorkoutSetup
                    } else {
                        UiState.DataState.ExerciseListSetup
                    }
            )
        }
    }

    // Public instance of managers
    // 暴露三个管理器实例供 UI 组件使用
    val exerciseManager = ExerciseManager()
    val filterManager = FilterManager()
    val workoutManager = WorkoutManager()
}