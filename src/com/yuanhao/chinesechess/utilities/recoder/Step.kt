package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.main.ChessColor
import java.awt.Point
import java.io.Serializable

/**
 * c:移动的棋子颜色
 * uc:用户棋子颜色
 */
class Step constructor(f: Point, t: Point, n: String, c: ChessColor, uc: ChessColor) : Serializable {
    private val info: String
    val from: Point = f
    val to: Point = t

    init {
        val action =
                if (c == uc) {
                    when {
                        f.y > t.y -> "退"
                        f.y < t.y -> "进"
                        else -> "平"
                    }
                } else {
                    when {
                        f.y > t.y -> "进"
                        f.y < t.y -> "退"
                        else -> "平"
                    }
                }
        val fx = if (c == uc) user_numbers[f.x] else computer_numbers[f.x]
        val tx = if (c == uc) user_numbers[t.x] else computer_numbers[t.x]
        info =
                if (f.y == t.y)
                    "$n$fx$action$tx"
                else
                    "$n$fx$action${Math.abs(f.y - t.y)}"
    }

    companion object {
        val user_numbers = arrayOf("九", "八", "七", "六", "五", "四", "三", "二", "一")
        val computer_numbers = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    }
}