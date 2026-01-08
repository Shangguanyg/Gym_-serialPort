package com.ganainy.gymmasterscompose.ui.screens.static_test

import android.content.Intent
import android.os.Handler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.screens.exercise.ExerciseViewModel
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar
import com.ganainy.gymmasterscompose.utils.MockData.sampleExercise
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import androidx.compose.foundation.layout.Box
import com.breens.beetablescompose.BeeTablesCompose
import androidx.compose.ui.text.TextStyle
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaLayerRenderDelegate
import android.widget.FrameLayout
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import android.os.Looper
import androidx.compose.material3.RadioButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.material3.Card
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import com.ganainy.gymmasterscompose.ui.screens.static_test.StaticTestResultViewModel
import com.ganainy.serialportlibrary.enumerate.SerialStatus


/**
 * 🐝 A Compose UI data table library.
 *
 * @param data The list of data items to display in the table.
 * @param enableTableHeaderTitles show or hide the table header titles. If not set, by default the table header titles will be shown.
 * @param headerTableTitles The list of header titles to display at the top of the table.
 * @param headerTitlesBorderColor The color of the border for the header titles, by default it will be [Color.LightGray].
 * @param headerTitlesBorderWidth The width of the border for the header titles in DP, by default it will be "0.4.dp".
 * @param headerTitlesTextStyle The text style to apply to the header titles, by default it will be [MaterialTheme.typography.bodySmall].
 * @param headerTitlesBackGroundColor The background color for the header titles, by default it will be [Color.White].
 * @param tableRowColors The list of background colors to alternate between rows in the table, by default it will be a list of: [Color.White], [Color.White].
 * @param rowBorderColor The color of the border for the table rows, by default it will be [Color.LightGray].
 * @param rowBorderWidth The width of the border for the table rows in DP, by default it will be "0.4.dp".
 * @param rowTextStyle The text style to apply to the data cells in the table rows, by default it will be [MaterialTheme.typography.bodySmall].
 * @param tableElevation The elevation of the entire table (Card elevation) in DP, by default it will be "6.dp".
 * @param shape The shape of the table's corners, by default it will be "RoundedCornerShape(4.dp)".
 * @param disableVerticalDividers show or hide the vertical dividers between the table cells. If not set, by default the vertical dividers will be shown.
 * @param horizontalDividerThickness The thickness of the horizontal dividers in DP, by default it will be "1.dp". Note: This will only be visible if [disableVerticalDividers] is set to true.
 * @param horizontalDividerColor The color of the horizontal dividers, by default it will be [Color.LightGray]. Note: This will only be visible if [disableVerticalDividers] is set to true.
 * @param contentAlignment The alignment of the content in the table cells, by default it will be [Alignment.Center].
 * @param textAlign The alignment of the text in the table cells, by default it will be [TextAlign.Center].
 */


@OptIn(ExperimentalMaterial3Api::class)
//主要 ExerciseScreen 功能
//ExerciseScreen 函数用作入口点并处理 ViewModel 集成
//主要职责：
//
//使用 Hilt 依赖项注入初始化 ExerciseViewModel
//使用 LaunchedEffect 在 ViewModel 中设置练习数据
//使用 collectAsState（） 从 ViewModel 观察界面状态
//将渲染委托给 ExerciseListContent

data class SampleData(val name: String, val age: Int, val city: String)

data class TestData(val name: String, val value: Int)


//职责分明: ExerciseScreen 负责整个锻炼屏幕的逻辑，包括与 ViewModel 的交互；ExerciseListContent 专注于根据 UI 状态渲染内容。
//异步处理与状态监控: 使用 LaunchedEffect 和状态流收集UI状态变化，确保 UI 总是显示最新的数据。
//UI 组件布局: 使用灵活的 Column 布局，针对不同 UI 状态（加载中、错误信息、锻炼内容、空状态）提供不同的视图。
//预览支持: 通过注解提供的预览功能，允许开发者快速在 IDE 中看到组件的样子，提升开发效率。

