package com.ganainy.gymmasterscompose.ui.screens.static_test

import android.view.View
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.Rect
import org.jetbrains.skia.paragraph.FontCollection
import org.jetbrains.skia.paragraph.ParagraphBuilder
import org.jetbrains.skia.paragraph.ParagraphStyle
import org.jetbrains.skia.paragraph.TextStyle
import org.jetbrains.skiko.FPSCounter
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoRenderDelegate
import org.jetbrains.skiko.currentSystemTheme
import org.jetbrains.skiko.hostOs
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

//类名: Clocks，表示这是一个时钟的实现类。
//构造函数参数:
//skiaLayer: SkiaLayer 是一个 Skia 渲染层，用于绘制内容。
//view: View 是一个 Android 视图，用于接收用户的触控事件。
//实现接口: 实现 SkikoRenderDelegate 接口，表明这个类将处理渲染操作。
class Clocks(private val skiaLayer: SkiaLayer, view: View): SkikoRenderDelegate {

    //withFps: 一个布尔值，决定是否统计帧率。
    private val withFps = true
    //fpsCounter: 一个帧率计数器对象，用于跟踪渲染帧率。
    private val fpsCounter = FPSCounter()
    //platformYOffset: 根据操作系统选择平台的Y偏移量（用于适配iOS和其他平台）。
    private val platformYOffset = if (hostOs == OS.Ios) 50f else 5f
    //frame: 记录已经渲染的帧数，初始值为0。
    private var frame = 0
    //xpos, ypos: 浮点数，表示触摸事件的X和Y坐标。
    private var xpos = 0.0f
    private var ypos = 0.0f
    //xOffset, yOffset: 偏移量的变量，初始值为0.0，用于渲染偏移。
    private var xOffset = 0.0
    private var yOffset = 0.0
    //scale: 控制绘制缩放的因子，初始值为1.0。
    private var scale = 1.0
    private var k = scale
    //rotate: 控制绘制旋转的角度，初始值为0.
    private var rotate = 0.0
    //fontCollection: 字体集合，用于绘制文本。
    private val fontCollection = FontCollection()
        .setDefaultFontManager(FontMgr.default)
    //style: 段落样式，用于设置文本的样式。
    private val style = ParagraphStyle()

    //init 块: 在类实例化时执行的代码块。
    //setOnTouchListener: 设置触摸事件监听器，当用户在 view 上触摸时，
    // 更新 xpos 和 ypos 为触摸点的坐标，按 skiaLayer.contentScale 缩放。
    init {
        view.setOnTouchListener { _, event ->
            xpos = event.x / skiaLayer.contentScale
            ypos = event.y / skiaLayer.contentScale
            true
        }
    }

