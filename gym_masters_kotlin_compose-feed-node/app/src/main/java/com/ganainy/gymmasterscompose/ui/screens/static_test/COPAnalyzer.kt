package com.ganainy.gymmasterscompose.ui.screens.static_test

import kotlin.math.*
import kotlin.random.Random


data class COPParameters(
    val stdML: Double,           // 标准差 - 内外侧方向 (cm)
    val stdAP: Double,           // 标准差 - 前后方向 (cm)
    val meanVelocityML: Double,  // 平均速度 - 内外侧方向 (cm/s)
    val meanVelocityAP: Double,  // 平均速度 - 前后方向 (cm/s)
    val totalPathLength: Double, // 总路径长度 (cm)
    val pea95Area: Double        // 95% 预测椭圆面积 (cm²)
)

data class COPDataPoint(
    val time: Double,
    val copx: Double,  // 内外侧方向
    val copy: Double   // 前后方向
)


class COPAnalyzer {

    fun calculateCoP(pressureA: Double, pressureB: Double, pressureC: Double, pressureD: Double): Pair<Double, Double> {
        // 计算总压力
        val totalPressure = pressureA + pressureB + pressureC + pressureD

        // 如果总压力为零，返回 (0,0) 作为 CoP
        if (totalPressure == 0.0) {
            return Pair(0.0, 0.0)
        }

        // 计算 CoP 的 X 坐标
        val copX = (pressureA * 0 + pressureB * 0 + pressureC * 50 + pressureD * 50) / totalPressure

        // 计算 CoP 的 Y 坐标
        val copY = (pressureA * 0 + pressureB * 100 + pressureC * 100 + pressureD * 0) / totalPressure

        return Pair(copX, copY)
    }

    fun generateCOPData() {
        val copDataPoints = List(500) { index ->
            COPDataPoint(
                time = index.toDouble(), // 可以使用简单的时间序列
                copx = Random.nextDouble(1.0, 15.0), // 随机生成 copx
                copy = Random.nextDouble(1.0, 15.0)  // 随机生成 copy
            )
        }
    }

    fun calculateParameters(data: List<COPDataPoint>): COPParameters {
        require(data.isNotEmpty()) { "数据不能为空" }

        // 计算标准差 - 对应 R 代码第 334-335 行
        val stdML = calculateStandardDeviation(data.map { it.copx })
        val stdAP = calculateStandardDeviation(data.map { it.copy })

        // 计算平均速度 - 对应 R 代码第 341-342 行
        val (meanVelocityML, meanVelocityAP) = calculateMeanVelocity(data)

        // 计算总路径长度 - 对应 R 代码第 345 行
        val totalPathLength = calculatePathLength(data)

        // 计算 95% PEA - 对应 R 代码第 348 行
        val pea95Area = calculatePEA95(data)

        return COPParameters(
            stdML = stdML,
            stdAP = stdAP,
            meanVelocityML = meanVelocityML,
            meanVelocityAP = meanVelocityAP,
            totalPathLength = totalPathLength,
            pea95Area = pea95Area
        )
    }

    //计算标准差
    private fun calculateStandardDeviation(values: List<Double>): Double {
        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.average()
        return sqrt(variance)
    }

    //计算平均速度
    private fun calculateMeanVelocity(data: List<COPDataPoint>): Pair<Double, Double> {
        if (data.size < 2) return 0.0 to 0.0

        val meanCopx = data.map { it.copx }.average()
        val meanCopy = data.map { it.copy }.average()

        val mcopx = data.map { it.copx - meanCopx }
        val mcopy = data.map { it.copy - meanCopy }

        val timeDiffs = data.zipWithNext { a, b -> b.time - a.time }
        val copxDiffs = mcopx.zipWithNext { a, b -> abs(b - a) }
        val copyDiffs = mcopy.zipWithNext { a, b -> abs(b - a) }

        val meanVelocityML = copxDiffs.zip(timeDiffs) { diff, time -> diff / time }.average()
        val meanVelocityAP = copyDiffs.zip(timeDiffs) { diff, time -> diff / time }.average()

        return meanVelocityML to meanVelocityAP
    }

    private fun calculatePathLength(data: List<COPDataPoint>): Double {
        if (data.size < 2) return 0.0

        return data.zipWithNext { a, b ->
            sqrt((b.copx - a.copx).pow(2) + (b.copy - a.copy).pow(2))
        }.sum()
    }

    private fun calculatePEA95(data: List<COPDataPoint>): Double {
        // 基于 Schuber & Kirchner (2013) 方法实现
        val copx = data.map { it.copx }.toDoubleArray()
        val copy = data.map { it.copy }.toDoubleArray()

        // 移除无穷值
        val validIndices = copx.indices.filter {
            copx[it].isFinite() && copy[it].isFinite()
        }
        val validCopx = validIndices.map { copx[it] }.toDoubleArray()
        val validCopy = validIndices.map { copy[it] }.toDoubleArray()

        if (validCopx.size < 2) return 0.0

        val meanCopx = validCopx.average()
        val meanCopy = validCopy.average()

        // 计算协方差矩阵的特征值
        val covarianceMatrix = calculateCovarianceMatrix(validCopx, validCopy)
        val eigenvalues = calculateEigenvalues(covarianceMatrix)

        // 95% 置信水平的卡方值 (自由度=2)
        val chiSquareValue = 5.991 // chi2inv(0.95, 2) 的近似值

        // 计算椭圆面积
        return PI * chiSquareValue * sqrt(eigenvalues[0] * eigenvalues[1])
    }

    private fun calculateCovarianceMatrix(x: DoubleArray, y: DoubleArray): Array<DoubleArray> {
        val meanX = x.average()
        val meanY = y.average()

        val covXX = x.map { (it - meanX).pow(2) }.average()
        val covYY = y.map { (it - meanY).pow(2) }.average()
        val covXY = x.zip(y) { xi, yi -> (xi - meanX) * (yi - meanY) }.average()

        return arrayOf(
            doubleArrayOf(covXX, covXY),
            doubleArrayOf(covXY, covYY)
        )
    }

    private fun calculateEigenvalues(matrix: Array<DoubleArray>): DoubleArray {
        // 对于 2x2 矩阵，直接计算特征值
        val a = matrix[0][0]
        val b = matrix[0][1]
        val c = matrix[1][0]
        val d = matrix[1][1]

        val trace = a + d
        val determinant = a * d - b * c

        val discriminant = sqrt(trace.pow(2) - 4 * determinant)

        return doubleArrayOf(
            (trace + discriminant) / 2,
            (trace - discriminant) / 2
        )
    }


}