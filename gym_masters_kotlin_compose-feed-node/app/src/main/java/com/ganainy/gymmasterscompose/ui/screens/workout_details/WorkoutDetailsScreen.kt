package com.ganainy.gymmasterscompose.ui.screens.workout_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.screens.workout_list.WorkoutWithStatus
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar
import com.ganainy.gymmasterscompose.ui.shared_components.EmptyComponent
import com.ganainy.gymmasterscompose.ui.shared_components.ErrorComponent
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import com.ganainy.gymmasterscompose.utils.MockData.sampleWorkoutExerciseList
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity

import com.ganainy.balancetheball.BalanceTheBallActivity
import com.ganainy.galaxyrun.activity.GalaxyRunActivity
import com.ganainy.spacedude.SpaceDudeActivity
import com.ganainy.spacedude.SplashActivity
import com.ganainy.galaxyrun.activity.GameActivity
import com.ganainy.DroidJet.DroidJetActivity
import com.ganainy.BalanceBall.BouncingBallActivity
import com.ganainy.AndroidBall.BallActivity
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.ganainy.gymmasterscompose.ui.screens.static_test.OnlyRefreshChartDataActivity
import com.ganainy.gymmasterscompose.ui.screens.static_test.SpecialChartActivity
import com.ganainy.racingcar.ui.RacingCarActivity
import com.ganainy.pixelwheels.android.main.AndroidLauncher
import com.ganainy.LD47.LD47Launcher
//import com.ganainy.FishingGame.OtherActivity
import com.ganainy.FishingGame.FishingActivity
import com.ganainy.bikemessenger.MenuActivity
import com.ganainy.bikeracing.BikeRacingLauncher
import com.ganainy.kugel.KugelActivity
import com.ganainy.supersnake.SuperSnakeLauncher
import com.ganainy.flyingfish.StartFishingActivity
import com.ganainy.EatFishGame.EatFishActivity
import com.ganainy.motoman.MotomanActivity
import com.ganainy.PortApplication.SingleSerialPortActivity
//@Composable: 这表明这是一个可组合函数，可以用于构建 UI。
//参数:
//workout: Workout: 接收锻炼对象的实例，用于显示详细信息。
//isLiked: Boolean: 表示该锻炼是否被用户喜欢。
//isSaved: Boolean: 表示该锻炼是否被用户保存。
//navigateBack: () -> Unit: 用于处理返回到上一个屏幕的操作。
//navigateToExerciseDetails: (Exercise) -> Unit: 用于处理导航到锻炼详细信息的操作，接收一个锻炼对象作为参数。
@Composable
fun WorkoutDetailsScreen(
    workout: Workout,
    isLiked: Boolean,
    isSaved: Boolean,
    navigateBack: () -> Unit,
    navigateToExerciseDetails: (StaticExercise) -> Unit,
    navigateToRacingGame: () -> Unit,
    navigateToSpaceGame: () -> Unit,
    navigateToBalanceBoardControl: () -> Unit,
    navigateToStaticTest: () -> Unit,
    navigateToStaticTestResult: () -> Unit
) {

    //获取 ViewModel：使用 Hilt 通过 hiltViewModel() 方法获得 WorkoutDetailsViewModel 实例，用于管理该界面的逻辑。
    val viewModel: WorkoutDetailsViewModel = hiltViewModel()

    //状态流收集：使用 collectAsState() 从 ViewModel 中收集 UI 状态，并自动更新 UI，确保界面反映最新状态。
    val uiState by viewModel.uiState.collectAsState()

    //LaunchedEffect(Unit): 表示这个代码块在首次组合时运行，执行一些副作用，比如设置初始的锻炼详细信息。
    //viewModel.setWorkoutDetails(...): 将传入的锻炼及其状态（是否被喜欢、是否被保存）传递给 ViewModel，进行初始化。
    //viewModel.observeWorkoutChanges(): 可能是用来观察锻炼数据的变化，确保 UI 可以响应任何后续的更新。
    LaunchedEffect(Unit) {
        viewModel.setWorkoutDetails(
            workout = workout, isLiked = isLiked, isSaved = isSaved
        )
        viewModel.observeWorkoutChanges()
    }

    //调用内容组件: 使用当前的 UI 状态和导航函数，调用 WorkoutDetailsContent 来呈现详细的锻炼信息。
    WorkoutDetailsContent(uiState, navigateBack, navigateToExerciseDetails,navigateToRacingGame,navigateToSpaceGame, navigateToBalanceBoardControl,navigateToStaticTest,navigateToStaticTestResult)
}

