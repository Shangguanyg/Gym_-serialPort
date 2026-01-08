/**
 * ◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉ ...... SOURCE CODE ......◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉
 * ◉◉◉...................................................       ◉◉◉
 * ◉◉◉   https://github.com/AAChartModel/AAChartCore            ◉◉◉
 * ◉◉◉   https://github.com/AAChartModel/AAChartCore-Kotlin     ◉◉◉
 * ◉◉◉...................................................       ◉◉◉
 * ◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉ ...... SOURCE CODE ......◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉◉
 */

/**

 * -------------------------------------------------------------------------------
 *
 *  🌕 🌖 🌗 🌘  ❀❀❀   WARM TIPS!!!   ❀❀❀ 🌑 🌒 🌓 🌔
 *
 * Please contact me on GitHub,if there are any problems encountered in use.
 * GitHub Issues : https://github.com/AAChartModel/AAChartCore-Kotlin/issues
 * -------------------------------------------------------------------------------
 * And if you want to contribute for this project, please contact me as well
 * GitHub        : https://github.com/AAChartModel
 * StackOverflow : https://stackoverflow.com/users/7842508/codeforu
 * JianShu       : http://www.jianshu.com/u/f1e6753d4254
 * SegmentFault  : https://segmentfault.com/u/huanghunbieguan
 *
 * -------------------------------------------------------------------------------

 */
package com.ganainy.gymmasterscompose.ui.screens.static_test

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AAClickEventMessageModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAMoveOverEventMessageModel
import com.ganainy.gymmasterscompose.ui.screens.static_test.SpecialChartComposer
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.screens.static_test.SpecialChartComposer.numberArr
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.math.sin
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.ui.screens.workout_details.WorkoutDetailsViewModel
import com.ganainy.gymmasterscompose.ui.screens.static_test.StaticTestViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.*
import android.os.*
import android.widget.*
import java.util.*

