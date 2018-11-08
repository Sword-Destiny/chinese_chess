package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.ai.Score
import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.ChessMan
import com.yuanhao.chinesechess.settings.Settings
import java.awt.Point
import java.io.Serializable

/**
 * c:移动的棋子颜色
 * uc:用户棋子颜色
 * n:移动的棋子名字
 */
class Step constructor(f: Point, t: Point, n: String, man: ChessMan, uc: ChessColor, rs: Double, bs: Double, e: Double) : Serializable {
    val info: String // 走棋信息
    val from: Point = f // 棋子原位置
    val to: Point = t // 棋子新位置
    val chess = man // 棋子
    val redScore = rs // 此步走完之后红方棋面分数
    val blackScore = bs // 此步走完之后黑方棋面分数
    val eatScore = Score.EAT_FACTOR * e // 吃子得分
    var differentKingWillDie = false // 是否将军
    var up = 0.0 // 局势变化增量,也就是棋面向自己胜利倾斜的程度

    init {
        val c = man.color
        // 用户执黑坐标旋转
        val fy = if (uc == ChessColor.RED) f.y else Settings.MAX_Y - f.y
        val ty = if (uc == ChessColor.RED) t.y else Settings.MAX_Y - t.y
        val fx = if (uc == ChessColor.RED) f.x else Settings.MAX_X - f.x
        val tx = if (uc == ChessColor.RED) t.x else Settings.MAX_X - t.x
        // 判断动作为进,退或者平
        // 对于用户,以上为进,下为退
        // 对于电脑,以下为进,上为退
        val action =
                if (c == uc) { // 棋子颜色等于用户颜色,这是用户走棋
                    when {
                        fy > ty -> "退"
                        fy < ty -> "进"
                        else -> "平"
                    }
                } else { // 电脑走棋
                    when {
                        fy > ty -> "进"
                        fy < ty -> "退"
                        else -> "平"
                    }
                }
        val fsx = if (c == uc) user_numbers[fx] else computer_numbers[fx]
        val tsx = if (c == uc) user_numbers[tx] else computer_numbers[tx]
        info =
                if (fy == ty)
                    "$n$fsx$action$tsx"
                else
                    "$n$fsx$action${Math.abs(fy - ty)}"
    }

    /**
     * 获取己方分数
     */
    fun getSameColorScore(c: ChessColor): Double {
        return if (c == ChessColor.RED) redScore else blackScore
    }

    /**
     * 获取对方分数
     */
    fun getDifferentColorScore(c: ChessColor): Double {
        return if (c == ChessColor.RED) blackScore else redScore
    }

    /**
     * 是否是静态
     * 动态:会导致吃子或者将军但是将不死
     * 静态:能将死或者不吃子
     */
    fun isStaticStep(): Boolean {
        if (getSameColorScore(chess.color) >= Score.WIN) {
            return true
        }
        if (eatScore > 0.0 || differentKingWillDie) {
            return false
        }
        return true
    }

    companion object {
        // 这个是和棋盘对应的,棋盘上用户这边是九到一,电脑那边是1到9
        val user_numbers = arrayOf("九", "八", "七", "六", "五", "四", "三", "二", "一")
        val computer_numbers = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    }

    override fun toString(): String {
        return "${if (chess.color == ChessColor.RED) "红" else "黑"} : $info \t { (${from.x},${from.y}) -> (${to.x},${to.y}) }   ---   红方棋面得分: $redScore, 黑方棋面得分: $blackScore\n"
    }
}