package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.utilities.common.ArrayUtility
import java.io.Serializable

/**
 * 记录步骤
 */
class Recorder(matrix: Array<Array<Int>>) : Serializable {

    private val steps = ArrayList<Step>() // 走棋记录
    internal val initStatus: Array<Array<Int>> // 初始棋盘
    internal val finalStatus: Array<Array<Int>> // 最终棋盘或者说当前棋盘

    init {
        if (matrix.isNotEmpty()) {
            initStatus = matrix
            finalStatus = Array(matrix.size) { Array(matrix[0].size) { 0 } }
            for (row in matrix.indices) {
                for (column in matrix[row].indices) {
                    finalStatus[row][column] = matrix[row][column]
                }
            }
        } else {
            initStatus = matrix
            finalStatus = matrix
        }
    }

    /**
     * 记录一步棋
     */
    fun record(s: Step) {
        steps.add(s)
        finalStatus[s.to.x][s.to.y] = finalStatus[s.from.x][s.from.y]
        finalStatus[s.from.x][s.from.y] = 0
    }

    /**
     * 清除记录
     */
    fun clear() {
        steps.clear()
    }

    override fun toString(): String {
        var s = "初始状态矩阵:\n"
        s += ArrayUtility.outputArray2D(initStatus, 2)
        s += "走棋过程:\n"
        for (step in steps) {
            s += step.toString()
        }
        s += "最终状态矩阵:\n"
        s += ArrayUtility.outputArray2D(finalStatus, 2)
        return s
    }
}