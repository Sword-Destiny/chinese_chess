package com.yuanhao.chinesechess.utilities.recoder

import java.awt.Point
import java.io.Serializable

class Step constructor(f: Point, t: Point, n: String) : Serializable {
    val name = n
    val from = f
    val to = t
}