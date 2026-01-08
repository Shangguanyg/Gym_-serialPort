package com.ganainy.gymmasterscompose.ui.screens.exercise_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.prefs.ExerciseDownloadPrefs
import com.ganainy.gymmasterscompose.ui.models.BodyPart
import com.ganainy.gymmasterscompose.ui.models.Equipment
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.TargetMuscle
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.repository.IExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.utils.CachedExerciseDataResult
import com.ganainy.gymmasterscompose.utils.ExerciseDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel：Hilt 注解，标记该类为 ViewModel，以便进行依赖注入。
//class ExerciseListViewModel：定义 ExerciseListViewModel 类，继承自 ViewModel。
//@Inject 构造函数：注入 ExerciseDataManager、IExerciseRepository 和 ExerciseDownloadPrefs 的实例，
// 方便在 ViewModel 中使用这些数据的管理和访问功能。
@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseDataManager: ExerciseDataManager,
    private val exerciseRepository: IExerciseRepository,
    private val exerciseDownloadPrefs: ExerciseDownloadPrefs
) : ViewModel() {
    // private val _uiState：定义一个私有的 MutableStateFlow，用于表示 UI 状态，
    // 初始化为 ExerciseListUiState 的一个实例，默认值为初始状态。
    // val uiState：将 _uiState 转换为只读的状态流，外部只能观察，不能修改，符合封装原则。
    private val _uiState = MutableStateFlow(ExerciseListUiState())
    val uiState = _uiState.asStateFlow()

    // val filteredExercises：定义一个流，表示根据当前状态过滤的锻炼列表。
    // uiState.map { state -> ... }：根据 UI 状态的变化，生成新的过滤后的锻炼数据列表。
    // when (val dataState = state.dataState)：检查当前的数据状态：
    // is DataState.Success：如果状态是成功状态，调用 exerciseDataManager.filterExercises(...) 进行过滤。
    // 否则返回空列表。
    // stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())：将结果流转换为可共享的状态流，
    // 只有在至少一个观察者订阅时才会生效，未订阅时在 5000ms 后停止。
    val filteredExercises = uiState.map { state ->
        when (val dataState = state.dataState) {
//            is DataState.Success -> exerciseDataManager.filterExercises(
//                exercises = dataState.exercises,
//                query = state.searchQuery,
//                bodyPart = state.filters.activeFilters.bodyPart,
//                target = state.filters.activeFilters.targetMuscle,
//                equipment = state.filters.activeFilters.equipment
//            )
            is DataState.Success -> exerciseDataManager.filterStaticExercises(
                //staticExercises = dataState.exercises,
                staticExercises = dataState.exercises,
                query = state.searchQuery,
                bodyPart = state.filters.activeFilters.bodyPart,
                target = state.filters.activeFilters.targetMuscle,
                equipment = state.filters.activeFilters.equipment
            )
            else -> emptyList()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    //init 块：在 ViewModel 初始化时调用 loadInitialData() 方法，开始加载初始数据。
    init {
        loadInitialData()
        Log.d("ExerciseListViewModel", "loadInitialData completed in init")
    }

    // private fun loadInitialData()：定义一个私有方法，用于加载初始数据。
    // viewModelScope.launch { ... }：使用协程在 ViewModel 的作用域内开启一个新协程。
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(dataState = DataState.Loading) }

            // Check if initial download is complete
            // if (exerciseDownloadPrefs.isInitialDownloadComplete())：检查初始下载是否完成，调用 ExerciseDownloadPrefs 中的方法。
            // Log.d(...)：记录日志，表明初始下载已完成，将从缓存中加载数据。
            if (exerciseDownloadPrefs.isInitialDownloadComplete()) {
                Log.d("ExerciseListViewModel", "Initial download complete. Loading from cache.")
                // Load directly from cache using the manager/repository
                // exerciseDataManager.loadCachedExerciseData()：从 ExerciseDataManager 中加载缓存的数据。
                // onSuccess { result: CachedExerciseDataResult ->：处理成功返回的缓存数据，这里要显式声明类型。

                exerciseDataManager.loadCachedExerciseData()
                    .onSuccess { result: CachedExerciseDataResult -> // Specify type
                        // Check if cache was actually populated
                        // 检查返回的锻炼列表是否为空：
                        // 如果为空，记录警告日志，表示标记为完成的初始下载但缓存为空。
                        if (result.staticExercises.isEmpty()) {
                            Log.w("ExerciseListViewModel", "Initial download was marked complete, but cache is empty. Triggering download again.")
                            // Set state to trigger download UI
                            // 更新 UI 状态为 DataState.InitialDownloadRequired，提示用户需要下载。
                            _uiState.update {
                                it.copy(dataState = DataState.InitialDownloadRequired("Cache empty, please download exercises."))
                            }
                        } else {
                            // Cache has data, update UI
                            // 如果缓存非空，更新 UI 状态，设置过滤器的列表和数据状态为成功状态，以及所加载的锻炼数据。
                            Log.d("ExerciseListViewModel", "Cache has data, update UI")
                            _uiState.update {
                                it.copy(
                                    filters = FilterState(
                                         //bodyPartList = result.bodyParts.,
                                         bodyPartList = listOf(BodyPart(name = "testBody")),
                                         //targetList = result.targets,
                                         targetList = listOf(TargetMuscle(name = "testTargetMuscle")),
                                         //equipmentList = result.equipment,
                                         equipmentList = listOf(Equipment(name = "testTargetMuscle")),
                                    ),
                                   dataState = DataState.Success(result.staticExercises)
                                   // dataState = DataState.Success(result.exercises)
                                )
                            }
                            Log.d("ExerciseListViewModel", " UI updated")
                        }
                    }
                    // 处理加载缓存失败的情况，记录错误日志，并更新 UI 状态为 DataState.Error，指明加载缓存数据时发生了错误。
                    .onFailure { error ->
                        Log.e("ExerciseListViewModel", "Error loading data from cache", error)
                        _uiState.update {
                            it.copy(dataState = DataState.Error(error.message ?: "Error loading cached data"))
                        }
                    }
            } else {
                // Initial download not yet complete or failed previously
                // 如果初始下载未完成或曾失败，记录调试日志，并提示用户进行下载。
                Log.d("ExerciseListViewModel", "Initial download not complete. Prompting user.")
                _uiState.update {
                    it.copy(dataState = DataState.InitialDownloadRequired("Exercises need to be downloaded."))
                }
            }
        }
    }


    //fun retry()：定义一个重新尝试的方法。
    //检查当前 UI 状态是否为 DataState.InitialDownloadRequired，允许重新尝试下载。
    //启动协程并更新状态为加载中，显示用户正在进行下载。
    fun retry() {
        // Only retry if in the InitialDownloadRequired state
        if (_uiState.value.dataState is DataState.InitialDownloadRequired) {
            viewModelScope.launch {
                Log.d("ExerciseListViewModel", "Retry button clicked. Forcing exercise download...")
                _uiState.update { it.copy(dataState = DataState.Loading) } // Show loading during download attempt

                //调用 exerciseRepository.fetchAllExercisesAndCache(forceRefresh = true) 尝试强制刷新并下载锻炼数据。
                val result = exerciseRepository.fetchAllExercisesAndCache(forceRefresh = true)

                //使用 when 语句来处理下载结果：
                //如果成功，记录成功日志，并调用 loadInitialData() 重新加载缓存。
                //如果失败，记录失败日志，更新 UI 状态为需要重新下载。
                //should not happen 可以表示这种状态不应被调用，因此不处理。
                when(result) {
                    is ResultWrapper.Success -> {
                        Log.i("ExerciseListViewModel", "Retry download successful. Reloading cached data.")
                        // Now that download succeeded, load data from cache
                        loadInitialData()
                    }
                    is ResultWrapper.Error -> {
                        Log.e("ExerciseListViewModel", "Retry download failed.", result.exception)
                        // Stay in the download required state, update message
                        _uiState.update {
                            it.copy(dataState = DataState.InitialDownloadRequired("Download failed. Please try again."))
                        }
                    }
                    is ResultWrapper.Loading -> { /* Should not happen */ }
                }
            }
        } else {
            // 如果在非下载状态（如缓存加载错误状态）下调用重新尝试，记录日志并重新加载缓存数据。
            // If retry is called in other states (e.g. Error loading cache), just reload cache
            Log.d("ExerciseListViewModel", "Retry called in non-download state. Reloading cached data.")
            loadInitialData()
        }
    }

    //fun updateSearchQuery(query: String)：定义一个方法更新搜索查询。
    //更新 UI 状态，复制现有状态，但将 searchQuery 更新为新的查询字符串。
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // fun updateFilters(newFilters: ActiveFilters)：定义一个方法更新活动过滤器。
    // 更新 UI 状态，保留现有过滤器并更新活动过滤器为新的过滤器。
    fun updateFilters(newFilters: ActiveFilters) {
        _uiState.update { it.copy(
            filters = it.filters.copy(activeFilters = newFilters)
        )}
    }

    //fun clearFilters()：定义一个方法清除所有过滤器。
    //更新 UI 状态，将活动过滤器重置为默认状态 ActiveFilters()。
    fun clearFilters() {
        _uiState.update { it.copy(
            filters = it.filters.copy(activeFilters = ActiveFilters())
        )}
    }


}

