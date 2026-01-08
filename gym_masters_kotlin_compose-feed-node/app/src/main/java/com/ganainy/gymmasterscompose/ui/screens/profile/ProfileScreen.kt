package com.ganainy.gymmasterscompose.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.ui.AppTheme
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.screens.profile.composables.CurrentUserProfileContent
import com.ganainy.gymmasterscompose.ui.screens.profile.composables.OtherUserProfileContent
import com.ganainy.gymmasterscompose.ui.screens.workout_list.WorkoutWithStatus
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import com.ganainy.gymmasterscompose.utils.MockData.sampleExerciseListSmall
import com.ganainy.gymmasterscompose.utils.MockData.samplePostList
import com.ganainy.gymmasterscompose.utils.MockData.sampleUser
import com.ganainy.gymmasterscompose.utils.MockData.sampleWorkoutWithStatus
import com.ganainy.gymmasterscompose.utils.Utils.showToast
//定义了一个用于展示用户个人资料的界面 ProfileScreen 及其内容 ProfileScreenContent，同时包含了一些用户交互的功能，使用了 Jetpack Compose 构建界面。

//todo fix infinite loading on opening other user profile
// @Composable: 指明这是一个可组合函数，用于构建 UI。
//参数:
//userId: String?: 当前显示的用户 ID，允许为 null，用于处理当前用户或其他用户的个人资料。
//导航函数: 传递各种导航操作的回调函数，允许在显示界面时处理用户操作，比如返回、登录、创建帖子、设置锻炼、查看锻炼详情和查看锻炼列表等。
@Composable
fun ProfileScreen(
    userId: String?,
    navigateBack: () -> Unit = {
        Log.d("ProfileScreen", "navigateBack: () -> Unit")
    },
    navigateToLogin: () -> Unit,
    navigateToCreatePost: () -> Unit,
    navigateToWorkoutSetup: () -> Unit,
    navigateToWorkoutDetails: (WorkoutWithStatus) -> Unit,
    navigateToExercisesList: () -> Unit,
    navigateToExerciseDetails: (StaticExercise) -> Unit
) {
    //创建 ViewModel 实例: 使用 Hilt 的 hiltViewModel() 函数获取 ProfileViewModel 的实例，管理该界面的数据和业务逻辑。
    val viewModel = hiltViewModel<ProfileViewModel>()

    //加载用户资料: 调用 ViewModel 中的 loadProfile() 方法加载指定用户的资料信息。传入用户 ID，以加载对应的用户资料。
    viewModel.loadProfile(userId)

    //收集 UI 数据:
    //uiData: 收集表示用户资料的状态数据，使用自动更新的方式反映数据变化。
    //uiState: 收集并监视当前的 UI 状态（如成功、加载中、错误）。
    //    val uiData by viewModel.uiData.collectAsState()：收集 UI 数据状态。
    //    val uiState by viewModel.uiState.collectAsState()：收集 UI 状态，表明当前的用户状态，如加载、成功或错误。

    val uiData by viewModel.uiData.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    //操作处理逻辑: 定义一个操作回调 onAction，根据传入的动作类型执行不同的逻辑。
    //支持的动作:
    //导航到创建帖子、返回、编辑个人资料、导航到锻炼详情、导航到锻炼列表、导航到登录、注销 和 切换关注状态。
    //使用类型安全的密封类 ProfileScreenAction，为不同的用户交互提供明确的指示。
    val onAction: (ProfileScreenAction) -> Unit = { action ->
        Log.d("ProfileScreen", "onAction: (ProfileScreenAction)")
        when (action) {
            //导航至创建帖子页面。
            ProfileScreenAction.NavigateToCreatePost -> navigateToCreatePost()
            //执行返回操作。
            ProfileScreenAction.NavigateBack -> {
                Log.d("ProfileScreen", "onAction: ProfileScreenAction.NavigateBack")
                navigateBack()
            }
            is ProfileScreenAction.NavigateToEditProfile -> viewModel.onEditProfilePicture(action.imagePath)
            //导航至锻炼详情页面
//            is ProfileScreenAction.NavigateToExerciseDetails -> navigateToExerciseDetails(action.exercise)
            is ProfileScreenAction.NavigateToExerciseDetails -> navigateToExerciseDetails(action.exercise)
            ProfileScreenAction.NavigateToExercisesList -> navigateToExercisesList()
            ProfileScreenAction.NavigateToLogin -> navigateToLogin()
            is ProfileScreenAction.NavigateToWorkoutDetails -> navigateToWorkoutDetails(action.workoutWithStatus)
            ProfileScreenAction.NavigateToWorkoutSetup -> {
                Log.d("ProfileScreen", "NavigateToWorkoutSetup in ProfileScreenAction ")
                navigateToWorkoutSetup()
            }
            is ProfileScreenAction.Logout -> viewModel.logout {
                navigateToLogin()
            }

            is ProfileScreenAction.ToggleFollow -> viewModel.toggleFollow(action.userToFollowOrUnfollowId)
        }
    }

    //ProfileScreenContent(...): 调用可组合的内容组件，传递 UI 状态、数据和操作逻辑。这个组件会负责显示用户接口的具体内容。
    ProfileScreenContent(uiState, uiData, onAction)
}

