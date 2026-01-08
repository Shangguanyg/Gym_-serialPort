package com.ganainy.gymmasterscompose.ui.screens.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.User
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.repository.IAuthRepository
import com.ganainy.gymmasterscompose.ui.repository.IExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.ILikeRepository
import com.ganainy.gymmasterscompose.ui.repository.ISocialRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.IWorkoutRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.ui.repository.onError
import com.ganainy.gymmasterscompose.ui.repository.onSuccess
import com.ganainy.gymmasterscompose.ui.room.CachedLike
import com.ganainy.gymmasterscompose.ui.room.LikeType
import com.ganainy.gymmasterscompose.ui.screens.workout_list.WorkoutWithStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


//定义了一个数据类 ProfileUiData，用于表示用户个人资料的 UI 数据，其中包含用户信息、帖子列表、是否关注、训练状态列表等。
data class ProfileUiData(
    val user: User = User(),
    val posts: List<FeedPost> = emptyList(),
    val isFollowing: Boolean = false,
    val workoutWithStatusList: List<WorkoutWithStatus> = emptyList(),
    val isOwnProfile: Boolean = false,
    val exerciseList: List<StaticExercise> = emptyList()
) {
}

//ProfileUiState 是一个密封类，用于表示 UI 状态。可以是加载中、成功（包括用户类型）或错误状态（包括整数或字符串形式的错误消息）。
sealed class ProfileUiState {
    data class Success(val profileType: ProfileType) : ProfileUiState()
    object Loading : ProfileUiState()
    sealed class Error : ProfileUiState() {
        data class IntError(val messageStringResource: Int) : Error()
        data class StringError(val message: String) : Error()
    }
}

// 定义了一个枚举类 ProfileType，表示用户类型（当前用户或其他用户）。
enum class ProfileType {
    CURRENT_USER,
    OTHER_USER
}

