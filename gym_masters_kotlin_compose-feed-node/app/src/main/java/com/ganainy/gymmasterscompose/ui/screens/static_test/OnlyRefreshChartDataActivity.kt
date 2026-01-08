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
import androidx.appcompat.app.AppCompatActivity

import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.ganainy.gymmasterscompose.R
import kotlin.math.cos
import kotlin.math.sin

//定义一个名为 OnlyRefreshChartDataActivity 的类，继承自 AppCompatActivity，这是 Android 应用中的一个基础 Activity 类，用于支持兼容性功能。
class OnlyRefreshChartDataActivity : AppCompatActivity() {

    //aaChartModel 是一个 AAChartModel 的实例，用于配置图表的数据模型。
    private var aaChartModel = AAChartModel()

    //aaChartView 是一个可空的 AAChartView 对象，用于显示图表。
    private var aaChartView: AAChartView? = null

    //updateTimes 是一个整数，用于记录图表数据刷新的次数，初始值为 0。
    private var updateTimes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_only_refresh_chart_data)

        //调用 setUpAAChartView 方法初始化图表视图。
        setUpAAChartView()

        //调用 repeatUpdateChartData 方法启动图表数据的定期更新。
        repeatUpdateChartData()
    }


    //setUpAAChartView 方法设置图表视图。
    fun setUpAAChartView() {
        //使用 findViewById 方法获取布局中 R.id.AAChartView 的引用并赋值给 aaChartView。
        aaChartView = findViewById(R.id.AAChartView)
        //调用 configureAAChartModel 方法配置图表的模型。
        aaChartModel = configureAAChartModel()
        //将 aaChartModel 转换为 AAOptions 对象，以便于设置图表的选项。
        val aaOptions: AAOptions = aaChartModel.aa_toAAOptions()
        //根据图表类型 (Column 或 Bar)，设置相应的图表选项（如分组和点间距）。
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
        //调用 aa_drawChartWithChartOptions 方法来绘制图表，只有在 aaChartView 不为 null 时。
        aaChartView?.aa_drawChartWithChartOptions(aaOptions)
    }

    //configureAAChartModel 方法创建并返回一个 AAChartModel 对象。
    private fun configureAAChartModel(): AAChartModel {
        //首先调用 configureChartBasicContent 设置基本内容，然后使用 configureChartSeriesArray 配置图表的数据系列。
        val aaChartModel = configureChartBasicContent()
        aaChartModel.series(this.configureChartSeriesArray() as Array<Any>)
        return aaChartModel
    }

    //configureChartBasicContent 方法获取图表类型的字符串（从 Intent 中）。
    private fun configureChartBasicContent(): AAChartModel {
        //intent 获取当前 Activity 的 Intent 对象，用于获取传递的参数。
        val intent = intent
        val chartType = intent.getStringExtra("chartType")
        //使用 AAChartModel.Builder 创建并配置图表模型，包括图表类型、坐标轴的可见性等属性。
        //设置图表的主题颜色，从 AAGradientColor 中指定不同的颜色。
        return AAChartModel.Builder(this)
            .setChartType(convertStringToEnum(chartType!!))
//            .setChartType(AAChartType.Area)
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

    //convertStringToEnum 方法将图表类型字符串转换为相应的 AAChartType 枚举类型，提供默认值为 Column。
    private fun convertStringToEnum(chartTypeStr: String): AAChartType {
        var chartTypeEnum = AAChartType.Column
        //通过 when 表达式检查不同的图表类型。
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

    // configureChartSeriesArray 方法创建一个图表数据系列。
    // 使用 @Suppress("UNCHECKED_CAST") 注解避免在类型转换时的警告。
    @Suppress("UNCHECKED_CAST")
    private fun configureChartSeriesArray(): Array<AASeriesElement> {
        //定义 maxRange 为 40，创建两个空的数组分别用于存储不同的数据系列。
        val maxRange = 40
        val numberArr1 = arrayOfNulls<Any>(maxRange)
        val numberArr2 = arrayOfNulls<Any>(maxRange)
        //定义 y1 和 y2 为 Double 类型，用于存储计算后的值。
        //生成一个随机数 random，在 min 和 max 范围内，用于后续计算。
        var y1: Double
        var y2: Double
        val max = 38
        val min = 1
        //使用循环生成 maxRange 个数据点，分别计算 y1 和 y2 的值，
        // 存储到 numberArr1 和 numberArr2 数组中。这里用到了三角函数计算。
        val random = (Math.random() * (max - min) + min).toInt()
        for (i in 0 until maxRange) {
            y1 = sin(random * (i * Math.PI / 180)) + i * 2 * 0.01
            y2 = cos(random * (i * Math.PI / 180)) + i * 3 * 0.01
            numberArr1[i] = y1
            numberArr2[i] = y2
        }
        //返回一个 AASeriesElement 数组，每个元素对应不同年份的系列数据。
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

    //repeatUpdateChartData 方法用于定期更新图表数据。
    //创建一个 Handler 实例，用于处理定时任务。
    private fun repeatUpdateChartData() {
        val mStartVideoHandler = Handler()

        //创建一个 Runnable 对象，定义在 run 方法中要执行的操作。
        val mStartVideoRunnable: Runnable = object : Runnable {

            //重写 run 方法，调用 configureChartSeriesArray 来获取最新的数据系列。
            //使用 aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray 更新图表数据。
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