class SpecialChartActivity : AppCompatActivity(),
    AAChartView.AAChartViewCallBack {

//    var aaChartModel: AAChartModel? = null

    private var aaChartView: AAChartView? = null
    private var updateTimes: Int = 0

    private var numberArrLength: Int = 0

    val viewModel: StaticTestViewModel by viewModels()

    var testTime: TextView? = null

    var testScore: TextView? = null

    private lateinit var timerHandler: Handler
    private var timerRunnable: Runnable? = null
    private var secondsElapsed: Int = 0 // 计时器秒数

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("chart", "start onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_chart)

        val intent = intent
        val chartType = intent.getStringExtra("chartType")

        val aaChartModel = configureChartModelWithChartType(chartType!!)
        aaChartModel.clickEventEnabled(true)
            .touchEventEnabled(true)

        testTime = findViewById(R.id.testTime)

        testScore= findViewById(R.id.testScore)

        aaChartView = findViewById(R.id.AAChartView)
        aaChartView?.callBack = this
        aaChartView?.aa_drawChartWithChartModel(aaChartModel)

        // 初始化计时器处理器
        timerHandler = Handler(Looper.getMainLooper())

        // 确保这个 ID 在布局文件中存在
        val startButton = findViewById<Button>(R.id.startStaticTest)
        val stopButton = findViewById<Button>(R.id.stopStaticTest)

        startButton.setOnClickListener {
            Log.d("chart", "findViewById startStaticTest")
            startTimer()
        }

        stopButton.setOnClickListener {
            Log.d("chart", "findViewById stopStaticTest")
            stopTimer()
        }

        repeatUpdateChartData()
    }

    // 启动计时器
    private fun startTimer() {
        Log.d("chart", "startTimer")
        secondsElapsed = 0 // 重置秒数
        timerRunnable = object : Runnable {
            override fun run() {
                secondsElapsed++ // 每次调用增加秒数
                testTime?.text = String.format("测试计时: %d秒", secondsElapsed)
                timerHandler.postDelayed(this, 1000) // 每秒调用一次
            }
        }
        timerHandler.post(timerRunnable!!) // 启动计时器
    }

    // 停止计时器
    private fun stopTimer() {
        timerRunnable?.let { timerHandler.removeCallbacks(it) } // 移除回调，停止计时器
    }

    private fun configureChartModelWithChartType(chartType: String): AAChartModel {
        when (chartType) {
//            AAChartType.Column.value -> return SpecialChartComposer.configurePolarColumnChart()
            AAChartType.Bar.value -> return SpecialChartComposer.configurePolarBarChart()
            AAChartType.Line.value -> return SpecialChartComposer.configurePolarLineChart()
            AAChartType.Area.value -> return SpecialChartComposer.configurePolarAreaChart()
            AAChartType.Pie.value -> return SpecialChartComposer.configurePieChart()
            AAChartType.Bubble.value -> return SpecialChartComposer.configureBubbleChart()
            AAChartType.Scatter.value -> return SpecialChartComposer.configureScatterChart()
            AAChartType.Arearange.value -> return SpecialChartComposer.configureArearangeChart()
            AAChartType.Areasplinerange.value -> return SpecialChartComposer.configureAreasplinerangeChart()
            AAChartType.Columnrange.value -> return SpecialChartComposer.configureColumnrangeChart()
            AAChartType.Spline.value -> return SpecialChartComposer.configureStepLineChart()
            AAChartType.Areaspline.value -> return SpecialChartComposer.configureStepAreaChart()
            AAChartType.Boxplot.value -> return SpecialChartComposer.configureBoxplotChart()
            AAChartType.Waterfall.value -> return SpecialChartComposer.configureWaterfallChart()
            AAChartType.Pyramid.value -> return SpecialChartComposer.configurePyramidChart()
            AAChartType.Funnel.value -> return SpecialChartComposer.configureFunnelChart()
            AAChartType.Errorbar.value -> return SpecialChartComposer.configureErrorbarChart()
            AAChartType.Gauge.value -> return SpecialChartComposer.configureGaugeChart()
            AAChartType.Polygon.value -> return SpecialChartComposer.configurePolygonChart()
        }

        return SpecialChartComposer.configurePolarColumnChart()

    }

    override fun chartViewDidFinishLoad(aaChartView: AAChartView) {
        //do nothing
    }

    override fun chartViewClickEventMessage(
        aaChartView: AAChartView,
        clickEventMessage: AAClickEventMessageModel
    ) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val clickEventMessageModelJson = gson.toJson(clickEventMessage)

        // 打印点击事件信息
        println("🖱🖱🖱获取点击事件 clickMessageModel = $clickEventMessageModelJson")
    }

    override fun chartViewMoveOverEventMessage(
        aaChartView: AAChartView,
        messageModel: AAMoveOverEventMessageModel
    ) {
        //do nothing
    }

    //repeatUpdateChartData 方法用于定期更新图表数据。
    //创建一个 Handler 实例，用于处理定时任务。
    private fun repeatUpdateChartData() {
        val mStartVideoHandler = Handler()

        //创建一个 Runnable 对象，定义在 run 方法中要执行的操作。
        val mStartVideoRunnable: Runnable = object : Runnable {

            //重写 run 方法，调用 configureChartSeriesArray 来获取最新的数据系列。
            //使用 aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray 更新图表数据。
            override fun run() {
               // val seriesArr = SpecialChartComposer.configureChartSeriesArray()
                val seriesArr = configureChartSeriesArray()
                aaChartView!!.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(seriesArr)

                mStartVideoHandler.postDelayed(this, 200)
                updateTimes += 1

                testTime?.text = "本次测试时间： $updateTimes"

                print("图表数据正在刷新,刷新次数为:$updateTimes")
            }
        }

        mStartVideoHandler.postDelayed(mStartVideoRunnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer() // 确保在活动销毁时停止计时器
    }

    @Suppress("UNCHECKED_CAST")
    fun configureChartSeriesArray(): Array<AASeriesElement> {
        Log.d("chart", "configureChartSeriesArray")
        //定义 maxRange 为 40，创建两个空的数组分别用于存储不同的数据系列。
        val maxRange = 30
        val numberArr1 = arrayOfNulls<Any>(maxRange)
        var updateTimes: Int = 0
        //定义 y1 和 y2 为 Double 类型，用于存储计算后的值。
        //生成一个随机数 random，在 min 和 max 范围内，用于后续计算。
        var y1: Double
//        var y2: Double
        val max = 38
        val min = 1
        //使用循环生成 maxRange 个数据点，分别计算 y1 和 y2 的值，
        // 存储到 numberArr1 和 numberArr2 数组中。这里用到了三角函数计算。
        val random = (Math.random() * (max - min) + min).toInt()
        for (i in 0 until maxRange) {
            y1 = sin(random * (i * Math.PI / 180)) + i * 2 * 0.01
            numberArr1[i] = y1 * 100
        }

        System.arraycopy(numberArr1,0,numberArr,numberArrLength,maxRange)

        numberArrLength += maxRange
        Log.d("chart", "numberArr is : ${numberArr}")
        Log.d("chart", "numberArrLength is : ${numberArrLength}")

        //返回一个 AASeriesElement 数组，每个元素对应不同年份的系列数据。
        return arrayOf(
            AASeriesElement()
                .name("静态测试")
                .data(numberArr as Array<Any>),
        )
//        return arrayOf(
//            AASeriesElement()
//                .name("静态测试")
//                .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6))
//        )
    }


}