    //方法定义: 重写 onRender 方法，负责实际的绘制操作，参数包括:
    //canvas: 用于绘制的画布。
    //width, height: 画布的宽和高。
    //nanoTime: 当前时间的纳秒表示。
    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        //帧率统计: 如果 withFps 为真，调用 fpsCounter.tick() 更新帧率计数器。
        if (withFps) fpsCounter.tick()
        //将画布的原点移动到 (xOffset, yOffset)，用于实现背景的偏移。
        canvas.translate(xOffset.toFloat(), yOffset.toFloat())
        //对画布进行缩放，按照 scale 的值，这影响后续所有绘制操作的大小
        canvas.scale(scale.toFloat(), scale.toFloat())
        // 在画布中心 (width / 2, height / 2) 进行旋转，角度由 rotate 决定。
        // 这将影响所有后续绘制的对象，使其根据 rotate 角度倾斜
        canvas.rotate(rotate.toFloat(), (width / 2).toFloat(), (height / 2).toFloat())
        //画笔创建: 定义一个填充画笔，用于绘制表盘的内部区域，颜色是白色 (0xFFFFFFFF)。
        val watchFill = Paint().apply { color = 0xFFFFFFFF.toInt() }
        //定义一个轮廓画笔，用于绘制钟表的边框，颜色为黑色 (0xFF000000)，绘制模式为 STROKE（轮廓模式），线宽为1像素。
        val watchStroke = Paint().apply {
            color = 0xFF000000.toInt()
            mode = PaintMode.STROKE
            strokeWidth = 1f
        }
        //创建另一个轮廓画笔，与 watchStroke 类似，可能用于抗锯齿效果的绘制但实际用途需看代码后面的具体实现
        val watchStrokeAA = Paint().apply {
            color = 0xFF000000.toInt()
            mode = PaintMode.STROKE
            strokeWidth = 1f
        }
        //定义一个填充画笔，用于在用户悬停时绘制表盘的颜色，颜色为淡黄色 (0xFFE4FF01)
        val watchFillHover = Paint().apply { color = 0xFFE4FF01.toInt() }
        // 双重循环: 在画布上绘制多个钟表，每个钟表的位置由 x 和 y 决定。
        // 根据触摸坐标判断是否为“hover”状态，绘制不同样式的钟表。
        // for 循环: 遍历画布宽度，从 0 到 width - 50（不包括 width - 50），每次增加 50。这是为了在画布上均匀分布绘制钟表的X坐标。
        for (x in 0 .. width - 50 step 50) {
            // 嵌套 for 循环: 遍历画布高度，从 30 + platformYOffset（确保表盘在画布的合适位置）到 height - 50，每次增加 50。
            // 这样可以在画布上均匀分布绘制钟表的Y坐标。
            for (y in 30 + platformYOffset.toInt() .. height - 50 step 50) {
                //hover 判断: 计算当前触摸点是否位于当前绘制的钟表区域内。
                //(xpos - xOffset) / scale: 将触摸X坐标转换为实际画布坐标。
                //检查触摸坐标是否在当前钟表的 (x, y) 和 (x + 50, y + 50) 确定的矩形区域内。
                val hover =
                    (xpos - xOffset) / scale > x &&
                            (xpos - xOffset) / scale < x + 50 &&
                            (ypos - yOffset) / scale > y &&
                            (ypos - yOffset) / scale < y + 50
                //选择填充样式: 如果触摸点在钟表区域内，则使用 watchFillHover 否则使用 watchFill。
                val fill = if (hover) watchFillHover else watchFill
                //选择轮廓样式: 如果当前钟表的X坐标在画布的右半边，将使用 watchStrokeAA 否则将使用 watchStroke。
                val stroke = if (x > width / 2) watchStrokeAA else watchStroke
                //// 计算 hover 条件...
                //绘制填充圆形: 在 (x + 5, y + 5) 的位置绘制一个宽高均为 40 的椭圆（因参数设置圆形），使用计算出的 fill 作为填充样式。
                canvas.drawOval(Rect.makeXYWH(x + 5f, y + 5f, 40f, 40f), fill)
                //绘制轮廓圆形: 在相同位置绘制一个宽高均为 40 的圆形轮廓，使用 stroke 作为轮廓样式。
                canvas.drawOval(Rect.makeXYWH(x + 5f, y + 5f, 40f, 40f), stroke)
                //初始化角度: 设置角度变量 angle 为 0，用于后面绘制刻度。
                var angle = 0f
                //while 循环: 循环从 0 到 2π（完整的一圈），用于绘制12个小时刻度。
                while (angle < 2f * PI) {
                    // 绘制刻度线: 根据当前角度 (angle) 在钟表的中心 (x + 25, y + 25) 位置绘制刻度线。
                    // 线的起点和终点分别在距离中心 17 和 20 的不同位置。
                    //使用三角函数 sin 和 cos 计算出端点的坐标，从而形成指向表盘外围的刻度线。
                    canvas.drawLine(
                        (x + 25 - 17 * sin(angle)),
                        (y + 25 + 17 * cos(angle)),
                        (x + 25 - 20 * sin(angle)),
                        (y + 25 + 20 * cos(angle)),
                        stroke
                    )
                    //增加角度: 每次循环后将角度增加 2π / 12，确保在 12 个时刻均匀分布刻度线。
                    angle += (2.0 * PI / 12.0).toFloat()
                }
                //计算 time:
                //(nanoTime / 1E6) % 60000: 将纳秒转换为毫秒，并对 60000 取模，得到当前分钟内的时间（毫秒）。
                //(x.toFloat() / width * 5000).toLong(): 将X坐标比例乘以 5000 计算，加入时间反映在画布位置上。
                //(y.toFloat() / width * 5000).toLong(): 将Y坐标比例乘以 5000 计算，还有相同的含义。
                //这个计算是为了将时间偏移映射到每个钟表的位置。
                val time = (nanoTime / 1E6) % 60000 +
                        (x.toFloat() / width * 5000).toLong() +
                        (y.toFloat() / width * 5000).toLong()

                //计算时针角度: 根据当前的 time 值计算时针的旋转角度，使其在0到5秒内转动。
                val angle1 = (time.toFloat() / 5000 * 2f * PI).toFloat()
                //绘制时针: 从钟表的中心 (x + 25, y + 25) 到计算出的角度位置绘制时针，长度为 15。使用 stroke 画笔。
                canvas.drawLine(
                    x + 25f,
                    y + 25f,
                    x + 25f - 15f * sin(angle1),
                    y + 25f + 15 * cos(angle1),
                    stroke)

                //计算分针角度: 根据 time 值计算分针的旋转角度，使其在0到1分钟内转动。
                val angle2 = (time / 60000 * 2f * PI).toFloat()
                //绘制分针: 从钟表的中心 (x + 25, y + 25) 到计算出的角度位置绘制分针，长度为 10，同样使用 stroke 画笔。
                canvas.drawLine(
                    x + 25f,
                    y + 25f,
                    x + 25f - 10f * sin(angle2),
                    y + 25f + 10f * cos(angle2),
                    stroke)
            }
        }

