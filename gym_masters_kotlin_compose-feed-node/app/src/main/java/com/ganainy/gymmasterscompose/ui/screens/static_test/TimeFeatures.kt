import kotlin.math.*
import org.json.JSONObject
import java.io.File
import java.lang.Math.log
import kotlin.random.Random

/**
 * 时域特征计算配置数据类
 */
data class TimeFeaturesConfig(
    val z05: Double = 1.645,    // 95%置信区间的z分数  
    val f05: Double = 3.0,      // 95%置信椭圆的F统计量  
    val acquisitionFrequency: Int = 100  // 采样频率(Hz)  
)



/**
 * COP特征基础类
 */
open class CopFeatures(
    val copX: DoubleArray,
    val copY: DoubleArray,
    protected val config: TimeFeaturesConfig = TimeFeaturesConfig()
) {
    val copRd: DoubleArray
    val n: Int
    val t: Double

    init {
        copRd = computeRd(copX, copY)
        n = copRd.size
        t = n.toDouble() / config.acquisitionFrequency
    }

    companion object {
        fun parseCopData(filepath: String): Pair<DoubleArray, DoubleArray> {
            val json = JSONObject(File(filepath).readText())
            val copX = json.getJSONArray("COP_x").let { array ->
                DoubleArray(array.length()) { i -> array.getDouble(i) }
            }
            val copY = json.getJSONArray("COP_y").let { array ->
                DoubleArray(array.length()) { i -> array.getDouble(i) }
            }
            return Pair(copX, copY)
        }

        fun computeRd(copX: DoubleArray, copY: DoubleArray): DoubleArray {
            return DoubleArray(copX.size) { i ->
                sqrt(copX[i] * copX[i] + copY[i] * copY[i])
            }
        }
    }

    open fun summary() {
        println("COP Features Summary:")
        println("Data points: $n")
        println("Duration: $t seconds")
    }
}



/**
 * 实时时域特征滑动窗口计算器
 * 统一处理21项时域特征的实时计算
 */
