package com.yuanhao.chinesechess.utilities.recoder

import java.io.Serializable

class Recorder(val matrix: Array<Array<Int>>) : Serializable {
    val steps = ArrayList<Step>()
}