        //计算帧率信息: 根据 withFps 的值来决定是否显示帧率。
        //如果 withFps 为 true，则获取 fpsCounter 的平均帧率，格式化为字符串 ${fpsCounter.average}FPS。
        //如果为 false，则 maybeFps 为空字符串。
        val maybeFps = if (withFps) " ${fpsCounter.average}FPS " else ""

        //创建文本段落:
        //ParagraphBuilder(style, fontCollection): 初始化一个段落构建器，使用之前定义的文本样式和字体集合。
        //pushStyle: 将文本样式压入栈中，这里设置文本颜色为黑色 (0xFF000000)。
        //addText: 添加要绘制的文本内容，包括图形 API、当前主题和可选的帧率信息。
        //popStyle: 从样式栈中弹出样式，以恢复之前的样式。
        //build(): 构建出一个段落对象 renderInfo。
        val renderInfo = ParagraphBuilder(style, fontCollection)
            .pushStyle(TextStyle().setColor(0xFF000000.toInt()))
            .addText("Graphics API: ${skiaLayer.renderApi} ✿ﾟ ${currentSystemTheme}${maybeFps}")
            .popStyle()
            .build()
        // 布局计算: 为段落 renderInfo 进行布局计算，Float.POSITIVE_INFINITY 表示不限制段落的宽度。
        // 这将计算文本的占用空间，以便可以准确地在画布上绘制
        renderInfo.layout(Float.POSITIVE_INFINITY)
        // 绘制文本: 在画布的指定位置 (5f, platformYOffset) 绘制文本段落 renderInfo。
        // 5f 是X坐标，platformYOffset 是Y坐标，用来确定文本绘制的具体位置。
        renderInfo.paint(canvas, 5f, platformYOffset)

        //创建帧数文本段落:
        //ParagraphBuilder(style, fontCollection): 再次初始化一个段落构建器。
        //pushStyle: 压入样式，设置颜色为亮绿色 (0xff9BC730) 并将字体大小设置为 20f。
        //addText: 添加文本内容，包括当前帧数和旋转角度，使用 frame++ 来显示当前帧并在之后自增帧数。
        //popStyle: 弹出样式，恢复到之前的样式。
        //build(): 构建出用以显示帧数和角度的段落对象 frames
        val frames = ParagraphBuilder(style, fontCollection)
            .pushStyle(TextStyle().setColor(0xff9BC730L.toInt()).setFontSize(20f))
            .addText("Frames: ${frame++}\nAngle: $rotate")
            .popStyle()
            .build()
        // 布局计算: 对段落 frames 进行布局计算，确保文本内容能够正确显示
        frames.layout(Float.POSITIVE_INFINITY)
        // 绘制文本: 在触摸位置的坐标上绘制 frames 文本段落。
        // 通过 (xpos - xOffset) / scale 和 (ypos - yOffset) / scale 将用户的触摸坐标转换为画布坐标并绘制
        frames.paint(canvas, ((xpos - xOffset) / scale).toFloat(), ((ypos - yOffset) / scale).toFloat())

        //重置画布矩阵: 将画布的当前变换状态重置为初始状态，确保后续的绘制操作不受本次绘制操作影响。这是个好习惯，以避免潜在的变换状态干扰后续绘图
        canvas.resetMatrix()
    }
}
