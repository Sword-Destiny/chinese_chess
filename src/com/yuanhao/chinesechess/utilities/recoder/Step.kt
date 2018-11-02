package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.main.ChessColor
import java.awt.Point
import java.io.Serializable

class Step constructor(f: Point, t: Point, n: String, c: ChessColor) : Serializable {
    private val info: String
    val from: Point = f
    val to: Point = t

    init {
        val action =
                if (c == ChessColor.RED) {
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
        info =
                if (f.y == t.y)
                    "$n${f.x}$action${t.x}"
                else
                    "$n${f.x}$action${Math.abs(f.y - t.y)}"
    }
}