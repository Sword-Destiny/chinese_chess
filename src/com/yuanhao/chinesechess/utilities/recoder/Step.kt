package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.settings.Settings
import java.awt.Point
import java.io.Serializable

/**
 * c:移动的棋子颜色
 * uc:用户棋子颜色
 */
class Step constructor(f: Point, t: Point, n: String, c: ChessColor, uc: ChessColor) : Serializable {
    val info: String
    val from: Point = f
    val to: Point = t
    val color = c

    init {
        val fy = if (uc == ChessColor.RED) f.y else Settings.MAX_Y - f.y
        val ty = if (uc == ChessColor.RED) t.y else Settings.MAX_Y - t.y
        val fx = if (uc == ChessColor.RED) f.x else Settings.MAX_X - f.x
        val tx = if (uc == ChessColor.RED) t.x else Settings.MAX_X - t.x
        val action =
                if (c == uc) {
                    when {
                        fy > ty -> "退"
                        fy < ty -> "进"
                        else -> "平"
                    }
                } else {
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

    companion object {
        val user_numbers = arrayOf("九", "八", "七", "六", "五", "四", "三", "二", "一")
        val computer_numbers = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    }

    override fun toString(): String {
        return "${color.name} : $info { (${from.x},${from.y}) -> (${to.x},${to.y}) }\n"
    }
}