package com.ganainy.gymmasterscompose.ui.screens.create_workout

//Compose UI 基础与布局 ：导入必要的 Compose 布局组件，如 Column、LazyColumn、PaddingValues 以及布局修饰符
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
//Material3 组件 ：包括 Scaffold、TopAppBar、OutlinedTextField 以及下拉菜单组件等
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar

//Compose 运行时与状态 ：用于状态管理和组合的运行时组件
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

//平台与工具导入 ：特定平台的导入和工具，包括上下文、字符串资源以及 Hilt 依赖注入
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

//应用特定导入 ：来自应用程序的自定义模型、UI 组件和实用类
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.AppTheme
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.screens.create_workout.UiState.DataState
import com.ganainy.gymmasterscompose.ui.screens.create_workout.composables.WorkoutCoverImageContent
import com.ganainy.gymmasterscompose.ui.screens.create_workout.composables.WorkoutExerciseListScreenContent
import com.ganainy.gymmasterscompose.ui.screens.create_workout.composables.WorkoutExercisesSection
import com.ganainy.gymmasterscompose.ui.shared_components.HashtagOutlinedTextField
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import com.ganainy.gymmasterscompose.utils.MockData
import com.ganainy.gymmasterscompose.utils.MockData.sampleExercise
import com.ganainy.gymmasterscompose.utils.MockData.sampleWorkoutExercise
import com.ganainy.gymmasterscompose.utils.Utils.showToast


@Composable

//The WorkoutSetupScreen.kt 文件展示了具有清晰关注点分离的 Compose 架构。它使用了现代 Android 开发模式，包括：
//
//MVVM 架构 ：通过 ViewModel 在 UI 和业务逻辑之间实现清晰的分离
//状态管理 ：正确使用 Compose 状态集合和委托
//事件驱动设计 ：通过动作分发实现集中式事件处理
//Material Design 3: 现代 UI 组件和设计模式
//依赖注入 : 为 ViewModel 提供 Hilt 集成
//模块化组合 : 将复杂 UI 分解为更小、可重用的组合组件
//预览支持 : 开发和测试的多个预览功能
//该文件遵循 Kotlin 和 Compose 的最佳实践，具有适当的状态提升、单向数据流和清晰的组件职责。

//这一部分代码展示了如何使用 Jetpack Compose 来构建复杂的用户界面，以创建和管理锻炼计划。
// 通过使用 @Composable 注解定义组件，以及清楚地分离事件处理逻辑，代码结构整洁且易于维护。
// 该实现还充分利用了 Kotlin 的数据类和流式 API，使得 UI 与业务逻辑在结构上保持清晰的分离。

//主要可组合函数 WorkoutSetupScreen 接受多个参数，包括导航回调和带有 Hilt 注入的视图模型
//参数：WorkoutSetupScreen 函数接收一些导航函数和一个 ViewModel。
//状态流：uiState 用于观察和显示当前整体的 UI 状态。
//事件处理：onAction 定义了如何处理不同的用户操作（例如按下按钮、选择锻炼等）。
fun WorkoutSetupScreen(
    modifier: Modifier = Modifier,
    navigateToFeed: () -> Unit,
    navigateBack: () -> Unit,
    navigateToExercise: (StaticExercise) -> Unit,
    viewModel: CreateWorkoutViewModel = hiltViewModel(),
) {
    //状态管理
    //该功能使用 collectAsState() 从视图模型收集 UI 状态，并获取本地上下文
    val uiState: UiState.WorkoutUiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Event handler
    // 事件处理
    // 一个全面的事件处理函数通过 when 表达式处理各种锻炼创建动作，包括导航、锻炼管理、过滤和锻炼操作

    //在 onAction 中，我们看到了对 CreateWorkoutAction 的处理。不同事件会调用 ViewModel 的相应管理器Function，比如：
    //导航到其他屏幕：通过 navigateBack() 和 navigateToExercise() 进行导航。
    //更新锻炼或添加锻炼：通过 ViewModel 的 ExerciseManager 或 FilterManager 来处理具体的更新逻辑。
    //这种方式很清晰地将用户的操作和相应的逻辑相分离，提高了代码的可维护性。

    val onAction: (CreateWorkoutAction) -> Unit = { event ->
        Log.d("Workout", "event onAction: (CreateWorkoutAction)")
        when (event) {
            is CreateWorkoutAction.NavigateBack -> navigateBack()
            is CreateWorkoutAction.ShowFilterSheet -> {
                viewModel.filterManager.showFilterSheet()
            }

            is CreateWorkoutAction.SearchQueryChanged -> {
                viewModel.filterManager.onQueryChange(event.query)
            }

            is CreateWorkoutAction.ExerciseSelected -> {
                viewModel.exerciseManager.setSelectedExercise(event.exercise)
            }

            is CreateWorkoutAction.ExerciseModified -> {
                viewModel.exerciseManager.editWorkoutExercise(event.exercise)
            }

            is CreateWorkoutAction.ExerciseDeleted -> {
                viewModel.exerciseManager.deleteWorkoutExercise(event.exercise)
            }

            is CreateWorkoutAction.DismissAddExerciseDialog -> {
                viewModel.exerciseManager.dismissAddExerciseDialog()
            }

            is CreateWorkoutAction.AddWorkoutExercise -> {
                viewModel.exerciseManager.addWorkoutExercise(event.exercise)
            }

            is CreateWorkoutAction.BodyPartFilterChange -> {
                viewModel.filterManager.onBodyPartFilterChange(event.bodyPart)
            }

            CreateWorkoutAction.ApplyFilters -> viewModel.filterManager.applyFilters()
            is CreateWorkoutAction.EquipmentFilterChange -> {
                viewModel.filterManager.onEquipmentFilterChange(event.equipment)
            }

            is CreateWorkoutAction.TargetMuscleFilterChange -> {
                viewModel.filterManager.onTargetMuscleFilterChange(event.targetMuscle)
            }

            CreateWorkoutAction.ClearFilters -> viewModel.filterManager.clearFilters()
            is CreateWorkoutAction.EditWorkoutExercise -> {
                viewModel.exerciseManager.editWorkoutExercise(event.exercise)
            }

            is CreateWorkoutAction.NavigateToExercise -> navigateToExercise(event.exercise)
            CreateWorkoutAction.HideFilterSheet -> viewModel.filterManager.hideFilterSheet()

            is CreateWorkoutAction.ToggleExerciseWorkoutListShow -> viewModel.toggleExerciseWorkoutListShow()
            is CreateWorkoutAction.NavigateToFeed -> navigateToFeed()
            is CreateWorkoutAction.UploadWorkout -> viewModel.workoutManager.uploadWorkout(onSuccess = {
                showToast(context, context.getString(R.string.workout_uploaded_successfully))
                navigateToFeed()
            })
            is CreateWorkoutAction.EditWorkout -> viewModel.workoutManager.editWorkout(
                event.workout
            )
        }
    }



    //内容委派
    //主函数委派给 WorkoutSetupScreenContent 进行渲染
    WorkoutSetupScreenContent(uiState, onAction)

}