//定义 ExerciseListUiState 数据类，包含搜索查询、过滤器和数据状态，提供 UI 状态的所有必要信息。
data class ExerciseListUiState(
    val searchQuery: String = "",
    val filters: FilterState = FilterState(),
    val dataState: DataState = DataState.Loading
)

//定义 FilterState 数据类，包含身体部位列表、目标肌肉列表、设备列表和活动过滤器，提供 UI 过滤的结构。
data class FilterState(
    val bodyPartList: List<BodyPart> = emptyList(),
    val targetList: List<TargetMuscle> = emptyList(),
    val equipmentList: List<Equipment> = emptyList(),
    val activeFilters: ActiveFilters = ActiveFilters()
)

//定义 ActiveFilters 数据类，用于表示当前选中的过滤器，默认为空。
data class ActiveFilters(
    val bodyPart: BodyPart? = null,
    val targetMuscle: TargetMuscle? = null,
    val equipment: Equipment? = null
)

//定义一个密封类 DataState，表示不同的数据加载状态。
//Loading：表示数据正在加载中。
//Success：表示数据加载成功，包含加载的锻炼列表。
//Error：表示加载错误，包含错误消息。
//InitialDownloadRequired：表示需要用户下载初始数据的状态。
sealed class DataState {
    object Loading : DataState()
    data class Success(val exercises: List<StaticExercise>) : DataState()
    //data class SuccessStaticExercises(val exercises: List<StaticExercise>) : DataState()
    data class Error(val message: String) : DataState() // Error loading CACHED data
    data class InitialDownloadRequired(val message: String) : DataState() // Cache empty/download failed
}