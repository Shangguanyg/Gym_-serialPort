package com.ganainy.gymmasterscompose.ui.screens.static_test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.screens.exercise.ExerciseContent
import com.ganainy.gymmasterscompose.ui.screens.exercise.ExerciseViewModel
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar
import com.ganainy.gymmasterscompose.ui.shared_components.EmptyComponent
import com.ganainy.gymmasterscompose.ui.shared_components.ErrorComponent
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import com.ganainy.gymmasterscompose.utils.MockData.sampleExercise


@OptIn(ExperimentalMaterial3Api::class)
//主要 ExerciseScreen 功能
//ExerciseScreen 函数用作入口点并处理 ViewModel 集成
//主要职责：
//
//使用 Hilt 依赖项注入初始化 ExerciseViewModel
//使用 LaunchedEffect 在 ViewModel 中设置练习数据
//使用 collectAsState（） 从 ViewModel 观察界面状态
//将渲染委托给 ExerciseListContent

//职责分明: ExerciseScreen 负责整个锻炼屏幕的逻辑，包括与 ViewModel 的交互；ExerciseListContent 专注于根据 UI 状态渲染内容。
//异步处理与状态监控: 使用 LaunchedEffect 和状态流收集UI状态变化，确保 UI 总是显示最新的数据。
//UI 组件布局: 使用灵活的 Column 布局，针对不同 UI 状态（加载中、错误信息、锻炼内容、空状态）提供不同的视图。
//预览支持: 通过注解提供的预览功能，允许开发者快速在 IDE 中看到组件的样子，提升开发效率。

//exercise: Exercise: 传入一个 Exercise 对象，表示当前显示的锻炼信息。
//navigateBack: () -> Unit: 一个函数类型的参数，用于处理返回到上一个界面的逻辑。
@Composable
fun StaticTestScreen(exercise: StaticExercise?, navigateBack: () -> Unit) {

    // viewModel: 使用 Hilt 获取 ExerciseViewModel 的实例，以便于管理与锻炼相关的数据和业务逻辑。
    // hiltViewModel 函数在 Hilt 中被用作 ViewModel 的提供者。
    val viewModel = hiltViewModel<ExerciseViewModel>()

    //LaunchedEffect(Unit): 这是一个效果处理器，仅在初始构建时执行其内容。这里，用于设置当前锻炼对象，确保 ViewModel 知道当前需要操作的锻炼。
    //viewModel.setExercise(exercise): 将传入的 exercise 设置到 ViewModel 中，通知其更新其状态。

//    LaunchedEffect(Unit) {
//        viewModel.setExercise(exercise)
//    }


    // collectAsState(): 从 StateFlow 中收集最新的 UI 状态并自动更新，以便在用户界面中反映状态的变化。
    // 使用 by 关键字使得 uiState 变量变得观察性，当 uiState 更新时，Compose 会重新组合界面。
    val uiState by viewModel.uiState.collectAsState()

    //调用initDevice()初始化串口设备列表

    // Main Screen Content
    // ExerciseListContent(...): 调用一个用于展示锻炼内容的子组件，传递当前的 UI 状态、保存锻炼的回调函数和返回导航的回调函数。
//    ExerciseListContent(uiState, viewModel::toggleExerciseSave, navigateBack)
}


//ExerciseListContent 函数
//此专用可组合项负责处理主要界面布局和状态管理。
//
//该函数使用具有以下功能的列布局：
//
//具有导航和保存功能的自定义顶部应用栏
//基于 UI 状态的条件呈现（加载、错误、内容或空状态）

//uiState: ExerciseViewModel.ExerciseUiState: 传递 UI 状态，以便渲染锻炼的界面。
//onSaveExercise: () -> Unit: 作为保存锻炼的回调。
//navigateBack: () -> Unit: 返回操作的回调。
@Composable
private fun ExerciseListContent(
    uiState: com.ganainy.gymmasterscompose.ui.screens.exercise.ExerciseViewModel.ExerciseUiState,
    onSaveExercise: () -> Unit,
    navigateBack: () -> Unit,
) {

    //exerciseWithSaveState: 从传入的 UI 状态中提取当前的锻炼对象。
    val exerciseWithSaveState = uiState.exercise

    //Column: 使用 Column 布局管理器，以垂直方向排列其中的子组件，设置其宽高占满父容器。
    Column(modifier = Modifier.fillMaxSize()) {

        //UI 组件
        //顶部应用栏
        //该屏幕使用一个 CustomTopAppBar 组件，该组件提供：
        //
        //标题显示（“练习”）
        //带箭头图标的后退导航
        //动态保存/取消保存按钮，根据练习的保存状态而变化
        CustomTopAppBar(
            title = "Exercise",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = navigateBack,
            actionDrawables = listOf(
                if (exerciseWithSaveState?.isSavedLocally == true) R.drawable.save_filled else R.drawable.save_outlined,
            ),
            onActionClicks = listOf { onSaveExercise() },

            )

        //        内容条件渲染：
        //        if (uiState.isLoading): 如果 UI 正在加载，显示加载指示器。
        //        else if (uiState.error != null): 如果 UI 状态中存在错误消息，显示错误组件。
        //        else if (exerciseWithSaveState != null): 如果锻炼对象不为 null，则调用 ExerciseContent 来显示锻炼详细信息。
        //        else: 如果以上条件都不满足，显示一个空组件，提示 "No exercises found"。

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorComponent(text = uiState.error)
        } else if (exerciseWithSaveState != null) {
            ExerciseContent(
                exerciseWithSaveState,
            )
        } else {
            EmptyComponent("No exercises found")
        }
    }

}

// @Preview: 表明这是一个预览函数，允许在 Android Studio 中可视化该组件的 UI。
// PreviewExerciseListContent: 在该函数中创建一个包含基本状态的 ExerciseListContent 实例，
// 提供一个示例的 Exercise 对象。isLoading 设置为 false，error 为 null，使得组件能够显示正常的锻炼内容。
@Preview(showBackground = true)
@Composable
private fun PreviewExerciseListContent() {
    ExerciseListContent(
        uiState = ExerciseViewModel.ExerciseUiState(
            exercise = sampleExercise,
            isLoading = false,
            error = null
        ),
        onSaveExercise = { },
        navigateBack = { }
    )
}
