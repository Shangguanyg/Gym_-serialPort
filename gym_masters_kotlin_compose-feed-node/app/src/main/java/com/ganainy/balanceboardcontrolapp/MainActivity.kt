package com.ganainy.balanceboardcontrolapp

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.os.Handler
import com.ganainy.balanceboardcontrolapp.data.Command
import com.ganainy.balanceboardcontrolapp.data.Outfit
import com.ganainy.balanceboardcontrolapp.network.RequestResult
import com.ganainy.balanceboardcontrolapp.network.ResultState
import com.ganainy.balanceboardcontrolapp.network.ServerInterface
import com.ganainy.balanceboardcontrolapp.ui.OutfitLayout
import com.ganainy.gymmasterscompose.R
//import kotlinx.android.synthetic.main.activity_main.*
import com.ganainy.gymmasterscompose.databinding.ActivityMainBinding
import com.ganainy.gymmasterscompose.databinding.OutfitLayoutBinding

class MainActivity : AppCompatActivity()
{
    //outfitLayoutList: 存储 OutfitLayout 对象的列表，
    private var outfitLayoutList: ArrayList<OutfitLayout> = ArrayList()
    //userEventListener: 用户事件监听器实例，处理来自用户的交互事件。
    private var userEventListener: UserEventListener = UserEventListener()
    //serverInterface: 用于和服务器交互的接口，初始化时传入了 OnServerResponseListener 以处理服务器的响应。
    private var serverInterface: ServerInterface = ServerInterface(OnServerResponseListener())

    //outfitMonitoringTaskHandler: Android 的 Handler 实例，用于在主线程中执行任务。
    private var outfitMonitoringTaskHandler = Handler()

    //outfitMonitoringTask: 自定义的 Runnable，负责定期从服务器获取服装列表。
    private var outfitMonitoringTask = OutfitMonitoringTask()

    private lateinit var binding: ActivityMainBinding // 假设您要使用的布局名称为 activity_main


    //onCreate: 生命周期方法，在活动创建时调用。
    //setContentView: 设置该活动使用的布局文件 activity_main，Layout 文件负责 UI 的显示。
    //outfitMonitoringTaskHandler.post(outfitMonitoringTask): 启动监测任务，定期获取服装列表。
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        outfitMonitoringTaskHandler.post(outfitMonitoringTask)
    }

    //onDestroy: 生命周期方法，在活动销毁时调用。
    //removeCallbacks: 停止任何 callback 任务，防止在活动销毁后仍继续尝试运行任务，这样可以避免内存泄漏。
    override fun onDestroy()
    {
        super.onDestroy()
        outfitMonitoringTaskHandler.removeCallbacks(outfitMonitoringTask)
    }

    //内部类: OutfitMonitoringTask 实现了 Runnable 接口。
    //run 方法: 每次任务执行时调用，获取服务器的服装列表并在一秒后再次运行自己，形成循环。
    inner class OutfitMonitoringTask : Runnable
    {
        override fun run()
        {
            serverInterface.getOutfitList()
            outfitMonitoringTaskHandler.postDelayed(this, 1000)
        }
    }

    //内部类: 作为服务器响应监听器的处理程序，确保处理来自服务器的特定数据。
    //onGetOutfitList 方法: 当成功获得服装列表时被调用，与 RequestResult 数据交互，并更新 UI 或状态。
    //onCommand 方法: 处理服务器的命令响应，可以留空或进行特定操作。
    inner class OnServerResponseListener : ServerInterface.OnResponseListener()
    {
        override fun onGetOutfitList(requestResult: RequestResult<ArrayList<Outfit>?>)
        {
            binding = ActivityMainBinding.inflate(layoutInflater)

            if(requestResult.resultState == ResultState.SUCCESS)
            {
                requestResult.data?.let{
                    /* check update */
                    for(outfit in it)
                    {
                        var foundLayout: OutfitLayout? = null
                        for(layout in outfitLayoutList)
                        {
                            if(outfit.id == layout.outfitId)
                            {
                                foundLayout = layout
                                break
                            }
                        }

                        if(foundLayout != null)
                            foundLayout.updateOutfit(outfit)
                        else
                        {
                            val outfitLayout = OutfitLayout()
                            supportFragmentManager.beginTransaction()
                                .add((R.id.mainLinearLayout), outfitLayout)
                                .commit()
                            outfitLayout.parentHorizontalScrollView = binding.mainHorizontalScrollView
                            outfitLayout.userEventListener = userEventListener
                            outfitLayout.setOutfit(outfit)

                            outfitLayoutList.add(outfitLayout)
                        }
                    }

                    /* check disconnected */
                    val toRemoveOutfitLayoutList = ArrayList<OutfitLayout>()
                    for(layout in outfitLayoutList)
                    {
                        var found = false
                        for(outfit in it)
                        {
                            if(outfit.id == layout.outfitId)
                            {
                                found = true
                                break
                            }
                        }

                        if(!found)
                        {
                            supportFragmentManager.beginTransaction()
                                .remove(layout)
                                .commit()

                            toRemoveOutfitLayoutList.add(layout)
                        }
                    }
                    for(layout in toRemoveOutfitLayoutList)
                        outfitLayoutList.remove(layout)
                }
            }
        }

        override fun onCommand(requestResult: RequestResult<Void?>)
        {
            // do nothing
        }
    }

    //处理用户事件: 此类根据用户的按钮点击执行相应操作。
    //onStartButtonClicked: 当用户点击开始按钮时，获取命令并将 type 设置为启动命令，进而通过 serverInterface 向服务器发送命令。
    //onStopButtonClicked: 与上一个相似，当停止按钮被按下时，设置相应的命令类型，然后向服务器发送停止命令。
    inner class UserEventListener : OutfitLayout.UserEventListener()
    {
        override fun onStartButtonClicked(outfitLayout: OutfitLayout)
        {
            var command = outfitLayout.getCommand()
            command.type = 2 /* command_start */
            serverInterface.command(command)
        }

        override fun onStopButtonClicked(outfitLayout: OutfitLayout)
        {
            var command = outfitLayout.getCommand()
            command.type = 3 /* command_stop */
            serverInterface.command(command)
        }
    }
}
