package com.yuanhao.chinesechess.gui

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Exception
import javax.swing.*

class MainFrame : JFrame() {
    private val game = Game(Settings())
    private val buttons = ArrayList<ChessButton>()
    private val back = JPanel()
    val cont = JPanel()

    companion object {
        const val init_x = 200 // 窗口初始位置
        const val init_y = 100 // 窗口初始位置
        const val total_width = 1200 // 窗口大小
        const val total_height = 950 // 窗口大小
        const val panel_x = 50 // 棋盘位置
        const val panel_y = 50 // 棋盘位置
        const val cell_width_height = 80 // 棋盘格子大小
        const val half_cell = cell_width_height / 2 // 棋盘格子半高
        const val panel_width = cell_width_height * (Settings.MAX_X + 1) //棋盘宽
        const val panel_height = cell_width_height * (Settings.MAX_Y + 1) //棋盘高
    }

    init {
        layout = null
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "中国象棋"
        isResizable = false
        setLocation(init_x, init_y)
        setSize(total_width, total_height)
        layeredPane.layout = null

        // 棋盘背景
        val board = JLabel(ImageIcon("data/board.jpg"))
        board.setSize(panel_width, panel_height)
        board.setLocation(panel_x, panel_y)

        back.isOpaque = false
        back.layout = null
        back.setSize(total_width, total_height)
        back.setLocation(0, 0)
        back.add(board)
        layeredPane.add(back, Int.MIN_VALUE)

        // 棋子
        cont.isOpaque = false
        cont.layout = null
        cont.setSize(total_width, total_height)
        cont.setLocation(0, 0)
        layeredPane.add(cont, 0)
        for (man in game.redAliveChesses) {
            val p = LocationUtility.chessBoardToFrame(man.location, game)
            val btn = ChessButton(man.color, p.x, p.y, cell_width_height - 16, man)
            chessButtonClicked(btn)
        }
        for (man in game.blackAliveChesses) {
            val p = LocationUtility.chessBoardToFrame(man.location, game)
            val btn = ChessButton(man.color, p.x, p.y, cell_width_height - 16, man)
            chessButtonClicked(btn)
        }

        game.startGame()

        cont.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                val p = LocationUtility.frameToChessBoard(e!!.point, game)
                moveChess(p)
                super.mouseClicked(e)
            }
        })

    }

    /**
     * 处理点击棋子
     */
    private fun chessButtonClicked(btn: ChessButton) {
        val man = btn.chess
        buttons.add(btn)
        cont.add(btn)
        btn.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if(game.userGo) {
                    if (man.color == game.settings.userColor) {
                        man.isSelected = !man.isSelected
                        for (m in game.getSameColorChesses(man.color)) {
                            if (m != man) {
                                m.isSelected = false
                            }
                        }
                    } else {
                        if (moveChess(man.location)) {
                            cont.remove(btn)
                        }
                    }
                    for (b in buttons) {
                        b.repaint()
                    }
                }
                super.mouseClicked(e)
            }
        })
    }

    /**
     * 移动棋子
     */
    fun moveChess(p: Point): Boolean {
        for (btn in buttons) {
            if (btn.chess.isSelected) {
                if (btn.chess.canGo(p.x, p.y)) {
                    try {
                        btn.chess.moveTo(p.x, p.y)
                        val fp = LocationUtility.chessBoardToFrame(btn.chess.location, game)
                        btn.move(fp.x, fp.y, cell_width_height - 10)
                        btn.chess.isSelected = false
                        game.userGo = ! game.userGo
                        return true
                    } catch (e: Exception) {
                        println(e.message)
                        JOptionPane.showMessageDialog(null, e.message, "错误", JOptionPane.ERROR_MESSAGE)
                        return false
                    }
                }
                break
            }
        }
        return false
    }

}