//定义 ProfileScreenContent: 这是用于渲染用户个人资料的具体内容函数。
//参数:
//uiState: 当前 UI 状态，包含了加载、错误信息等。
//uiData: 用户具体数据，包括用户信息、帖子等。包含用户的资料和信息数据。
//onAction: 处理用户操作的回调。
@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    uiData: ProfileUiData,
    onAction: (ProfileScreenAction) -> Unit
) {
   // Log.d("ProfileScreen", "begin of ProfileScreenContent")

    //Column { ... }: 使用 Column 布局组件垂直布置子组件。
    //CustomTopAppBar(...): 自定义的顶部应用栏组件，展示标题“Profile”和导航图标。
    //onNavigationClick: 当用户点击返回图标时，调用 onAction 函数进行返回操作。
    Column() {
        //CustomTopAppBar(...)：自定义应用程序顶部栏组件，用于显示页面标题和导航图标。
        //参数说明：
        //title = "Profile"：设置标题为 "Profile"。
        //navigationIcon：设置返回图标，表示用户可以返回上一页面。
        //onNavigationClick：点击返回图标时调用的函数，触发返回动作。
        CustomTopAppBar(
            title = "个人设置",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = { onAction(ProfileScreenAction.NavigateBack) },
        )

        //Spacer(...)：增加垂直空间，设置的高度为 16dp，以确保顶部应用程序栏与后续内容之间有空间。
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        //使用 when 表达式检查当前的 UI 状态 uiState。这里使用了 val state = uiState 进行解包。
        //Loading 状态：当状态为 Loading 时，调用 LoadingIndicator() 展示加载进度指示器。
        when (val state = uiState) {
                is ProfileUiState.Loading -> {
                // Loading state
                Log.d("ProfileScreen", "ProfileUiState.Loading${ProfileUiState.Loading}")
                LoadingIndicator()
            }

            is ProfileUiState.Error -> {
                // Error state
                Log.d("ProfileScreen", "ProfileUiState.Error")
                //Error 状态：当 UI 状态为 Error 时，使用嵌套的 when 表达式处理具体错误状。
                //Integer 错误：如果是整型错误，提取资源字符串，并调用 showToast 显示消息。
                //字符串错误：直接提取字符串错误消息并调用 showToast 进行显示。
                when (state) {
                    is ProfileUiState.Error.IntError -> {
                        Log.d("ProfileScreen", "ProfileUiState.Error.IntError")
                        val errorMessage = stringResource(state.messageStringResource)
                        showToast(context = LocalContext.current, message = errorMessage)
                    }

                    is ProfileUiState.Error.StringError -> {
                        Log.d("ProfileScreen", "ProfileUiState.Error.StringError")
                        showToast(context = LocalContext.current, message = state.message)
                    }
                }
            }

            //Success 状态：处理下载成功的状态，先从状态中提取 profileType。
            is ProfileUiState.Success -> {

//                Log.d("ProfileScreen", "ProfileUiState.Success")
                val profileType = state.profileType
                when (profileType) {
                    ProfileType.CURRENT_USER -> {
                        // Local user profile
                        CurrentUserProfileContent(
                            uiData = uiData,
                            onAction = onAction,
                        )
                    }

                    ProfileType.OTHER_USER -> {
                        // Other user profile
                        OtherUserProfileContent(
                            uiData = uiData,
                            onAction = onAction,
                        )
                    }
                }

            }
        }

    }
}


enum class EmptyStateActionType {
    CREATE_POST_ACTION,
    CREATE_WORKOUT_ACTION,
    EXPLORE_EXERCISE_LIST_ACTION
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreenContent() {
    AppTheme {
        ProfileScreenContent(
            uiState = ProfileUiState.Success(ProfileType.CURRENT_USER),
            uiData = ProfileUiData(
                user = sampleUser,
                posts = samplePostList,
                workoutWithStatusList = listOf(sampleWorkoutWithStatus),
                exerciseList = sampleExerciseListSmall,
                isFollowing = false
            ),
            onAction = {}
        )
    }
}