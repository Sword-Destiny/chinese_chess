package com.yuanhao.chinesechess.settings

import com.yuanhao.chinesechess.main.ChessMan

import java.io.Serializable
import java.util.ArrayList

/**
 * 设置
 */
class Settings : Serializable {
    private val userColor: UserColor//用户执红或者执黑
    private val firstStep: FirstStep//用户先手还是电脑先手
    private val redDeficiency: ArrayList<ChessMan>//红方让子
    private val blackDeficiency: ArrayList<ChessMan>//黑方让子
    private val totalTimeSecond: Long//总时长
    private val stepTimeSecond: Int//每一步时长

    constructor() {
        userColor = UserColor.RED
        firstStep = FirstStep.USER
        redDeficiency = ArrayList()
        blackDeficiency = ArrayList()
        totalTimeSecond = java.lang.Long.MAX_VALUE
        stepTimeSecond = Integer.MAX_VALUE
    }

    constructor(userColor: UserColor, firstStep: FirstStep, redDeficiency: ArrayList<ChessMan>, blackDeficiency: ArrayList<ChessMan>, totalTimeSecond: Long, stepTimeSecond: Int) {
        this.userColor = userColor
        this.firstStep = firstStep
        this.redDeficiency = redDeficiency
        this.blackDeficiency = blackDeficiency
        this.totalTimeSecond = totalTimeSecond
        this.stepTimeSecond = stepTimeSecond
    }

    companion object {
        const val MAX_X = 8
        const val MAX_Y = 8
        const val MIN_X = 0
        const val MIN_Y = 0
    }
}
