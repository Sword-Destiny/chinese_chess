package com.yuanhao.chinesechess.settings

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.ChessMan

import java.io.Serializable
import java.util.ArrayList

/**
 * 设置
 */
class Settings(uc: ChessColor = ChessColor.RED, cc: ChessColor = ChessColor.BLACK, fs: FirstStep = FirstStep.USER, rd: ArrayList<ChessMan> = ArrayList(), bd: ArrayList<ChessMan> = ArrayList(), tt: Long = Long.MAX_VALUE, st: Long = 180) : Serializable {
    val userColor = uc // 用户执红或者执黑
    val computerColor = cc // 电脑颜色
    val firstStep = fs // 用户先手还是电脑先手
    private val redDeficiency = rd // 红方让子
    private val blackDeficiency = bd // 黑方让子
    private val totalTimeSecond = tt // 总时长
    private val stepTimeSecond = st // 每一步时长

    companion object {
        const val MAX_X = 8
        const val MAX_Y = 9
        const val MIN_X = 0
        const val MIN_Y = 0
    }

    override fun toString(): String {
        var s = "先手: " + (if (firstStep == FirstStep.USER) "用户" else "电脑") + "\n"
        s += "用户颜色: ${userColor.name}\n电脑颜色: ${computerColor.name}\n"
        s += "红方让子: "
        for (man in redDeficiency) {
            s += man.name + " "
        }
        s += "\n"
        s += "黑方让子: "
        for (man in blackDeficiency) {
            s += man.name + " "
        }
        s += "\n"
        s += "棋局规定总时长(秒): $totalTimeSecond \n"
        s += "每步规定时长(秒): $stepTimeSecond \n"
        return s
    }
}
