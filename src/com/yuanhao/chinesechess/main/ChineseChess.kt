package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.gui.MainFrame
import javax.swing.UIManager

object ChineseChess {
    @JvmStatic
    fun main(args: Array<String>) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val chessFrame = MainFrame()
        chessFrame.isVisible = true
    }
}