//@Composable: 表示是一个可组合函数。
//参数:
//uiState: WorkoutDetailsUiData: 接收包含锻炼详细信息及状态的对象。
//navigateBack: () -> Unit: 处理返回操作的函数。
//navigateToExerciseDetails: (Exercise) -> Unit: 导航到锻炼详细信息的处理函数。
@Composable
private fun WorkoutDetailsContent(
    uiState: WorkoutDetailsUiData,
    navigateBack: () -> Unit,
    navigateToExerciseDetails: (StaticExercise) -> Unit,
    navigateToRacingGame: () -> Unit,
    navigateToSpaceGame: () -> Unit,
    navigateToBalanceBoardControl: () -> Unit,
    navigateToStaticTest: () -> Unit,
    navigateToStaticTestResult: () -> Unit
) {
    val context = LocalContext.current
    //Column: 使用 Column 布局垂直排列子组件。
    //CustomTopAppBar(...): 自定义的顶部应用栏，设置标题为 “Workout Details”，并包含返回图标和点击事件处理。
    Column {
        CustomTopAppBar(
            title =  "训练记录详情",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = { navigateBack() },
        )
//        TextButton(
//            onClick = {
//                navigateToRacingGame()
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "开始赛车游戏")
//        }
        TextButton(
            onClick = {
                val intent = Intent(context, RacingCarActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始赛车游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, AndroidLauncher::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始赛车游戏1")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, MotomanActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始摩托游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, LD47Launcher::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始赛车游戏2")
        }
//        TextButton(
//            onClick = {
//                navigateToSpaceGame()
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "开始太空游戏")
//        }
        TextButton(
            onClick = {
                val intent = Intent(context, SpaceDudeActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始太空游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, BouncingBallActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始平衡球游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, GalaxyRunActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始跑酷游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, DroidJetActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始机器人喷气机游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, StartFishingActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始飞鱼游戏")
        }

        TextButton(
            onClick = {
                val intent = Intent(context, EatFishActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始吃鱼游戏")
        }

//        TextButton(
//            onClick = {
//                val intent = Intent(context, BikeRacingLauncher::class.java)
//                startActivity(context,intent,null)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "开始自行车游戏1")
//        }



//        TextButton(
//            onClick = {
//                val intent = Intent(context, BalanceTheBallActivity::class.java)
//                startActivity(context,intent,null)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "开始平衡球游戏2")
//        }
        TextButton(
            onClick = {
                val intent = Intent(context, BallActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始平衡球游戏3")
        }

//        TextButton(
//            onClick = {
//                val intent = Intent(context, FishingActivity::class.java)
//                startActivity(context,intent,null)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "开始钓鱼游戏")
//        }
        TextButton(
            onClick = {
                val intent = Intent(context, KugelActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始凯格尔球游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, SuperSnakeLauncher::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始贪吃蛇游戏")
        }
        TextButton(
            onClick = {
                val intent = Intent(context, SingleSerialPortActivity::class.java)
                startActivity(context,intent,null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始串口测试")
        }

//        TextButton(
//            onClick = {
//                val intent = Intent(context, MenuActivity::class.java)
//                startActivity(context,intent,null)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "开始自行车游戏")
//        }


        TextButton(
            onClick = {
                navigateToBalanceBoardControl()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始平衡测试")
        }
        TextButton(
            onClick = {
//                val intent = Intent(
//                    context,
//                    OnlyRefreshChartDataActivity::class.java
//                )
                val intent = Intent(
                    context,
                    SpecialChartActivity::class.java
                )
//                intent.putExtra("chartType", AAChartType.Scatter.value)
                intent.putExtra("chartType", AAChartType.Line.value)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始静态测试")
        }
        TextButton(
            onClick = {
                navigateToStaticTestResult()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "静态测试结果")
        }



        //if (uiState.isLoading): 检查 UI 状态是否在加载中，如果是，则显示加载指示器。
        if (uiState.isLoading) {
            LoadingIndicator()
        }
        ////if (uiState.error != null): 检查 UI 状态是否有错误信息，如果有，则显示错误组件，可能包含重试逻辑。
        if (uiState.error != null) {
            ErrorComponent(
                text = uiState.error,
                onRetryClick = {},
            )
        } else {
            //嵌套的条件检查:
            //if (uiState.workoutWithStatus == null): 如果锻炼状态对象为空，则展示一个提示，表明没有找到锻炼数据。
            //else: 否则，获取实际的 workoutWithStatus 对象，准备在 UI 中显示锻炼详细信息。
            if (uiState.workoutWithStatus == null) {
                EmptyComponent(
                    text = stringResource(R.string.no_workout_found),
                )
            } else {
                val workoutWithStatus = uiState.workoutWithStatus

                //WorkoutDetailedComposable(...): 调用一个组合函数以展示锻炼的详细信息。
                //workoutWithStatus: 将获得的锻炼对象传递给该组件。
                //detailedOnlyParams: 可以指定一些额外的参数，例如点击锻炼的处理逻辑。
                WorkoutDetailedComposable(
                    workoutWithStatus = workoutWithStatus,
                    detailedOnlyParams = DetailedOnlyParams(
                        onExerciseClick = navigateToExerciseDetails,
                    ),
                )

            }


        }
    }
}

//@Preview: 允许在 Android Studio 中为组件提供预览。
//WorkoutDetailsContent_Preview(...): 定义了一个预览函数，设置默认的 UI 状态以展示一个示例的锻炼内容。
//参数:
//uiState: 为预览定义的状态，包含一个示例锻炼对象及其各种状态。
//空的回调函数: 对于导航参数，提供默认的空实现以允许预览。
@Preview(showBackground = false)
@Composable
private fun WorkoutDetailsContent_Preview(
    uiState: WorkoutDetailsUiData = WorkoutDetailsUiData(
        workoutWithStatus = WorkoutWithStatus(
            workout = Workout(
                id = "1",
                title = "Sample Workout",
                description = "This is a sample workout description",
                trainingTime = "30m",
                workoutExerciseList = sampleWorkoutExerciseList
            ), isLiked = false, isSaved = false
        ), isLoading = false, error = null
    ), navigateBack: () -> Unit = {},
       navigateToExerciseDetails: (StaticExercise) -> Unit = {},
       navigateToRacingGame: () -> Unit = {},
       navigateToSpaceGame: () -> Unit = {},
       navigateToBalanceBoardControl: () -> Unit = {},
       navigateToStaticTest: () -> Unit = {},
       navigateToStaticTestResult: () -> Unit = {}
) {
    WorkoutDetailsContent(
        uiState = uiState,
        navigateBack = navigateBack,
        navigateToExerciseDetails = navigateToExerciseDetails,
        navigateToRacingGame = navigateToRacingGame,
        navigateToSpaceGame = navigateToSpaceGame,
        navigateToBalanceBoardControl = navigateToBalanceBoardControl,
        navigateToStaticTest = navigateToStaticTest,
        navigateToStaticTestResult = navigateToStaticTestResult
    )
}