package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.gui.MainFrame

object ChineseChess {
    @JvmStatic
    fun main(args: Array<String>) {
        val chessFrame = MainFrame()
        chessFrame.isVisible = true
    }
}