//exercise: Exercise: 传入一个 Exercise 对象，表示当前显示的锻炼信息。
//navigateBack: () -> Unit: 一个函数类型的参数，用于处理返回到上一个界面的逻辑。
@Composable
fun StaticTestResultScreen(exercise: StaticExercise?, navigateBack: () -> Unit) {

    // viewModel: 使用 Hilt 获取 ExerciseViewModel 的实例，以便于管理与锻炼相关的数据和业务逻辑。
    // hiltViewModel 函数在 Hilt 中被用作 ViewModel 的提供者。
    val viewModel = hiltViewModel<StaticTestResultViewModel>()

    val context = LocalContext.current

    val serialPort = remember { SerialPort() }

    var receivedData by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    // 3. 初始化串口
    LaunchedEffect(Unit) {
        serialPort.openSerialPort(object : SerialPort.SerialPortCallback {
            override fun onStatusChanged(success: Boolean, status: SerialStatus, message: String) {
                isConnected = success
                statusMessage = message
            }

            override fun onDataReceived(data: ByteArray) {
                receivedData = String(data)
            }

            override fun onDataSent(data: ByteArray) {
                // 发送完成回调
            }
        })
    }

    // 4. 清理资源
    DisposableEffect(Unit) {
        onDispose {
            serialPort.closeSerialPort()
        }
    }



//    val CoPPoint: MutableState<Int> =   mutableStateOf(0)
//
//    val EnclosingArea: MutableState<Int> = remember { mutableStateOf(0) }
//
//    var TotalLength: MutableState<Int> = remember { mutableStateOf(0) }
//
//    var HorizontalLength: MutableState<Int> = remember { mutableStateOf(0) }
//
//    var VerticalLength: MutableState<Int> = remember { mutableStateOf(0) }
//
//    var TotalSpeed: MutableState<Int> = remember { mutableStateOf(0) }
//
//    var HorizontalSpeed: MutableState<Int> = remember { mutableStateOf(0) }
//
//    var VerticalSpeed: MutableState<Int> = remember { mutableStateOf(0) }




    //LaunchedEffect(Unit): 这是一个效果处理器，仅在初始构建时执行其内容。这里，用于设置当前锻炼对象，确保 ViewModel 知道当前需要操作的锻炼。
    //viewModel.setExercise(exercise): 将传入的 exercise 设置到 ViewModel 中，通知其更新其状态。
//    LaunchedEffect(Unit) {
//        val intent = Intent(context, SpecialChartActivity::class.java)
//        intent.putExtra("chartType", AAChartType.Line.value)
//        context.startActivity(intent)
//    }


    // collectAsState(): 从 StateFlow 中收集最新的 UI 状态并自动更新，以便在用户界面中反映状态的变化。
    // 使用 by 关键字使得 uiState 变量变得观察性，当 uiState 更新时，Compose 会重新组合界面。
    val uiState by viewModel.uiState.collectAsState()


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3.5f),
            horizontalArrangement = Arrangement.SpaceBetween // 可选：设置子元素保持水平间距
        ) {
            Box(
                modifier = Modifier
                .weight(1.5f) // 使其占用一等份宽度
                    .fillMaxHeight() // 确保填满父容器高度
                    .padding(8.dp), // 可选：设置内边距
                contentAlignment = Alignment.Center
            ) {
//                AndroidView(
//                    modifier = Modifier.fillMaxWidth(),
//                    factory = { context ->
//                        // 从XML加载布局
//                        val layoutInflater = LayoutInflater.from(context)
//                        val rootView = layoutInflater.inflate(R.layout.activity_special_chart, null)
//                        // 获取AAChartView引用
//                        val aaChartView = rootView.findViewById<AAChartView>(R.id.AAChartView)
//                        // 配置图表
//                        val chartModel = SpecialChartComposer.configurePolarBarChart()
//                        aaChartView.aa_drawChartWithChartModel(chartModel)
//                        rootView
//                    }
//                )
                TestControl()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    if (receivedData.isEmpty()) {
                        // 未接收到数据时显示提示
                        Text(
                            text = "等待串口数据...",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        // 显示接收到的数据
                        Text(
                            text = receivedData,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight() // 确保填满父容器高度
                    .padding(8.dp), // 可选：设置内边距
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth(),
                    factory = { context ->
                        FrameLayout(context).apply {
                            val skiaLayer = SkiaLayer()
                            skiaLayer.renderDelegate =
                                SkiaLayerRenderDelegate(skiaLayer, Boards(skiaLayer, this))
                            skiaLayer.attachTo(this)
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
          //  horizontalArrangement = Arrangement.SpaceBetween // 可选：设置子元素保持水平间距
        ) {
            Box(
                modifier = Modifier
                    .weight(1f) // 使其占用一等份宽度
                    .fillMaxHeight(), // 确保填满父容器高度
                contentAlignment = Alignment.Center
            ){ Column{
                TestDataDisplay("中心坐标",remember { viewModel.CoPPoint })
                TestDataDisplay("包络面积",remember { viewModel.EnclosingArea})
            }
            }
            Box(
                modifier = Modifier
                    .weight(1f) // 使其占用一等份宽度
                    .fillMaxHeight(), // 确保填满父容器高度
                contentAlignment = Alignment.Center
            ){
                Column{
                    TestDataDisplay("轨迹总长度",remember { viewModel.TotalLength})
                    TestDataDisplay("左右方向轨迹长度",remember { viewModel.HorizontalLength})
                    TestDataDisplay("前后方向轨迹长度",remember { viewModel.VerticalLength})
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f) // 使其占用一等份宽度
                    .fillMaxHeight(), // 确保填满父容器高度
                contentAlignment = Alignment.Center
            ){
                Column {
                    TestDataDisplay("总摇动平均速度", remember { viewModel.TotalSpeed})
                    TestDataDisplay("左右方向摇动平均速度", remember { viewModel.HorizontalSpeed})
                    TestDataDisplay("前后方向摇动平均速度", remember { viewModel.VerticalSpeed})
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f) // 使其占用一等份宽度
                    .fillMaxHeight(), // 确保填满父容器高度
                contentAlignment = Alignment.Center
            ){
                Column {
                    TestDataDisplay("摇动总标准差", remember { viewModel.TotalMSD})
                    TestDataDisplay("左右方向摇动标准差", remember { viewModel.HorizontalMSD})
                    TestDataDisplay("前后方向摇动标准差度", remember { viewModel.VerticalMSD})
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f) // 使其占用一等份宽度
                    .fillMaxHeight(), // 确保填满父容器高度
                contentAlignment = Alignment.Center
            ){
                Column {
                    TestDataDisplay("左右方向中间频率", remember { viewModel.HorizontalFrequency})
                    TestDataDisplay("前后方向中间频率", remember { viewModel.VerticalFrequency})
                }
            }
        }
    }

    // Main Screen Content
    // ExerciseListContent(...): 调用一个用于展示锻炼内容的子组件，传递当前的 UI 状态、保存锻炼的回调函数和返回导航的回调函数。
    // ExerciseListContent(uiState, viewModel::toggleExerciseSave, navigateBack)
}

fun configurePolarColumnChart(): AAChartModel {
    return AAChartModel()
        .chartType(AAChartType.Column)
        .polar(true)
        .dataLabelsEnabled(false)
        .categories(arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"))
        .series(arrayOf(
            AASeriesElement()
                .name("2018")
                .colorByPoint(true)
                .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6))
        ))
}

@Composable
fun configurePolarBarChart(): AAChartModel {
    return AAChartModel()
        .chartType(AAChartType.Bar)
        .polar(true)
        .dataLabelsEnabled(false)
        .categories(arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"))
        .series(arrayOf(
            AASeriesElement()
                .name("2018")
                .colorByPoint(true)
                .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6))
        ))
}

@Composable
fun TestControl(){

    var secondsElapsed by remember { mutableStateOf(0) } // 计时器秒数

    var testTime: TextView? = null

    var testScore: TextView? = null

    var timerHandler = remember { Handler(Looper.getMainLooper()) }

    var timerRunnable: Runnable? = remember { Runnable {} } // Initialize as an empty Runnable

    var progress by remember { mutableStateOf(0f) } // 存储当前进度

    var isTimerRunning by remember { mutableStateOf(false) } // 状态变量，表明计时器是否在运行

    val viewModel = hiltViewModel<StaticTestResultViewModel>()


    val startTimer: () -> Unit = {
        // 启动计时器的逻辑
        // 可以添加代码来实现计时的功能
        println("计时器已启动")

        secondsElapsed = 0 // Reset the timer every time it starts
        timerRunnable = object : Runnable {
            override fun run() {
                secondsElapsed++
                // Update the UI with the new time
                timerHandler.postDelayed(this, 1000) // Repeat every second
                progress = secondsElapsed / 60f
                viewModel.CoPPoint.value++
                viewModel.EnclosingArea.value++

            }
        }

        timerHandler.post(timerRunnable!!) // 启动计时器
    }

    val stopTimer: () -> Unit = {
        // 停止计时器的逻辑
        // 可以添加代码来实现停止计时的功能
        timerRunnable?.let { timerHandler.removeCallbacks(it) } // Stop the timer
        secondsElapsed = 0 // Reset to zero when stopped
    }


    // 使用 Column 来垂直排列按钮
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // 添加内边距
        verticalArrangement = Arrangement.Center, // 垂直居中
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {


        Text(text = "人员姓名：张三", fontSize = 32.sp)

        TestTypeGroup()

        Spacer(modifier = Modifier.height(16.dp)) // Add some space between text and buttons

        // Text for displaying test time
        Text(text = "测试时间：$secondsElapsed 秒")

        Spacer(modifier = Modifier.height(16.dp)) // Add some space between text and buttons

        LinearProgressIndicator(
            progress = { progress }, // 将进度传入
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp), // 设置高度为 8dp
           //  color = MaterialTheme.colorScheme.onPrimaryContainer // 进度条颜色设置为绿色
             color = Color.Green
        )

        Spacer(modifier = Modifier.height(16.dp)) // Add some space between text and buttons

        Button(
            onClick = {
                startTimer() // 启动计时器
            },
            modifier = Modifier
                .padding(8.dp) // 添加按钮的外边距
                .fillMaxWidth() // 按钮宽度填满
        ) {
            Text(text = "开始计时")
        }

        Button(
            onClick = {
                stopTimer() // 停止计时器
            },
            modifier = Modifier
                .padding(8.dp) // 添加按钮的外边距
                .fillMaxWidth() // 按钮宽度填满
        ) {
            Text(text = "停止计时")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add some space between text and buttons

        //var CoPPoint = MutableState()

        TestDataDisplay("总体重",remember { viewModel.TotalWeight })
        TestDataDisplay("左足压力",remember { viewModel.LeftWeight })
        TestDataDisplay("右足压力",remember { viewModel.RightWeight })
        TestDataDisplay("测试得分",remember { viewModel.TestScore })
    }
}

@Composable
fun MyDataTable1() {
val testDatalist = listOf(
    TestData("中心坐标",1),
    TestData("包络面积",1),
    TestData("X轴方向标准差",1),
    TestData("Y轴方向标准差",1),
    TestData("轨迹长度",1),
    TestData("X轴方向速度",1),
    TestData("Y轴方向速度",1),
)
val headerritles = listOf ("项目", "数值")
// Customize Table to your preference
BeeTablesCompose (
data = testDatalist,
enableTableHeaderTitles = false,
headerTableTitles = headerritles,
headerTitlesBorderColor = Color.LightGray,
//headerTitlesBorderWidth = 1.dp,
headerTitlesTextStyle = TextStyle(fontSize = 14.sp),
headerTitlesBackGroundColor = Color.White,
tableRowColors = listOf (Color.White, Color.White),
rowBorderColor = Color.LightGray,
//rowBorderWidth = 1.dp,
rowTextStyle = TextStyle(fontSize = 8.sp),
tableElevation = 2.dp,
//shape: RoundedCornerShape = RoundedCornerShape(4.dp)
)

}

@Composable
fun PolarColumnChart() {
    // 创建图表模型
//    val polarChartModel = configurePolarColumnChart()

    val aaChartModel : AAChartModel = AAChartModel()

    // 使用 AndroidView 来显示 AAChartView
    val context = LocalContext.current
    AndroidView(
        factory = {
            AAChartView(context).apply {
 //               aaChartModel = polarChartModel // 设置图表模型
 //               Log.d("AAChart", "Chart Model set with categories: ${polarChartModel.categories.joinToString()}")
            }
        },
        modifier = Modifier.fillMaxSize() // 避免大小问题
    )
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
    uiState: ExerciseViewModel.ExerciseUiState,
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

//        TextButton(
//            onClick = {
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "静态测试结果")
//        }

//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center // 将子元素居中
//        ) {
//            // 显示极坐标柱状图
//            PolarColumnChart()
//        }


        //        内容条件渲染：
        //        if (uiState.isLoading): 如果 UI 正在加载，显示加载指示器。
        //        else if (uiState.error != null): 如果 UI 状态中存在错误消息，显示错误组件。
        //        else if (exerciseWithSaveState != null): 如果锻炼对象不为 null，则调用 ExerciseContent 来显示锻炼详细信息。
        //        else: 如果以上条件都不满足，显示一个空组件，提示 "No exercises found"。

//        if (uiState.isLoading) {
//            LoadingIndicator()
//        } else if (uiState.error != null) {
//            ErrorComponent(text = uiState.error)
//        } else if (exerciseWithSaveState != null) {
//            ExerciseContent(
//                exerciseWithSaveState,
//            )
//        } else {
//            EmptyComponent("No exercises found")
//        }
        }

}

