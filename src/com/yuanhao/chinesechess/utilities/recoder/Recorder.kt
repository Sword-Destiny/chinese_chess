package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.utilities.common.ArrayUtility
import java.io.Serializable

/**
 * 记录步骤
 */
class Recorder(matrix: Array<Array<Int>>, g: Game) : Serializable {

    internal val steps = ArrayList<Step>() // 走棋记录
    internal val initStatus: Array<Array<Int>> // 初始棋盘
    internal val finalStatus: Array<Array<Int>> // 最终棋盘或者说当前棋盘
    internal val game = g

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
        finalStatus[s.toX][s.toY] = finalStatus[s.fromX][s.fromY]
        finalStatus[s.fromX][s.fromY] = 0
    }

    /**
     * 最后一步
     */
    fun lastStep(): Step {
        return steps.last()
    }

    /**
     * 清除记录
     */
    fun clear() {
        steps.clear()
    }

    /**
     * 悔棋
     */
    fun cancel() {
        if (steps.isEmpty()) {
            return
        }
        val step = steps.last()
        step.chess.setLocation(step.fromX, step.fromY)
        val differentChesses = game.getDifferentDeadChesses(step.chess.color)
        if (differentChesses.isNotEmpty()) {
            val last = differentChesses.last()
            if (last.x == step.toX && last.y == step.toY) {
                last.alive()
            }
        }
        game.userGo = !game.userGo
        steps.removeAt(steps.lastIndex)
        if (steps.isNotEmpty()) {
            val last = lastStep()
            game.redScore = last.redScore
            game.blackScore = last.blackScore
        } else {
            game.redScore = 0.0
            game.blackScore = 0.0
        }
    }

    /**
     * 应用一个走棋着法
     */
    fun applyStep(s: Step) {
        game.userGo = !game.userGo
        game.redScore = s.redScore
        game.blackScore = s.blackScore
        s.chess.setLocation(s.toX, s.toY)
        for (man in game.getDifferentColorChesses(s.chess.color)) {
            if (man.x == s.toX && man.y == s.toY) {
                man.die()
                break
            }
        }
        steps.add(s)
    }

    override fun toString(): String {
        var s = "初始状态矩阵:\n"
        s += ArrayUtility.outputArray2D(initStatus, 2)
        s += "走棋过程:\n"
        for (step in steps) {
            s += step.toString() + "\n"
        }
        s += "最终状态矩阵:\n"
        s += ArrayUtility.outputArray2D(finalStatus, 2)
        return s
    }
}