//布局结构
//屏幕状态管理
//内容函数通过在锻炼状态上使用模式匹配来确定要显示的 UI - 错误消息、加载指示器、锻炼设置框架或锻炼列表

//作用：该组件根据传入的 uiState 渲染不同的 UI 部分。
//状态切换：根据 workoutState 的不同状态，展示加载指示器、错误信息或主要内容。
//Scaffold 结构：使用 Scaffold 布局容器，用于放置顶部栏和内容部分。

@Composable
private fun WorkoutSetupScreenContent(
    uiState: UiState.WorkoutUiState,
    onAction: (CreateWorkoutAction) -> Unit
) {
    Log.d("Workout", "event WorkoutSetupScreenContent")
    val context = LocalContext.current

    when (val workoutState = uiState.workoutState) {

        is DataState.Error -> {
            Log.d("Workout", "DataState.Error")
            Log.d("Workout", "workoutState Error message is ${workoutState.message} " )
            showToast(context, workoutState.message)
        }

        is DataState.Loading -> {
            Log.d("Workout", "DataState.Loading")
            LoadingIndicator()

        }

        //框架布局
        //对于锻炼设置状态，一个 框架 提供了主结构，包括顶部栏和内容区域
        is DataState.WorkoutSetup -> Scaffold(
            topBar = {
                WorkoutSetupTopBar(
                    isUploadEnabled = uiState.isUploadEnabled,
                    onAction = onAction
                )
            },
            content = { paddingValues ->
                WorkoutSetupContent(
                    paddingValues = paddingValues,
                    uiState = uiState,
                    onAction = onAction,
                )
            }
        )

        is DataState.ExerciseListSetup -> WorkoutExerciseListScreenContent(uiState, onAction)
    }
}

//顶部栏组件
//顶部栏包含标题和根据 UI 状态启用的上传按钮
//布局：创建顶栏，包含标题和一个“上传”按钮。
//上传按钮逻辑：判断按钮是否可以点击，基于 isUploadEnabled 状态。

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSetupTopBar(isUploadEnabled: Boolean, onAction: (CreateWorkoutAction) -> Unit) {
    TopAppBar(
        title = { Text("Create New Workout") },
        actions = {
            TextButton(
                onClick = {
                    onAction(CreateWorkoutAction.UploadWorkout)
                },
                enabled = isUploadEnabled
            ) {
                Text("Upload")
            }
        }
    )
}

