package com.ganainy.gymmasterscompose

import MainScreen
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ganainy.gymmasterscompose.ui.AppTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
//import com.github.aachartmodel.aainfographics.demo.R
import kotlin.math.cos
import kotlin.math.sin


// FishingActivity 类在 @AndroidEntryPointMainActivity.kt:16 处进行了注解，这使 Hilt 依赖注入能够应用于此 Android 组件。

//此 FishingActivity 展示了使用以下现代 Android 应用程序架构：
//
//Jetpack Compose for UI
//Hilt 用于依赖注入
//MVVM 模式与 ViewModels
//使用 Compose 的状态集合进行状态管理
//后端服务集成 Firebase
//使用 StrictMode 进行调试的开发最佳实践
//该结构表明这是一个与健身房相关的应用程序，处理用户身份验证并使用 Firebase 作为后端服务。身份验证状态管理表明，该应用可能根据用户是否登录而具有不同的屏幕或功能。

@AndroidEntryPoint
// 该类继承自 ComponentActivity，这是使用 Compose 的活动所使用的现代基类。
class MainActivity : ComponentActivity()  {

    private var aaChartModel = AAChartModel()

    private var aaChartView: AAChartView? = null

    private var updateTimes: Int = 0

    // onCreate 方法从对超类实现的常规调用开始。启用了边缘到边缘显示
    // 这使得应用能够在系统栏后面绘制内容，从而提供更沉浸式的体验。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Enable strict mode
        // 严格模式已配置用于开发调试 。
        // 此开发工具可检测各种违规行为，如主线程上的网络调用、主线程上的磁盘读写以及其他性能问题。
        // 该配置会检测所有违规行为并将它们记录为惩罚。
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        //setContent 块使用 Jetpack Compose 定义 UI。在自定义的 AppTheme 中，代码：
        //
        // DroidJetActivity.kt:35
        //从 ViewModel 收集认证状态作为 Compose 状态 DroidJetActivity.kt:37
        //将此认证状态传递给 MainScreen 可组合组件 DroidJetActivity.kt:39

        setContent {
            AppTheme {
                // Obtain the ViewModel instance using Hilt
                //使用 Hilt 的 hiltViewModel() 函数获取 MainViewModel 实例
                val viewModel: MainViewModel = hiltViewModel()
                // Collect the login status state from the ViewModel
                // 从 ViewModel 收集认证状态作为 Compose 状态
                val authState by viewModel.authState.collectAsState()
                // Pass the login status to the MainScreen composable
                // 将此认证状态传递给 MainScreen 可组合组件
                MainScreen(authState = authState)
            }
        }

//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val params = window.attributes
//        params.width = 1920
//        params.height = 1920
//        window.attributes = params
    }

    //在Activity销毁时释放音频资源
    override fun onDestroy() {
        super.onDestroy()
    }

    fun setUpAAChartView() {
        aaChartView = findViewById(R.id.AAChartView)
        aaChartModel = configureAAChartModel()
        val aaOptions: AAOptions = aaChartModel.aa_toAAOptions()
        if (aaChartModel.chartType == AAChartType.Column) {
            aaOptions.plotOptions?.column!!
                .groupPadding(0f)
                .pointPadding(0f)
                .borderRadius(5f)
        } else if (aaChartModel.chartType == AAChartType.Bar) {
            aaOptions.plotOptions?.bar!!
                .groupPadding(0f)
                .pointPadding(0f)
                .borderRadius(5f)
        }
        aaChartView?.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun configureAAChartModel(): AAChartModel {
        val aaChartModel = configureChartBasicContent()
        aaChartModel.series(this.configureChartSeriesArray() as Array<Any>)
        return aaChartModel
    }

    private fun configureChartBasicContent(): AAChartModel {
        val intent = intent
        val chartType = intent.getStringExtra("chartType")
        return AAChartModel.Builder(this)
            .setChartType(convertStringToEnum(chartType!!))
            .setXAxisVisible(true)
            .setYAxisVisible(false)
            .setTitle("")
            .setYAxisTitle("摄氏度")
            .setColorsTheme(arrayOf(
                AAGradientColor.Sanguine,
                AAGradientColor.DeepSea,
                AAGradientColor.NeonGlow,
                AAGradientColor.WroughtIron
            ))
            .setStacking(AAChartStackingType.Normal)
            .build()
    }

    private fun convertStringToEnum(chartTypeStr: String): AAChartType {
        var chartTypeEnum = AAChartType.Column
        when (chartTypeStr) {
            AAChartType.Column.value -> chartTypeEnum = AAChartType.Column
            AAChartType.Bar.value -> chartTypeEnum = AAChartType.Bar
            AAChartType.Area.value -> chartTypeEnum = AAChartType.Area
            AAChartType.Areaspline.value -> chartTypeEnum = AAChartType.Areaspline
            AAChartType.Line.value -> chartTypeEnum = AAChartType.Line
            AAChartType.Spline.value -> chartTypeEnum = AAChartType.Spline
            AAChartType.Scatter.value -> chartTypeEnum = AAChartType.Scatter
        }
        return chartTypeEnum
    }

    @Suppress("UNCHECKED_CAST")
    private fun configureChartSeriesArray(): Array<AASeriesElement> {
        val maxRange = 40
        val numberArr1 = arrayOfNulls<Any>(maxRange)
        val numberArr2 = arrayOfNulls<Any>(maxRange)
        var y1: Double
        var y2: Double
        val max = 38
        val min = 1
        val random = (Math.random() * (max - min) + min).toInt()
        for (i in 0 until maxRange) {
            y1 = sin(random * (i * Math.PI / 180)) + i * 2 * 0.01
            y2 = cos(random * (i * Math.PI / 180)) + i * 3 * 0.01
            numberArr1[i] = y1
            numberArr2[i] = y2
        }
        return arrayOf(
            AASeriesElement()
                .name("2017")
                .data(numberArr1 as Array<Any>),
            AASeriesElement()
                .name("2018")
                .data(numberArr2 as Array<Any>),
            AASeriesElement()
                .name("2019")
                .data(numberArr1 as Array<Any>),
            AASeriesElement()
                .name("2020")
                .data(numberArr2 as Array<Any>)
        )
    }

    private fun repeatUpdateChartData() {
        val mStartVideoHandler = Handler()

        val mStartVideoRunnable: Runnable = object : Runnable {

            override fun run() {
                val seriesArr = configureChartSeriesArray()
                aaChartView!!.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(seriesArr)

                mStartVideoHandler.postDelayed(this, 1000)
                updateTimes += 1

                print("图表数据正在刷新,刷新次数为:$updateTimes")
            }
        }

        mStartVideoHandler.postDelayed(mStartVideoRunnable, 2000)
    }


}