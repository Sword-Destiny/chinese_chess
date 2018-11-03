package com.yuanhao.chinesechess.utilities.recoder

import java.io.Serializable

/**
 * 记录步骤
 */
class Recorder(matrix: Array<Array<Int>>) : Serializable {
    // 走棋记录
    private val steps = ArrayList<Step>()
    // 初始棋盘
    internal val initStatus: Array<Array<Int>>
    // 最终棋盘或者说当前棋盘
    internal val finalStatus: Array<Array<Int>>

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
     * 记录
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


}