// LazyColumn：使用 LazyColumn 展示可滚动的项目列表。
// 内容项：包含封面、锻炼的基本信息、难度选择下拉框，和程序员定义的其他组件，便于用户配置锻炼信息。
@Composable
private fun WorkoutSetupContent(
    modifier: Modifier = Modifier,
    uiState: UiState.WorkoutUiState,
    onAction: (CreateWorkoutAction) -> Unit,
    paddingValues: PaddingValues
) {
    //内容布局
    //主要内容使用带有适当填充和间距的 LazyColumn 来显示锻炼设置组件
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        //LazyColumn 包含封面图片、基本锻炼信息、难度选择和锻炼部分
        item {
            WorkoutCoverImageContent(workout = uiState.workout,
                onEditWorkout = { onAction(CreateWorkoutAction.EditWorkout(it)) })
        }

        item {
            WorkoutBasicInfo(
                workout = uiState.workout,
                onEditWorkout = { onAction(CreateWorkoutAction.EditWorkout(it)) })
        }
        item {
            DifficultyDropdownMenu(
                difficulties = uiState.availableDifficulties,
                onEditWorkout = { onAction(CreateWorkoutAction.EditWorkout(it)) },
                workout = uiState.workout
            )
        }


        item {
            WorkoutExercisesSection(
                exerciseList = uiState.workout.workoutExerciseList,
                onDeleteExercise = {
                    onAction(
                        CreateWorkoutAction.ExerciseDeleted(
                            it
                        )
                    )
                },
                toggleExerciseWorkoutListShow = { onAction(CreateWorkoutAction.ToggleExerciseWorkoutListShow) }
            )

        }
    }
}



//支持组件
//基本信息组件
//The WorkoutBasicInfo 组合式处理标题、描述和时长输入字段，并对数值输入进行适当的验证
// 功能：用于展示锻炼的基本信息，包含标题、描述和时长字段，并允许用户编辑。
// 交互：输入框更新时会触发 onEditWorkout，更新 Workout 对象。

@Composable
private fun WorkoutBasicInfo(workout: Workout, onEditWorkout: (Workout) -> Unit) {
    Column {
        HashtagOutlinedTextField(
            value = workout.title,
            onValueChange = { onEditWorkout(workout.copy(title = it)) },
            label = stringResource(R.string.workout_title),
            modifier = Modifier.fillMaxWidth()
        )

        HashtagOutlinedTextField(
            value = workout.description,
            onValueChange = { onEditWorkout(workout.copy(description = it)) },
            label = stringResource(R.string.description),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = workout.trainingTime,
            onValueChange = {
                if (it.toIntOrNull() != null) {
//                    onEditWorkout(workout.copy(workoutDuration = it))
                    onEditWorkout(workout.copy(trainingTime = it))
                }
            },
            label = { Text(stringResource(R.string.duration_minutes)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )


    }
}

// DifficultyDropdownMenu 实现了一个 Material3 暴露的下拉菜单，带有状态管理，用于展开和选择
// 下拉菜单：使用 ExposedDropdownMenuBox 显示可选难度，以便用户选择。
// 状态管理：通过 remember 管理展开状态，并在用户选择时更新。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultyDropdownMenu(
    difficulties: List<String>,
    onEditWorkout: (Workout) -> Unit,
    workout: Workout
) {
    var expanded by remember { mutableStateOf(false) } // State to manage dropdown visibility

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }, // Toggle expanded state
    ) {
        OutlinedTextField(
            value = workout.difficulty,
            onValueChange = {}, // No need to handle this since it's readOnly
            readOnly = true, // Make the text field read-only
            label = { Text("Difficulty Level") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // Anchor for the dropdown
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Close the dropdown on dismiss
        ) {
            difficulties.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty) },
                    onClick = {
                        onEditWorkout(workout.copy(difficulty = difficulty)) // Update the ViewModel
                        expanded = false // Close the dropdown
                    }
                )
            }
        }
    }
}

//预览功能
//提供多种预览功能以在开发过程中可视化屏幕的不同状态
// 功能：这些预览提供了在 IDE 中可视化测试组件的方式，帮助开发者调整 UI，而无需在真实设备上运行应用。
@Preview(showBackground = true)
@Composable
private fun PreviewWorkoutSetupContentNoExercises() {
    AppTheme {
        WorkoutSetupScreenContent(
            uiState = UiState.WorkoutUiState(workoutState = DataState.WorkoutSetup),
            onAction = {},
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun WorkoutSetupContentWithExercisesPreview2() {
    AppTheme {
        WorkoutSetupScreenContent(
            uiState = UiState.WorkoutUiState(
                workoutState = DataState.ExerciseListSetup,
                workout = MockData.sampleWorkout,
                filteredExerciseList = MockData.sampleExerciseListLarge
            ),
            onAction = {},
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun WorkoutExercisesSectionPreview() {
    AppTheme {
        WorkoutExercisesSection(
            exerciseList = listOf(
             sampleWorkoutExercise,
                sampleWorkoutExercise.copy(exercise = sampleExercise.copy(name = "Exercise 2"))
            ),
            onDeleteExercise = { /* Handle delete */ },
            toggleExerciseWorkoutListShow = { /* Handle toggle */ }
        )
    }
}