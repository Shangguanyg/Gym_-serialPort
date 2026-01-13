package com.ganainy.gymmasterscompose.ui.screens.static_test

import android.os.Handler
import android.os.Looper
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
//import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import kotlin.math.cos
import kotlin.math.sin


@HiltViewModel
//ViewModel 具有通过 Hilt 的依赖项注入注入的单个依赖项：
class StaticTestViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository
) : ViewModel() {

// ExerciseViewModel 是一个 Hilt 注入的 ViewModel，它扩展了 Android 的 ViewModel 类并管理 Exercise 屏幕的状态。
// ExerciseViewModel 遵循 Android 的 MVVM 架构模式，并利用现代 Android 开发实践，
// 包括用于依赖项注入的 Hilt、用于异步作的 Kotlin 协程以及用于响应式界面更新的 Jetpack Compose。
// ViewModel 通过将数据作委托给存储库层来保持关注点的分离，同时管理特定于 UI 的状态和业务逻辑。
// 整个系统中使用的练习模型是一个 Room 实体，同时支持 API 数据和本地存储功能。


        //状态管理
        //ViewModel 使用 Kotlin 协程和 Flow 实现响应式状态管理。它维护一个私有的可变状态流，并公开一个不可变版本供 UI 使用。
        //_uiState: 使用 MutableStateFlow 来保持内部状态，初始状态为一个新创建的 ExerciseUiState 对象。
        //val uiState: 为外部提供一个不变的状态流 StateFlow，允许观察 UI 状态，不允许外部修改。
        private val _uiState = MutableStateFlow(ExerciseUiState())
        val uiState = _uiState.asStateFlow()

    //aaChartModel 是一个 AAChartModel 的实例，用于配置图表的数据模型。
    //private var aaChartModel = AAChartModel()

    //aaChartView 是一个可空的 AAChartView 对象，用于显示图表。
    //private var aaChartView: AAChartView? = null

    //updateTimes 是一个整数，用于记录图表数据刷新的次数，初始值为 0。
    private var updateTimes: Int = 0

    val generatedData = MutableStateFlow<List<Int>>(emptyList())
    private val dataGenerator = Handler(Looper.getMainLooper())


    fun startDataGeneration() {
        val dataRunnable = object : Runnable {
            override fun run() {
                val randomValue = (0..100).random()
                val currentList = generatedData.value.toMutableList()
                currentList.add(randomValue)
                // 保持最近50个数据点
                if (currentList.size > 50) {
                    currentList.removeAt(0)
                }
                generatedData.value = currentList
                dataGenerator.postDelayed(this, 100)
            }
        }
        dataGenerator.post(dataRunnable)
    }

    fun stopDataGeneration() {
        dataGenerator.removeCallbacksAndMessages(null)
    }

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

//    //setUpAAChartView 方法设置图表视图。
//    fun setUpAAChartView() {
//        //使用 findViewById 方法获取布局中 R.id.AAChartView 的引用并赋值给 aaChartView。
//        aaChartView = findViewById(R.id.AAChartView)
//        //调用 configureAAChartModel 方法配置图表的模型。
//        aaChartModel = configureAAChartModel()
//        //将 aaChartModel 转换为 AAOptions 对象，以便于设置图表的选项。
//        val aaOptions: AAOptions = aaChartModel.aa_toAAOptions()
//        //根据图表类型 (Column 或 Bar)，设置相应的图表选项（如分组和点间距）。
//        if (aaChartModel.chartType == AAChartType.Column) {
//            aaOptions.plotOptions?.column!!
//                .groupPadding(0f)
//                .pointPadding(0f)
//                .borderRadius(5f)
//        } else if (aaChartModel.chartType == AAChartType.Bar) {
//            aaOptions.plotOptions?.bar!!
//                .groupPadding(0f)
//                .pointPadding(0f)
//                .borderRadius(5f)
//        }
//        //调用 aa_drawChartWithChartOptions 方法来绘制图表，只有在 aaChartView 不为 null 时。
//        aaChartView?.aa_drawChartWithChartOptions(aaOptions)
//    }

//    //configureAAChartModel 方法创建并返回一个 AAChartModel 对象。
//    private fun configureAAChartModel(): AAChartModel {
//        //首先调用 configureChartBasicContent 设置基本内容，然后使用 configureChartSeriesArray 配置图表的数据系列。
//        val aaChartModel = configureChartBasicContent()
//        aaChartModel.series(this.configureChartSeriesArray() as Array<Any>)
//        return aaChartModel
//    }


//    //configureChartBasicContent 方法获取图表类型的字符串（从 Intent 中）。
//    private fun configureChartBasicContent(): AAChartModel {
//        //intent 获取当前 Activity 的 Intent 对象，用于获取传递的参数。
//        val intent = intent
//        val chartType = intent.getStringExtra("chartType")
//        //使用 AAChartModel.Builder 创建并配置图表模型，包括图表类型、坐标轴的可见性等属性。
//        //设置图表的主题颜色，从 AAGradientColor 中指定不同的颜色。
//        return AAChartModel.Builder(this)
//            .setChartType(convertStringToEnum(chartType!!))
//            .setXAxisVisible(true)
//            .setYAxisVisible(false)
//            .setTitle("")
//            .setYAxisTitle("摄氏度")
//            .setColorsTheme(arrayOf(
//                AAGradientColor.Sanguine,
//                AAGradientColor.DeepSea,
//                AAGradientColor.NeonGlow,
//                AAGradientColor.WroughtIron
//            ))
//            .setStacking(AAChartStackingType.Normal)
//            .build()
//    }

//    //convertStringToEnum 方法将图表类型字符串转换为相应的 AAChartType 枚举类型，提供默认值为 Column。
//    private fun convertStringToEnum(chartTypeStr: String): AAChartType {
//        var chartTypeEnum = AAChartType.Column
//        //通过 when 表达式检查不同的图表类型。
//        when (chartTypeStr) {
//            AAChartType.Column.value -> chartTypeEnum = AAChartType.Column
//            AAChartType.Bar.value -> chartTypeEnum = AAChartType.Bar
//            AAChartType.Area.value -> chartTypeEnum = AAChartType.Area
//            AAChartType.Areaspline.value -> chartTypeEnum = AAChartType.Areaspline
//            AAChartType.Line.value -> chartTypeEnum = AAChartType.Line
//            AAChartType.Spline.value -> chartTypeEnum = AAChartType.Spline
//            AAChartType.Scatter.value -> chartTypeEnum = AAChartType.Scatter
//        }
//        return chartTypeEnum
//    }


    // configureChartSeriesArray 方法创建一个图表数据系列。
    // 使用 @Suppress("UNCHECKED_CAST") 注解避免在类型转换时的警告。

//    @Suppress("UNCHECKED_CAST")
//    private fun configureChartSeriesArray(): Array<AASeriesElement> {
//        //定义 maxRange 为 40，创建两个空的数组分别用于存储不同的数据系列。
//        val maxRange = 40
//        val numberArr1 = arrayOfNulls<Any>(maxRange)
//        val numberArr2 = arrayOfNulls<Any>(maxRange)
//        //定义 y1 和 y2 为 Double 类型，用于存储计算后的值。
//        //生成一个随机数 random，在 min 和 max 范围内，用于后续计算。
//        var y1: Double
//        var y2: Double
//        val max = 38
//        val min = 1
//        //使用循环生成 maxRange 个数据点，分别计算 y1 和 y2 的值，
//        // 存储到 numberArr1 和 numberArr2 数组中。这里用到了三角函数计算。
//        val random = (Math.random() * (max - min) + min).toInt()
//        for (i in 0 until maxRange) {
//            y1 = sin(random * (i * Math.PI / 180)) + i * 2 * 0.01
//            y2 = cos(random * (i * Math.PI / 180)) + i * 3 * 0.01
//            numberArr1[i] = y1
//            numberArr2[i] = y2
//        }
//        //返回一个 AASeriesElement 数组，每个元素对应不同年份的系列数据。
//        return arrayOf(
//            AASeriesElement()
//                .name("2017")
//                .data(numberArr1 as Array<Any>),
//            AASeriesElement()
//                .name("2018")
//                .data(numberArr2 as Array<Any>),
//            AASeriesElement()
//                .name("2019")
//                .data(numberArr1 as Array<Any>),
//            AASeriesElement()
//                .name("2020")
//                .data(numberArr2 as Array<Any>)
//        )
//    }

    //repeatUpdateChartData 方法用于定期更新图表数据。
    //创建一个 Handler 实例，用于处理定时任务。
//    private fun repeatUpdateChartData() {
//        val mStartVideoHandler = Handler()
//
//        //创建一个 Runnable 对象，定义在 run 方法中要执行的操作。
//        val mStartVideoRunnable: Runnable = object : Runnable {
//
//            //重写 run 方法，调用 configureChartSeriesArray 来获取最新的数据系列。
//            //使用 aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray 更新图表数据。
//            override fun run() {
//                val seriesArr = configureChartSeriesArray()
//                aaChartView!!.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(seriesArr)
//
//                mStartVideoHandler.postDelayed(this, 1000)
//                updateTimes += 1
//
//                print("图表数据正在刷新,刷新次数为:$updateTimes")
//            }
//        }
//
//        mStartVideoHandler.postDelayed(mStartVideoRunnable, 2000)
//    }


    }

