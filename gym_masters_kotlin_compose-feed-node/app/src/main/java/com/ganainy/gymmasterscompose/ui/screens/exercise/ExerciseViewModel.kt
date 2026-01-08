package com.ganainy.gymmasterscompose.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.repository.IExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.onError
import com.ganainy.gymmasterscompose.ui.repository.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ExerciseViewModel 是一个 Hilt 注入的 ViewModel，它扩展了 Android 的 ViewModel 类并管理 Exercise 屏幕的状态。
// ExerciseViewModel 遵循 Android 的 MVVM 架构模式，并利用现代 Android 开发实践，
// 包括用于依赖项注入的 Hilt、用于异步作的 Kotlin 协程以及用于响应式界面更新的 Jetpack Compose。
// ViewModel 通过将数据作委托给存储库层来保持关注点的分离，同时管理特定于 UI 的状态和业务逻辑。
// 整个系统中使用的练习模型是一个 Room 实体，同时支持 API 数据和本地存储功能。
@HiltViewModel
//ViewModel 具有通过 Hilt 的依赖项注入注入的单个依赖项：
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository
) : ViewModel() {

    //状态管理
    //ViewModel 使用 Kotlin 协程和 Flow 实现响应式状态管理。它维护一个私有的可变状态流，并公开一个不可变版本供 UI 使用。
    //_uiState: 使用 MutableStateFlow 来保持内部状态，初始状态为一个新创建的 ExerciseUiState 对象。
    //val uiState: 为外部提供一个不变的状态流 StateFlow，允许观察 UI 状态，不允许外部修改。
    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState = _uiState.asStateFlow()

    //核心功能
    //初始化和练习设置
    //ViewModel 通过在其 init 块中调用 observeExerciseSaveState（） 来初始化。
    //init 块: 初始化代码块，在创建 ViewModel 时自动执行。这里调用 observeExerciseSaveState() 方法来启动观察锻炼保存状态的逻辑。
    init {
        observeExerciseSaveState()
    }
    //setExercise 函数: 用于更新当前锻炼对象和加载状态。
    //_uiState.update { ... }: 调用 StateFlow 的 update 方法，以当前状态为基础更新状态，通过调用 copy() 方法创建一个新的状态实例。
    fun setExercise(exercise: StaticExercise) {
        //使用 update 函数执行状态更新，以确保线程安全性和不变性。
        _uiState.update {
            it.copy(
                exercise = exercise,
                isLoading = false
            )
        }
    }

    //save exercise locally for this user
    //练习保存/取消保存功能
    //toggleExerciseSave 函数处理在本地保存或取消保存练习的核心业务逻辑。它在 IO 调度程序上运行，并在作期间管理加载状态。

    //toggleExerciseSave 函数: 切换当前锻炼的保存状态。
    //更新加载状态: 首先将 isLoading 设置为 true，表明操作正在进行。
    //协程启动: 在 viewModelScope 中使用 launch 启动一个协程，这样可以在后台执行长时间运行的操作，避免阻塞 UI。
    //获取锻炼: 从当前 uiState 获取锻炼对象。如果锻炼不为 null，就调用 exerciseRepository 的方法来切换保存状态。
    //成功和错误处理:
    //onSuccess: 如果保存成功，更新状态将 isLoading 设置为 false。
    //onError: 如果失败，更新状态，设置 isLoading 为 false 并将错误信息存储到状态中。
    fun toggleExerciseSave() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val exercise = uiState.value.exercise
            if (exercise != null) {
                //save exercise to user exercises
                exerciseRepository.toggleExerciseSaveLocally(exercise).onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }.onError {
                    _uiState.update { it.copy(isLoading = false, error = it.error) }
                }

            }
        }

    }

    //实时锻炼状态观察
    //observeExerciseSaveState 函数建立对练习保存状态的连续观察，并在发生更改时自动更新 UI。

    //observeExerciseSaveState 函数: 观察当前锻炼的保存状态，实时更新 UI 状态。
    //协程启动: 在 viewModelScope 中启动协程以处理异步流。
    //锻炼存在性检查: 先确认 uiState 中的锻炼不为 null，接着调用 exerciseRepository 的 observeExercise 方法，传入锻炼 ID 并对结果进行监听。
    //结果处理:
    //onSuccess: 如果成功获取到锻炼对象，更新 uiState 中的锻炼数据并将加载状态设置为 false。
    //onError: 如果在获取过程中发生错误，更新状态将加载标志设置为 false，同时记录错误信息。
    private fun observeExerciseSaveState() {
        viewModelScope.launch {
            uiState.value.exercise?.let {
                exerciseRepository.observeExercise(it.id).collect { result ->
                    result.onSuccess { exercise ->
                            _uiState.update { currentUiState ->
                                currentUiState.copy(
                                    exercise = exercise,
                                    isLoading = false
                                )
                            }
                    }
                    .onError {
                        _uiState.update { it.copy(isLoading = false, error = it.error) }
                    }
            }
        }
    }

}


//该类使用配套数据类 ExerciseUiState 来表示 UI 状态，其中包含三个主要属性：加载状态、错误消息和练习数据。
data class ExerciseUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
//    val exercise: Exercise? = null,
    val exercise: StaticExercise? = null,
)
}