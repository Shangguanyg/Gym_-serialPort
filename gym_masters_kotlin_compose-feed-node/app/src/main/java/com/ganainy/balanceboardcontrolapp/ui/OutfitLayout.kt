package com.ganainy.balanceboardcontrolapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.ganainy.balanceboardcontrolapp.data.Command
import com.ganainy.balanceboardcontrolapp.data.Outfit
import com.ganainy.gymmasterscompose.R
import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.activity_main.*
import com.ganainy.gymmasterscompose.databinding.ActivityMainBinding
import com.ganainy.gymmasterscompose.databinding.OutfitLayoutBinding

class OutfitLayout : Fragment()
{
    //myView 保存 Fragment 的根视图
    var myView: View? = null

    //
    val outfitLayoutBinding = OutfitLayoutBinding.inflate(layoutInflater) // 创建相应的 ViewBinding

    //outfitId 是设备的唯一标识符,默认值为 -1。
    var outfitId: Int = -1

    //name 和 status 存储设备名称和当前状态文本
    var name: String = ""
    var status: String = ""

    //signalPeriod(0-3000毫秒)和 changeTime(0-10秒)是可调节的参数
    var signalPeriod: Int = 0
    var changeTime: Int = 0

    //是由 FishingActivity 注入的依赖:parentHorizontalScrollView 用于触摸事件协调,
    // userEventListener 用于按钮点击回调。
    var parentHorizontalScrollView: HorizontalScrollView? = null
    var userEventListener: UserEventListener? = null

    //布尔标志防止 SeekBar 和 EditText 之间的双向更新循环
    private var signalPeriodChangedInternally = false
    private var changeTimeChangedInternally = false
    private lateinit var binding: ActivityMainBinding // 这里是主布局的 ViewBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        binding = ActivityMainBinding.inflate(layoutInflater) // 设置主布局的 ViewBinding

//        val outfitLayoutBinding = OutfitLayoutBinding.inflate(layoutInflater) // 创建相应的 ViewBinding

        myView = inflater.inflate(R.layout.outfit_layout, null)

        /* setup size (layout parameter */
        //设置固定宽度为 200dp(转换为像素),高度为 MATCH_PARENT,确保多个 Fragment 在水平滚动视图中大小一致。
        val width = (200 * resources.displayMetrics.density + 0.5f).toInt()
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        val layoutParams = ViewGroup.LayoutParams(width, height) //view.layoutParams
        // 创建子视图的 ViewBindingz

        outfitLayoutBinding.root.layoutParams = layoutParams // 使用 root 来获取根视图

//        myView?.layoutParams = layoutParams

        /* setting seekbar with scroll view */
//        myView?.periodSeekBar?.setOnTouchListener(object:View.OnTouchListener
        //当检测到 ACTION_DOWN 或 ACTION_MOVE 事件时,
        // 调用 requestDisallowInterceptTouchEvent(true) 阻止父滚动视图拦截触摸,
        // 使用户可以精确调节 SeekBar 而不会意外滚动。
        outfitLayoutBinding.periodSeekBar.setOnTouchListener(object:View.OnTouchListener
        {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean
            {
                if(event?.action == MotionEvent.ACTION_DOWN ||
                    event?.action == MotionEvent.ACTION_MOVE)
                {
                    parentHorizontalScrollView?.requestDisallowInterceptTouchEvent(true)
                }

                return false
            }
        })

        //
        outfitLayoutBinding.changeTimeSeekBar.setOnTouchListener(object:View.OnTouchListener
        {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean
            {
                if(event?.action == MotionEvent.ACTION_DOWN ||
                    event?.action == MotionEvent.ACTION_MOVE)
                {
                    parentHorizontalScrollView?.requestDisallowInterceptTouchEvent(true)
                }

                return false
            }
        })

        /* bind button events */
        //绑定开始按钮,点击时调用 userEventListener.onStartButtonClicked(this)。
        //  FishingActivity 接收此回调,获取命令并发送到服务器。
        outfitLayoutBinding.startButton.setOnClickListener((
        {
            userEventListener?.onStartButtonClicked(this)
        }))

        //绑定停止按钮
        outfitLayoutBinding.stopButton.setOnClickListener((
        {
            userEventListener?.onStopButtonClicked(this)
        }))

        /* bind seek bar events */
        //SeekBar 变化监听器
        outfitLayoutBinding.periodSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener
        {
            //处理 periodSeekBar 的进度变化。
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                //检查 signalPeriodChangedInternally 标志,如果为 true 则忽略此次变化(防止循环更新)。
                if(signalPeriodChangedInternally)
                {
                    signalPeriodChangedInternally = false
                    return
                }

                //更新 signalPeriod 属性,设置标志为 true,并更新 EditText。
                signalPeriod = progress
                signalPeriodChangedInternally = true
                outfitLayoutBinding.periodText.setText(signalPeriod.toString())
            }

            //
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        //changeTimeSeekBar变化监听器
        outfitLayoutBinding.changeTimeSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                if(changeTimeChangedInternally)
                {
                    changeTimeChangedInternally = false
                    return
                }

