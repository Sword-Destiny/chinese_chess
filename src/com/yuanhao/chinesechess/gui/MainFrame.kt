package com.yuanhao.chinesechess.gui

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.settings.Settings
import javax.swing.*

class MainFrame : JFrame() {
    private val game = Game(Settings())

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "中国象棋"
        isResizable = false
        setLocation(200, 100)
        setSize(1200, 800)
        game.initGame()
    }
}
