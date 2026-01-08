package com.ganainy.gymmasterscompose.prefs

import android.content.Context
import android.content.SharedPreferences

//类声明，定义了 ExerciseDownloadPrefs 类，接受 Context 参数
class ExerciseDownloadPrefs(context: Context) {
    // 私有属性 prefs，通过 Context 获取名为 "exercise_download_prefs" 的 SharedPreferences 实例，使用私有模式
    private val prefs: SharedPreferences = context.getSharedPreferences("exercise_prefs", Context.MODE_PRIVATE)

    //KEY_INITIAL_DOWNLOAD_COMPLETE: 用于标识初始下载是否完成的键名
    companion object {
        private const val KEY_INITIAL_DOWNLOAD_COMPLETE = "initial_exercise_download_complete"
    }

    // isInitialDownloadComplete() 方法检查初始下载是否完成，返回布尔值，默认为 false
    fun isInitialDownloadComplete(): Boolean {
        return prefs.getBoolean(KEY_INITIAL_DOWNLOAD_COMPLETE, false)
    }

    // setInitialDownloadComplete() 方法设置初始下载完成状态
    fun setInitialDownloadComplete(complete: Boolean) {
        prefs.edit().putBoolean(KEY_INITIAL_DOWNLOAD_COMPLETE, complete).apply()
    }
}