@Composable
fun TestTypeGroup() {
    // 存储当前选中的选项
    var selectedOption by remember { mutableStateOf("") }

    // 定义选项
    val Option1 = listOf(
        "睁眼双足站立",
        "闭眼双足站立",
    )

    val Option2 = listOf(
        "睁眼左足站立",
        "闭眼左足站立",
    )

    val Option3 = listOf(
        "睁眼右足站立",
        "闭眼右足站立"
    )

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween // 两列之间的间隔
    ) {
        // 左侧选项列
        Column(
            modifier = Modifier.weight(1f), // 使其占用一份宽度
            verticalArrangement = Arrangement.Top // 上对齐
        ) {
            // 遍历左侧选项并渲染每个 RadioButton
            Option1.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp), // 设置上下间距为 2dp
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == option),
                        onClick = {
                            selectedOption = option
                        }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = option)
                }
            }
        }

        // 右侧选项列
        Column(
            modifier = Modifier.weight(1f), // 使其占用一份宽度
            verticalArrangement = Arrangement.Top // 上对齐
        ) {
            // 遍历右侧选项并渲染每个 RadioButton
            Option2.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp), // 设置上下间距为 2dp
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == option),
                        onClick = {
                            selectedOption = option
                        }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = option)
                }
            }
        }
        // 右侧选项列
        Column(
            modifier = Modifier.weight(1f), // 使其占用一份宽度
            verticalArrangement = Arrangement.Top // 上对齐
        ) {
            // 遍历右侧选项并渲染每个 RadioButton
            Option3.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp), // 设置上下间距为 2dp
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == option),
                        onClick = {
                            selectedOption = option
                        }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = option)
                }
            }
        }
    }
}


@Composable
fun TestDataDisplay(label: String, value: MutableState<Int>) {
    // 用于存储动态值
    val trackLength = remember { mutableStateOf("0 m") } // 初始值为 0

    Row(
        modifier = Modifier
            .width(315.dp) // 设置整个组件的宽度
            .padding(vertical = 6.dp) // 设置纵向边距为 10dp
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧固定的文字
        Text(
            text = label,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f) // 占用剩余空间
        )
        Spacer(modifier = Modifier.width(6.dp)) // 添加横向间距为 6dp
        // 右侧的方框显示当前数值
        Card(
            modifier = Modifier
                .size(60.dp, 40.dp), // 固定大小的方框
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // 采用 CardDefaults 设置阴影
            border = BorderStroke(1.dp, Color.Gray) // 可选，添加边框
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize() // 填充卡片区域
            ) {
                Text(text = "${value.value}") // 显示的数值
            }
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