                changeTime = progress
                changeTimeChangedInternally = true
                outfitLayoutBinding.changeTimeText.setText(changeTime.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        /* bind edit text events */
        // 处理 periodText 的文本变化
        outfitLayoutBinding.periodText.addTextChangedListener(object:TextWatcher
        {
            override fun afterTextChanged(s: Editable?)
            {
                //检查标志,防止循环更新。
                if(signalPeriodChangedInternally)
                {
                    signalPeriodChangedInternally = false
                    return
                }

                //解析文本为整数,处理空字符串和数字格式异常
                if(outfitLayoutBinding.periodText.text.toString() == "")
                    signalPeriod = 0
                else
                {
                    try {
                        signalPeriod = Integer.parseInt(outfitLayoutBinding.periodText.text.toString())
                    } catch(e: NumberFormatException){
                        signalPeriodChangedInternally = true
                        outfitLayoutBinding.periodText.setText(signalPeriod.toString())
                        return
                    }
                }

                //验证范围(0-3000),超出范围则钳制并重置 EditText
                if(signalPeriod > 3000)
                {
                    signalPeriod = 3000
                    signalPeriodChangedInternally = true
                    outfitLayoutBinding.periodText.setText(signalPeriod.toString())
                }
                else if(signalPeriod < 0)
                {
                    signalPeriod = 0
                    signalPeriodChangedInternally = true
                    outfitLayoutBinding.periodText.setText(signalPeriod.toString())
                }

                //设置标志并更新 SeekBar
                signalPeriodChangedInternally = true
                outfitLayoutBinding.periodSeekBar.progress = signalPeriod
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //为 changeTimeText 实现相同逻辑,范围为 0-10
        outfitLayoutBinding.changeTimeText.addTextChangedListener(object:TextWatcher
        {
            override fun afterTextChanged(s: Editable?)
            {
                if(changeTimeChangedInternally)
                {
                    changeTimeChangedInternally = false
                    return
                }

                if(outfitLayoutBinding.changeTimeText.text.toString() == "")
                    changeTime = 0
                else
                {
                    try {
                        changeTime = Integer.parseInt(outfitLayoutBinding.changeTimeText.text.toString())
                    } catch(e: NumberFormatException){
                        changeTimeChangedInternally = true
                        outfitLayoutBinding.changeTimeText.setText(changeTime.toString())
                        return
                    }
                }

                if(changeTime > 10)
                {
                    changeTime = 10
                    changeTimeChangedInternally = true
                    outfitLayoutBinding.changeTimeText.setText(changeTime.toString())
                }
                else if(changeTime < 0)
                {
                    changeTime = 0
                    changeTimeChangedInternally = true
                    outfitLayoutBinding.changeTimeText.setText(changeTime.toString())
                }

                changeTimeChangedInternally = true
                outfitLayoutBinding.changeTimeSeekBar.progress = changeTime
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        /* initialize content value */
        //调用 initializeUI() 和 updateUI() 初始化界面。
        initializeUI()
        updateUI()

        //返回视图
        return myView
    }

    //此方法在 Fragment 首次创建时由 FishingActivity 调用
    fun setOutfit(outfit: Outfit)
    {
        //设置 outfitId
        outfitId = outfit.id

        //构造名称为 "教具 {id}号"。
        name = "교구 " + outfitId.toString() + "번"
        //复制参数值
        signalPeriod = outfit.signalPeriod
        changeTime = outfit.changeTime

        //调用 updateOutfit() 设置状态
        updateOutfit(outfit)
    }

    //此方法在服务器数据更新时被 FishingActivity 周期性调用
    fun updateOutfit(outfit: Outfit)
    {
        //使用 when 表达式将运动代码映射为韩文状态文本。
        status = when(outfit.exercise)
        {
            //"NO" → "待机中","BI" → "BI运动-{level}-{motion}",
            "NO" ->
                "대기중"
            "BI" ->
                "BI운동-${outfit.level}-${outfit.motion}"
            "DI" ->
                "방향성-${outfit.level}-${outfit.motion}"
            "BA" ->
                "균형-${outfit.level}-${outfit.motion}"
            "RO" ->
                "회전-${outfit.level}-${outfit.motion}"
            else ->
                "대기중"
        }

        updateUI()
    }

    //将 UI 状态转换为 Command 对象,由 FishingActivity 在按钮点击时调用。
    fun getCommand() : Command
    {
        val exercise = when( outfitLayoutBinding.exerciseSpinner.selectedItem)
        {
            "BI 운동" -> "BI"
            "방향성" -> "DI"
            "균형" -> "BA"
            "회전" -> "RO"
            else -> "BI"
        }

        val level = Integer.parseInt(outfitLayoutBinding.levelSpinner.selectedItem as String)

        return Command(
            outfitId,
            0, /* command_none */
            exercise,
            level,
            signalPeriod,
            changeTime
        )
    }

    private fun initializeUI()
    {
        outfitLayoutBinding.titleText?.text = name

        if(signalPeriod > 3000)
            signalPeriod = 3000
        else if(signalPeriod < 0)
            signalPeriod = 0

        if(changeTime > 10)
            changeTime = 10
        else if(changeTime < 0)
            changeTime = 0

        outfitLayoutBinding.periodSeekBar.progress = signalPeriod
        outfitLayoutBinding.periodText.setText(signalPeriod.toString())
        outfitLayoutBinding.changeTimeSeekBar.progress = changeTime
        outfitLayoutBinding.changeTimeText.setText(changeTime.toString())
    }

    private fun updateUI()
    {
        outfitLayoutBinding.statusText.text = status
    }

    abstract class UserEventListener
    {
        abstract fun onStartButtonClicked(outfitLayout: OutfitLayout)
        abstract fun onStopButtonClicked(outfitLayout: OutfitLayout)
    }
}