//
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository,
    private val socialRepository: ISocialRepository,
    private val workoutRepository: IWorkoutRepository,
    private val likeRepository: ILikeRepository,
    private val exerciseRepository: IExerciseRepository
) : ViewModel() {

    //保存传入的 Application 实例，以便在 ViewModel 中使用。
    val context = application

    //创建一个私有的 MutableStateFlow 用于 UI 数据，公开为只读的状态流 uiData，以遵循封装原则。
    private val _uiData = MutableStateFlow(ProfileUiData())
    val uiData = _uiData.asStateFlow()

    //同样，创建一个 MutableStateFlow 用于 UI 状态，并默认设置为加载状态。
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()


    //定义一个只读属性 currentUserId，从 userRepository 获取当前用户的 ID。
    private val currentUserId: String
        get() = userRepository.getCurrentUserId()


    // if no userId is provided, the current user's profile is loaded otherwise the profile of the user with the provided id
    // 定义一个函数 loadProfile，接受一个可选的用户 ID。使用 viewModelScope.launch 启动一个协程，以避免内存泄漏。
    fun  loadProfile(userId: String?) {

//        Log.d("ProfileViewModel", "begin of loadProfile")
        viewModelScope.launch {
            //如果提供了 userId，则使用它；否则，使用当前用户的 ID。
            val targetUserId = userId ?: currentUserId

//            Log.d("ProfileViewModel", "targetUserId is : ${ targetUserId }")
            //更新 uiData，根据当前用户 ID 判断是否是查看自己的资料。
            _uiData.update {
                it.copy(isOwnProfile = currentUserId == userId)
            }

            //使用 combine 函数来合并多个流，从不同的仓库获取数据，这里包含用户信息、用户帖子、是否关注、练习等信息，并在结果中进行处理。
            combine(
                //userRepository.getUserFlow(targetUserId),
                userRepository.getUserPosts(targetUserId),
                socialRepository.isFollowing(targetUserId),
                workoutRepository.getLocalWorkoutsFlow(),
                likeRepository.observeTypeLikes(type = LikeType.WORKOUT, userId = targetUserId),
                exerciseRepository.observeSavedExercises()
            ) { results ->
//                Log.d("ProfileViewModel", "results[0] is ${ results[0] }")
//                Log.d("ProfileViewModel", "results[1] is ${ results[1] }")
//                Log.d("ProfileViewModel", "results[2] is ${ results[2] }")
//                Log.d("ProfileViewModel", "results[3] is ${ results[3] }")
//                Log.d("ProfileViewModel", "results[4] is ${ results[4] }")

                //这段代码判断了哪些结果是错误的。如果任何一个结果是 ResultWrapper.Error 类型，函数将立即返回一个错误结果。
                when {
                    //results[0] is ResultWrapper.Error -> ResultWrapper.Error((results[0] as ResultWrapper.Error).exception)
                    results[0] is ResultWrapper.Error -> {
                        Log.d("ProfileViewModel", "results[0] is ResultWrapper.Error")
                        ResultWrapper.Error((results[0] as ResultWrapper.Error).exception)
                    }

                    results[1] is ResultWrapper.Error -> {
                        Log.d("ProfileViewModel", "results[1] is ResultWrapper.Error")
                        ResultWrapper.Error((results[1] as ResultWrapper.Error).exception)
                    }
                    results[2] is ResultWrapper.Error -> {
                        Log.d("ProfileViewModel", "results[1] is ResultWrapper.Error")
                        ResultWrapper.Error((results[2] as ResultWrapper.Error).exception)
                    }
                    results[3] is ResultWrapper.Error -> {
                        Log.d("ProfileViewModel", "results[1] is ResultWrapper.Error")
                        ResultWrapper.Error((results[3] as ResultWrapper.Error).exception)
                    }
                    results[4] is ResultWrapper.Error -> {
                        ResultWrapper.Error((results[4] as ResultWrapper.Error).exception)
                    }

                    //如果所有结果都成功，提取出成功的结果数据，准备做进一步处理。
                    else -> {
//                        Log.d("ProfileViewModel", "all result success")
                        try {
                            //val user = (results[0] as ResultWrapper.Success<User>).data
                            var user = User(
                                id = "123123",
                                displayName = "testDisplay",
                                username = "testUser",
                                email = "test@example.com",
                                bio = "This is a test user.",
                                profilePictureUrl = "http://example.com/profile.jpg"
                                // 其他属性使用默认值
                            )
                            val posts = (results[0] as ResultWrapper.Success<List<FeedPost>>).data
                            val isFollowing = (results[1] as ResultWrapper.Success<Boolean>).data
                            val localWorkouts = (results[2] as ResultWrapper.Success<List<Workout>>).data
                            val workoutLikes = results[3] as List<CachedLike?>
                            val exerciseList = (results[4] as ResultWrapper.Success<List<StaticExercise>>).data
//                            Log.d("ProfileViewModel", "run to ProcessedProfileData")
                            // 创建一个 ProcessedProfileData 的实例，将提取的数据封装起来，用于后续的 UI 更新。
                            val processedData = ProcessedProfileData(
                                user = user,
                                posts = posts,
                                isFollowing = isFollowing,
                                exerciseList = exerciseList,
                                // 映射 localWorkouts，创建 WorkoutWithStatus 实例，检查每个训练是否被喜欢，并将处理好的数据包装为 ResultWrapper.Success 类型。
                                workouts = localWorkouts.map { workout ->
                                    WorkoutWithStatus(
                                        workout = workout,
                                        isLiked = workoutLikes.any { like ->
                                            like?.targetId == workout.id && like.isLiked
                                        },
                                        isSaved = true
                                    )
                                }
                            )
                            ResultWrapper.Success(processedData)
                        //如果在处理数据时抛出任何异常，将其捕获并返回为 ResultWrapper.Error。
                        } catch (e: Exception) {
                            ResultWrapper.Error(e)
                        }
                    }
                }
            }
                .onEach { result ->
//                    Log.d("ProfileViewModel", "run to onEach")
                    //根据 result 的状态更新 UI 数据和状态，如果请求成功，更新 _uiData 和 _uiState。
                    when (result) {
                        is ResultWrapper.Success -> {
                            _uiData.update { currentState ->
                                currentState.copy(
                                    user = result.data.user,
                                    posts = result.data.posts,
                                    isFollowing = result.data.isFollowing,
                                    exerciseList = result.data.exerciseList,
                                    workoutWithStatusList = result.data.workouts
                                )
                            }
                            _uiState.value = ProfileUiState.Success(
                                if (userId == null) ProfileType.CURRENT_USER else ProfileType.OTHER_USER
                            )
                        }
                        //检测并处理不同的结果状态。最后链式调用的 catch {} 来捕获可能未被捕获的异常，确保 UI 状态正确更新。调用 collect {} 是流的结束，表明准备收集上游结果。
                        is ResultWrapper.Error -> {
                            _uiState.value = ProfileUiState.Error.StringError(
                                result.exception.message ?: "Unknown error"
                            )
                        }

                        is ResultWrapper.Loading -> _uiState.value = ProfileUiState.Loading
                    }
                }
                .catch { throwable ->
                    _uiState.value = ProfileUiState.Error.StringError(
                        throwable.message ?: "Unknown error"
                    )
                }
                .collect {}
        }
    }


    //处理本地工作、登出和编辑个人资料
    //定义一个数据类 ProcessedProfileData，用于存储经过处理的用户资料数据。
    private data class ProcessedProfileData(
        val user: User,
        val posts: List<FeedPost>,
        val isFollowing: Boolean,
        val exerciseList: List<StaticExercise>,
        val workouts: List<WorkoutWithStatus>
    )

    //协程函数 toggleFollow，用于关注或取消关注某个用户，方法体目前未实现。
    fun toggleFollow(userIdToFollowUnfollow: String?) {
        viewModelScope.launch {
            TODO()
        }
    }

    // todo use this
    // 创建用于删除本地工作的协程函数。
    fun deleteLocalWorkout(workoutId: String) = viewModelScope.launch {
            //删除本地工作后，如果成功则调用 loadLocalWorkouts() 刷新本地工作数据，如果失败则更新 UI 状态为错误。
            workoutRepository.deleteWorkoutLocally(workoutId).onSuccess {
                loadLocalWorkouts() // Refresh local workouts after deletion
            }.onError { exception ->
                _uiState.update {
                    ProfileUiState.Error.StringError(
                        exception.message ?: "Unknown error"
                    )
                }
            }
    }

    //todo when user deletes local workout
    //声明一个私有函数 loadLocalWorkouts，目前未实现，目的是为了加载本地训练数据。
    private fun loadLocalWorkouts() = viewModelScope.launch {

    }

    //定义登出函数，调用 authRepository 的 signOut() 方法，当成功时执行 onLoggedOut 回调，若失败则更新 UI 状态以显示错误。
    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            when (val result = authRepository.signOut()) {
                is ResultWrapper.Success -> onLoggedOut()
                is ResultWrapper.Error -> _uiState.update {
                    ProfileUiState.Error.StringError(
                        result.exception.message ?: "Unknown error"
                    )
                }
                is ResultWrapper.Loading -> _uiState.value = ProfileUiState.Loading
            }
        }
    }



    //用于编辑用户头像的函数，调用 userRepository 更新头像地址，并根据结果更新 UI 状态。
    fun onEditProfilePicture(imagePath: String) {
        viewModelScope.launch {
            when (val result = userRepository.updateUserProfileImage(imagePath)) {
                is ResultWrapper.Success -> {
                    _uiData.update { currentState ->
                        currentState.copy(
                            user = currentState.user.copy(profilePictureUrl = result.data)
                        )
                    }
                }

                is ResultWrapper.Error -> _uiState.update {
                    ProfileUiState.Error.StringError(
                        result.exception.message ?: "Unknown error while updating profile picture"
                    )
                }

                is ResultWrapper.Loading -> _uiState.value = ProfileUiState.Loading
            }
        }

    }
}


