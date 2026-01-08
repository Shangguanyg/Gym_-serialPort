package com.ganainy.gymmasterscompose.ui.screens.workout_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.ui.models.ExerciseRecord
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.repository.ILikeRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.IWorkoutRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.ui.repository.onSuccess
import com.ganainy.gymmasterscompose.ui.room.LikeType
import com.ganainy.gymmasterscompose.ui.screens.workout_list.WorkoutWithStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ganainy.gymmasterscompose.ui.screens.workout_list.ExerciseRecordWithStatus

//@HiltViewModel: 注解表明该类是一个 Hilt 管理的 ViewModel，允许 Hilt 自动处理依赖注入。
//构造函数:
//@Inject constructor(...): Hilt 在创建这个 ViewModel 时会自动提供依赖项。
//private val workoutRepository: IWorkoutRepository: 注入的工作管理仓库，用于处理与锻炼数据相关的操作。
//private val userRepository: IUserRepository: 注入的用户管理仓库，用于获取用户信息（如当前用户ID）。
//private val likeRepository: ILikeRepository: 注入的点赞管理仓库，用于管理点赞状态。
@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val workoutRepository: IWorkoutRepository,
    private val userRepository: IUserRepository,
    private val likeRepository: ILikeRepository
) : ViewModel() {
    //_uiState: 使用 MutableStateFlow 创建私有变量，初始值为 WorkoutDetailsUiData 的空实例。这个变量用于管理 ViewModel 的 UI 状态。
    //uiState: 将 _uiState 转换为只读的 StateFlow，供外部观察。这允许 UI 组件在界面状态变化时自动更新。
    private val _uiState = MutableStateFlow(WorkoutDetailsUiData())
    var uiState = _uiState.asStateFlow()


    //observeWorkoutChanges(): 该方法用于观察锻炼的变化。它使用协程运行以避免阻塞主线程。
    //viewModelScope.launch: 在 ViewModel 的生命周期范围内启动一个新的协程。
    //workoutRepository.getWorkoutFlow(...): 调用仓库的方法获取锻炼的 Flow。通过当前状态获取锻炼 ID，若 ID 为 null，则抛出异常。
    //distinctUntilChanged(): 该操作符确保仅当值变化时才会触发后续的函数调用，防止对相同数据的重复处理。
    //filterNotNull(): 过滤掉 null 值，只处理有效的锻炼数据。
    fun observeWorkoutChanges() {
        viewModelScope.launch {
            try {
                workoutRepository.getWorkoutFlow(
                    _uiState.value.workoutWithStatus?.workout?.id
                        ?: throw Exception("workout id is null")
                )
                    .distinctUntilChanged()
                    .filterNotNull()
                    .collect { workoutResult ->
                        workoutResult.onSuccess { workout ->
                            viewModelScope.launch {

                                if (workout != null) {
                                    // Collect like status
                                    val isLikedFlow = likeRepository.observeLikeStatus(
                                        targetId = workout.id,
                                        type = LikeType.WORKOUT
                                    ).first()

                                    // Handle the ResultWrapper from isWorkoutSavedByUser
                                    val isSavedResult = workoutRepository.isWorkoutSavedByUser(
                                        workout.id,
                                        userRepository.getCurrentUserId()
                                    )

                                    // Extract the boolean value from ResultWrapper
                                    val isSaved = when (isSavedResult) {
                                        is ResultWrapper.Success -> isSavedResult.data
                                        is ResultWrapper.Error -> false //todo dont like this
                                        is ResultWrapper.Loading -> false //todo dont like this
                                    }

                                    _uiState.update {
                                        it.copy(
                                            workoutWithStatus = WorkoutWithStatus(
                                                workout = workout,
                                                isLiked = isLikedFlow,
                                                isSaved = isSaved
                                            )
                                        )
                                    }

                                }


                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    //切换锻炼的喜欢状态
    //toggleWorkoutLike(workout: Workout): 切换锻炼的点赞状态。
    //viewModelScope.launch: 在 ViewModel 的协程作用域内启动一个新的协程。
    //userId = userRepository.getCurrentUserId(): 获取当前用户 ID。
    //likeRepository.toggleLike(...): 调用点赞仓库的方法切换锻炼的点赞状态。
    //错误处理: 如果过程中发生异常，更新 UI 状态，使其显示错误信息。
    fun toggleWorkoutLike(workout: Workout) = viewModelScope.launch {
        try {
            val userId = userRepository.getCurrentUserId()
            likeRepository.toggleLike(
                targetId = workout.id,
                type = LikeType.WORKOUT,
                userId = userId,
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }


    //toggleWorkoutSave(workout: Workout): 切换锻炼的保存状态。
    //viewModelScope.launch: 在 ViewModel 的协程作用域内启动。
    //result: 调用工作仓库的方法切换锻炼的保存状态，接收结果。
    //检查结果: 如果操作成功则继续处理：
    //查询保存状态: 再次检查锻炼是否被当前用户保存。
    //条件处理: 如果被保存，则调用方法保存到本地；如果没有，则删除对应的本地记录。
    fun toggleWorkoutSave(workout: Workout) = viewModelScope.launch {
        try {
            val userId = userRepository.getCurrentUserId()
            val result = workoutRepository.toggleWorkoutSave(workout, userId)

            if (result is ResultWrapper.Success) {
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
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }


    //setWorkoutDetails(workout: Workout, isLiked: Boolean, isSaved: Boolean): 方法用来直接设置当前锻炼的详细信息和状态。
    //更新 UI 状态: 使用 update 函数更新 _uiState，将新的 workoutWithStatus 赋值。
    fun setWorkoutDetails(workout: Workout, isLiked: Boolean, isSaved: Boolean) {

        _uiState.update {
            it.copy(
                workoutWithStatus = WorkoutWithStatus(
                    workout,
                    isLiked,
                    isSaved
                )
            )
        }
    }
}

//WorkoutDetailsUiData 数据类
//数据类定义: 通过 data class 定义 UI 状态的数据结构。
//属性:
//workoutWithStatus: WorkoutWithStatus?: 存储当前锻炼与其状态的信息。
//isLoading: Boolean: 表示当前是否在加载状态。
//error: String?: 存储可能的错误消息，便于 UI 反馈显示。
data class WorkoutDetailsUiData(
    val workoutWithStatus: WorkoutWithStatus? = null,
    val exerciseRecordWithStatus: ExerciseRecordWithStatus? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

