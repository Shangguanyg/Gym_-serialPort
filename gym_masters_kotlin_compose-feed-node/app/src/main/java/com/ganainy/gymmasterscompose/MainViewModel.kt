package com.ganainy.gymmasterscompose

import android.util.Log
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.prefs.ExerciseDownloadPrefs
import com.ganainy.gymmasterscompose.ui.repository.AuthRepository
import com.ganainy.gymmasterscompose.ui.repository.IExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.ILikeRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ganainy.racingcar.domain.usecase.GetHighscoreUseCase
import com.ganainy.racingcar.domain.usecase.SaveHighscoreUseCase
import com.ganainy.racingcar.utils.SoundRepository

// The MainViewModel 似乎是应用程序的主要入口 ViewModel，处理诸如身份验证状态管理、运动数据下载和同步等关键初始化任务。
// 它使用通过 Hilt 注入的多个存储库进行依赖注入，包括 AuthRepository、ILikeRepository 和 IExerciseRepository。


/**
 * ViewModel for managing user authentication state.
 *
 * @property authRepository The repository for authentication operations.
 */
// 此文件包含一个 MainViewModel 类，该类作为用户身份验证状态的 ViewModel
// 该类使用 @HiltViewModel 注解，并使用依赖注入
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val likeRepository: ILikeRepository,
    private val exerciseDownloadPrefs: ExerciseDownloadPrefs,
    private val exerciseRepository: IExerciseRepository,
    private val getHighscoreUseCase: GetHighscoreUseCase,
    private val saveHighscoreUseCase: SaveHighscoreUseCase,
    private val soundRepository: SoundRepository,
) : ViewModel() {

    //此 ViewModel 的关键特性包括：
    //
    //认证状态管理 ：它通过一个 MutableStateFlow 来管理认证状态，该状态跟踪用户是否正在加载、已认证或未认证
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val authState = _authState.asStateFlow()

    //初始化逻辑 ：在 init 块中，它观察用户登录状态，并在应用启动时同步待处理的点赞
    init {
        viewModelScope.launch {
            // Start with Loading state and then update based on auth status
            authRepository.isUserLoggedIn().collect { isLoggedIn ->
                _authState.value = if (isLoggedIn) {
                    AuthUiState.Authenticated
                } else {
                    AuthUiState.Unauthenticated
                    //AuthUiState.Authenticated
                }
            }


            // Sync pending likes (local and remote) when app first starts
            likeRepository.syncPendingLikes()
        }

        //Download all exercises on the first startup of the app and cache them locally
        triggerInitialExerciseDownload()
    }

    // 锻炼下载 ：它包括在应用首次启动时触发初始锻炼下载和缓存的函数
    private fun triggerInitialExerciseDownload() {
        if (!exerciseDownloadPrefs.isInitialDownloadComplete()) {
            viewModelScope.launch {
                Log.d("StartupViewModel", "Triggering initial exercise download...")
                val result = exerciseRepository.fetchAllExercisesAndCache()
                when (result) {
                    is ResultWrapper.Success -> {
                        Log.i("StartupViewModel", "Initial exercise download completed successfully.")
                        // Flag is set within the repository on success
                    }
                    is ResultWrapper.Error -> {
                        Log.e("StartupViewModel", "Initial exercise download failed.", result.exception)
                        // Handle error - maybe schedule retry with WorkManager or show user message
                    }
                    is ResultWrapper.Loading -> { /* Should not happen from suspend fun */ }
                }
            }
        } else {
            Log.d("StartupViewModel", "Initial exercise download already complete.")
        }
    }

}

// 认证 UI 状态 ：该文件还定义了一个密封类 AuthUiState，包含三种状态：加载中、已认证和未认证
sealed class AuthUiState {
    data object Loading : AuthUiState()
    data object Authenticated : AuthUiState()
    data object Unauthenticated : AuthUiState()
}