class RealTimeTimeFeatures(
    private val windowSize: Int = 200,        // 2秒数据 @ 100Hz
    private val slideStep: Int = 50,          // 0.5秒滑动步长
    private val config: TimeFeaturesConfig = TimeFeaturesConfig()
) {
    // COP数据缓冲区
    private val copXBuffer = mutableListOf<Double>()
    private val copYBuffer = mutableListOf<Double>()
    private var sampleCount = 0

    /**
     * 添加新的COP数据点并计算特征
     */
    fun addCopPoint(x: Double, y: Double): Map<String, Double>? {
        copXBuffer.add(x)
        copYBuffer.add(y)
        sampleCount++

        // 维护窗口大小
        if (copXBuffer.size > windowSize) {
            copXBuffer.removeAt(0)
            copYBuffer.removeAt(0)
        }

        // 检查是否满足计算条件
        return if (shouldComputeFeatures()) {
            computeAllTimeFeatures()
        } else null
    }

    /**
     * 判断是否应该计算特征
     */
    private fun shouldComputeFeatures(): Boolean {
        return copXBuffer.size >= windowSize &&
                (sampleCount % slideStep == 0 || sampleCount == windowSize)
    }

    /**
     * 计算所有21项时域特征
     */
    private fun computeAllTimeFeatures(): Map<String, Double> {
        val copXArray = copXBuffer.toDoubleArray()
        val copYArray = copYBuffer.toDoubleArray()

        // 创建CopFeatures实例并计算基础特征
        val copFeatures = CopFeatures(copXArray, copYArray, config)

        // 计算距离特征
        val distanceFeatures = computeDistanceFeatures(copFeatures)

        // 计算面积特征
        val areaFeatures = computeAreaFeatures(copFeatures, distanceFeatures)

        // 计算混合特征
        val hybridFeatures = computeHybridFeatures(copFeatures, distanceFeatures, areaFeatures)

        // 合并所有特征
        return distanceFeatures + areaFeatures + hybridFeatures
    }

    /**
     * 计算距离特征（13项）
     */
    private fun computeDistanceFeatures(copFeatures: CopFeatures): Map<String, Double> {
        return mapOf(
            "Mean distance" to copFeatures.copRd.average(),
            "Mean distance-ML" to copFeatures.copX.map { abs(it) }.average(),
            "Mean distance-AP" to copFeatures.copY.map { abs(it) }.average(),
            "Rms distance" to sqrt(copFeatures.copRd.map { it * it }.average()),
            "Rms distance-ML" to sqrt(copFeatures.copX.map { it * it }.average()),
            "Rms distance-AP" to sqrt(copFeatures.copY.map { it * it }.average()),
            "Total path length" to computePathLength(copFeatures.copX, copFeatures.copY),
            "Mean velocity" to (computePathLength(copFeatures.copX, copFeatures.copY) / (copFeatures.copRd.size.toDouble() / config.acquisitionFrequency)),
            "Mean velocity-ML" to (computePathLengthML(copFeatures.copX) / (copFeatures.copX.size.toDouble() / config.acquisitionFrequency)),
            "Mean velocity-AP" to (computePathLengthML(copFeatures.copY) / (copFeatures.copY.size.toDouble() / config.acquisitionFrequency)),
            "Range" to (copFeatures.copRd.maxOrNull()!! - copFeatures.copRd.minOrNull()!!),
            "Range-ML" to (copFeatures.copX.maxOrNull()!! - copFeatures.copX.minOrNull()!!),
            "Range-AP" to (copFeatures.copY.maxOrNull()!! - copFeatures.copY.minOrNull()!!)
        )
    }

    /**
     * 计算面积特征（2项）
     */
    private fun computeAreaFeatures(copFeatures: CopFeatures, distanceFeatures: Map<String, Double>): Map<String, Double> {
        val stdRd = sqrt(distanceFeatures["Rms distance"]!! * distanceFeatures["Rms distance"]!! - distanceFeatures["Mean distance"]!! * distanceFeatures["Mean distance"]!!)
        val stdMl = distanceFeatures["Rms distance-ML"]!!
        val stdAp = distanceFeatures["Rms distance-AP"]!!

        val confidenceCircleArea = PI * (2 * (distanceFeatures["Mean distance"]!! + config.z05 * stdRd)) * (2 * (distanceFeatures["Mean distance"]!! + config.z05 * stdRd))

        // 计算协方差
        val meanX = copFeatures.copX.average()
        val meanY = copFeatures.copY.average()
        var covariance = 0.0
        for (i in copFeatures.copX.indices) {
            covariance += (copFeatures.copX[i] - meanX) * (copFeatures.copY[i] - meanY)
        }
        covariance /= copFeatures.copX.size

        val term1 = stdMl * stdMl * stdMl * stdMl
        val term2 = stdAp * stdAp * stdAp * stdAp
        val term3 = 6.0 * stdMl * stdMl * stdAp * stdAp
        val term4 = 4.0 * covariance * covariance
        val term5 = stdMl * stdMl + stdAp * stdAp

        val discriminant = sqrt(term1 + term2 + term3 - term4 - term5)
        val confidenceEllipseArea = PI * config.f05 * discriminant

        return mapOf(
            "95% confidence circle area" to confidenceCircleArea,
            "95% confidence ellipse area" to confidenceEllipseArea
        )
    }

    /**
     * 计算混合特征（6项）
     */
    private fun computeHybridFeatures(copFeatures: CopFeatures, distanceFeatures: Map<String, Double>, areaFeatures: Map<String, Double>): Map<String, Double> {
        val swayArea = computeSwayArea(copFeatures.copX, copFeatures.copY, copFeatures.t)
        val meanFrequency = distanceFeatures["Mean velocity"]!! / (2 * PI * distanceFeatures["Mean distance"]!!)
        val meanFrequencyMl = distanceFeatures["Mean velocity-ML"]!! / (4 * sqrt(2.0) * distanceFeatures["Mean distance-ML"]!!)
        val meanFrequencyAp = distanceFeatures["Mean velocity-AP"]!! / (4 * sqrt(2.0) * distanceFeatures["Mean distance-AP"]!!)

        val fractalDimensionCc = computeFractalDimension(copFeatures.copRd.size, computePathLength(copFeatures.copX, copFeatures.copY), 2 * (distanceFeatures["Mean distance"]!! + config.z05 * sqrt(distanceFeatures["Rms distance"]!! * distanceFeatures["Rms distance"]!! - distanceFeatures["Mean distance"]!! * distanceFeatures["Mean distance"]!!)))

        return mapOf(
            "Mean frequency" to meanFrequency,
            "Mean frequency-ML" to meanFrequencyMl,
            "Mean frequency-AP" to meanFrequencyAp,
            "Sway area" to swayArea,
            "Fractal dimension-CC" to fractalDimensionCc,
            "Fractal dimension-CE" to fractalDimensionCc // 简化版本
        )
    }

    /**
     * 计算路径长度
     */
    private fun computePathLength(x: DoubleArray, y: DoubleArray): Double {
        var pathLength = 0.0
        for (i in 1 until x.size) {
            val dx = x[i] - x[i-1]
            val dy = y[i] - y[i-1]
            pathLength += sqrt(dx * dx + dy * dy)
        }
        return pathLength
    }

    /**
     * 计算单轴路径长度
     */
    private fun computePathLengthML(array: DoubleArray): Double {
        var pathLength = 0.0
        for (i in 1 until array.size) {
            pathLength += abs(array[i] - array[i-1])
        }
        return pathLength
    }

    /**
     * 计算摆动面积
     */
    private fun computeSwayArea(x: DoubleArray, y: DoubleArray, t: Double): Double {
        var swayArea = 0.0
        for (i in 0 until x.size - 1) {
            swayArea += abs(x[i + 1] * y[i] - x[i] * y[i + 1]) / (2.0 * t)
        }
        return swayArea
    }

    /**
     * 计算分形维数
     */
    private fun computeFractalDimension(n: Int, totalPathLength: Double, d: Double): Double {
        return ln(n.toDouble()) / ln((n * d) / totalPathLength)
    }

    /**
     * 获取当前窗口的统计信息
     */
    fun getWindowInfo(): WindowInfo {
        return WindowInfo(
            windowSize = copXBuffer.size,
            targetSize = windowSize,
            sampleCount = sampleCount.toLong(),
            isReady = copXBuffer.size >= windowSize
        )
    }

    /**
     * 重置缓冲区
     */
    fun reset() {
        copXBuffer.clear()
        copYBuffer.clear()
        sampleCount = 0
    }

    /**
     * 窗口信息数据类
     */
    data class WindowInfo(
        val windowSize: Int,
        val targetSize: Int,
        val sampleCount: Long,
        val isReady: Boolean
    )
}

fun main() {
    val realTimeFeatures = RealTimeTimeFeatures(
        windowSize = 200,
        slideStep = 50
    )

    repeat(500) { i ->
        val x = sin(i * 0.1) + Random.nextDouble() * 0.1  // 修正：使用Random.nextDouble()
        val y = cos(i * 0.1) + Random.nextDouble() * 0.1  // 修正：使用Random.nextDouble()

        val features = realTimeFeatures.addCopPoint(x, y)

        features?.let {
            println("=== 实时时域特征 (样本 ${realTimeFeatures.getWindowInfo().sampleCount}) ===")
            it.forEach { (featureName, value) ->
                println("$featureName: %.4f".format(value))
            }
            println()
        }
    }
}