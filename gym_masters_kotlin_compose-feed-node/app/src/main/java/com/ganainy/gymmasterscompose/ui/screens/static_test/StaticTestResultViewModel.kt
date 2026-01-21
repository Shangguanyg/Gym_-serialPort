package com.ganainy.gymmasterscompose.ui.screens.static_test

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.User
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost
import com.ganainy.gymmasterscompose.ui.repository.IPostRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.ui.screens.create_post.CreatePostUiState
import com.ganainy.gymmasterscompose.utils.Utils.extractHashtags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.flow.StateFlow

// 新增数据类定义
data class SensorData(
    val timestamp: Long = System.currentTimeMillis(),
    val sequenceId: Int = 0,
    val variable1: Float,
    val variable2: Float,
    val variable3: Float,
    val variable4: Float
)

data class StatisticsResult(
    val average: FloatArray,
    val variance: FloatArray,
    val sampleCount: Int,
    val lastUpdateTime: Long
)

data class StaticTestResultUiState(
    val isLoading: Boolean = false,
    val error: Int? = null,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val feedPost: FeedPost = FeedPost(),
    val isPostButtonEnabled: Boolean = false,
    val selectedImages: List<Uri> = emptyList(),
)

@HiltViewModel
class StaticTestResultViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    //private val postRepository: IPostRepository
) : ViewModel() {

    var  Name: MutableState<String> = mutableStateOf("测试")

    var TotalWeight: MutableState<Int> = mutableStateOf(0)

    var LeftWeight: MutableState<Int> = mutableStateOf(0)

    var RightWeight: MutableState<Int> = mutableStateOf(0)

    var TestScore: MutableState<Int> = mutableStateOf(0)

    val CoPPoint: MutableState<Int> =   mutableStateOf(0)

    val EnclosingArea: MutableState<Int> =  mutableStateOf(0)

    var TotalLength: MutableState<Int> = mutableStateOf(0)

    var HorizontalLength: MutableState<Int> =  mutableStateOf(0)

    var VerticalLength: MutableState<Int> = mutableStateOf(0)

    var TotalSpeed: MutableState<Int> =  mutableStateOf(0)

    var HorizontalSpeed: MutableState<Int> =  mutableStateOf(0)

    var VerticalSpeed: MutableState<Int> =  mutableStateOf(0)

    var TotalMSD: MutableState<Int> =  mutableStateOf(0)

    var HorizontalMSD: MutableState<Int> =  mutableStateOf(0)

    var VerticalMSD: MutableState<Int> =  mutableStateOf(0)

    var HorizontalFrequency: MutableState<Int> =  mutableStateOf(0)

    var VerticalFrequency: MutableState<Int> =  mutableStateOf(0)


    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

    private val _statisticsResult = MutableStateFlow(
        StatisticsResult(FloatArray(4), FloatArray(4), 0, 0)
    )

    //
    val statisticsResult: StateFlow<StatisticsResult> = _statisticsResult.asStateFlow()


    private var lastSequenceId = -1

    private val dataChannel = Channel<SensorData>(capacity = Channel.UNLIMITED)

    init {
        // 启动数据处理协程
        viewModelScope.launch {
            for (data in dataChannel) {
                addDataToBuffer(data)
                calculateStatistics()
            }
        }
    }

    // 处理接收到的原始数据
    fun processRawData(dataString: String) {
        viewModelScope.launch {
            try {
                val sensorData = parseSensorData(dataString)

                // 简单的防重复检查
                if (sensorData.sequenceId > lastSequenceId) {
                    dataChannel.trySend(sensorData)
                    lastSequenceId = sensorData.sequenceId
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "数据解析错误", e)
            }
        }
    }

    private fun parseSensorData(dataString: String): SensorData {
        // 假设数据格式为 "id,v1,v2,v3,v4"
        val parts = dataString.split(",").map { it.trim().toFloat() }
        return SensorData(
            sequenceId = parts[0].toInt(),
            variable1 = parts[1],
            variable2 = parts[2],
            variable3 = parts[3],
            variable4 = parts[4]
        )
    }


    data class SensorData(
        val timestamp: Long = System.currentTimeMillis(),
        val sequenceId: Int = 0,
        val variable1: Float,
        val variable2: Float,
        val variable3: Float,
        val variable4: Float
    )

    private val maxBufferSize = 1000  // 添加这一行

    private val dataBuffer = mutableListOf<SensorData>()

    private fun addDataToBuffer(data: SensorData) {
        dataBuffer.add(data)
        if (dataBuffer.size > maxBufferSize) {
            dataBuffer.removeAt(0)
        }
    }

    private fun calculateStatistics() {
        if (dataBuffer.isEmpty()) return

        val count = dataBuffer.size
        val sums = FloatArray(4)
        val averages = FloatArray(4)
        val variances = FloatArray(4)

        // 计算平均值
        dataBuffer.forEach { data ->
            sums[0] += data.variable1
            sums[1] += data.variable2
            sums[2] += data.variable3
            sums[3] += data.variable4
        }

        averages[0] = sums[0] / count
        averages[1] = sums[1] / count
        averages[2] = sums[2] / count
        averages[3] = sums[3] / count

        // 计算方差
        dataBuffer.forEach { data ->
            variances[0] += Math.pow((data.variable1 - averages[0]).toDouble(), 2.0).toFloat()
            variances[1] += Math.pow((data.variable2 - averages[1]).toDouble(), 2.0).toFloat()
            variances[2] += Math.pow((data.variable3 - averages[2]).toDouble(), 2.0).toFloat()
            variances[3] += Math.pow((data.variable4 - averages[3]).toDouble(), 2.0).toFloat()
        }

        for (i in 0..3) {
            variances[i] /= count
        }

        _statisticsResult.value = StatisticsResult(
            average = averages,
            variance = variances,
            sampleCount = count,
            lastUpdateTime = System.currentTimeMillis()
        )
    }


//    data class StatisticsResult(
//        val average: FloatArray,
//        val variance: FloatArray,
//        val sampleCount: Int,
//        val lastUpdateTime: Long
//    )
}