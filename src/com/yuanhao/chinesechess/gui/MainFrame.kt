package com.yuanhao.chinesechess.gui

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.main.GameStatus
import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Exception
import java.util.Timer
import javax.swing.*
import javax.swing.JDialog
import java.util.TimerTask
import javax.swing.JOptionPane

/**
 * 主界面
 */
class MainFrame : JFrame() {
    private val game = Game(Settings()) // 游戏
    private val buttons = ArrayList<ChessButton>() // 所有的棋子按钮
    private val back = JPanel() // 背景面板
    val cont = JPanel() // 内容面板
    private val timer = Timer() // 计时器

    private var aiThread: Thread? = null // ai后台任务
    private var userGoWithoutAi = false // 用户代替AI

    companion object {
        const val init_x = 200 // 窗口初始位置
        const val init_y = 100 // 窗口初始位置
        const val total_width = 820 // 窗口大小
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

        // 添加棋盘背景
        val board = JLabel(ImageIcon("data/board.jpg"))
        board.setSize(panel_width, panel_height)
        board.setLocation(panel_x, panel_y)

        back.isOpaque = false
        back.layout = null
        back.setSize(total_width, total_height)
        back.setLocation(0, 0)
        back.add(board)
        layeredPane.add(back, Int.MIN_VALUE)

        // 添加面板和所有的棋子
        cont.isOpaque = false
        cont.layout = null
        cont.setSize(total_width, total_height)
        cont.setLocation(0, 0)
        layeredPane.add(cont, 0)
        for (man in game.redAliveChesses) {
            val p = LocationUtility.chessBoardToFrame(man.x, man.y, game)
            val btn = ChessButton(man.color, p.x, p.y, cell_width_height - 16, man)
            buttons.add(btn)
        }
        for (man in game.blackAliveChesses) {
            val p = LocationUtility.chessBoardToFrame(man.x, man.y, game)
            val btn = ChessButton(man.color, p.x, p.y, cell_width_height - 16, man)
            buttons.add(btn)
        }
        for (btn in buttons) {
            chessButtonClicked(btn)
        }

        cont.addMouseListener(object : MouseAdapter() {
            /**
             * 鼠标点击棋盘面板事件
             */
            override fun mouseClicked(e: MouseEvent?) {
                if (game.status != GameStatus.STARTED) {
                    super.mouseClicked(e)
                    return
                }
                val p = LocationUtility.frameToChessBoard(e!!.x, e.y, game)
                if (game.userGo) {
                    userMoveChess(p.x, p.y)
                } else {
                    computerMoveChess(p.x, p.y)
                }
                for (b in buttons) {
                    b.repaint()
                }
                super.mouseClicked(e)
            }
        })

        game.start()
    }

    /**
     * 处理点击棋子
     */
    private fun chessButtonClicked(btn: ChessButton) {
        val man = btn.chess
        cont.add(btn)
        btn.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (game.status != GameStatus.STARTED) {
                    super.mouseClicked(e)
                    return
                }
                if (game.userGo) { // 用户点击
                    if (man.color == game.settings.userColor) {
                        man.isSelected = !man.isSelected
                        for (m in game.getSameColorChesses(man.color)) {
                            if (m != man) {
                                m.isSelected = false
                            }
                        }
                    } else {
                        if (userMoveChess(man.x, man.y)) {
                            cont.remove(btn)
                        }
                    }
                } else { // 电脑点击
                    if (man.color == game.settings.computerColor) {
                        man.isSelected = !man.isSelected
                        for (m in game.getSameColorChesses(man.color)) {
                            if (m != man) {
                                m.isSelected = false
                            }
                        }
                    } else {
                        if (computerMoveChess(man.x, man.y)) {
                            cont.remove(btn)
                        }
                    }
                }
                for (b in buttons) {
                    b.repaint()
                }
                super.mouseClicked(e)
            }
        })
    }

    /**
     * 玩家移动棋子
     */
    fun userMoveChess(px: Int, py: Int): Boolean {
        for (btn in buttons) {
            if (btn.chess.isSelected) {
                if (btn.chess.canGo(px, py)) {
                    return try {
                        print("User: ")
                        btn.chess.moveTo(px, py)
                        val fp = LocationUtility.chessBoardToFrame(btn.chess.x, btn.chess.y, game)
                        btn.move(fp.x, fp.y, cell_width_height - 10)
                        btn.chess.isSelected = false
                        if (game.checkKingWillDie(game.settings.computerColor)) {
                            showMessage("将军")
                        }
                        if (game.checkGameOver(game.settings.computerColor)) {
                            showMessage("玩家获胜")
                            game.end(game.settings.userColor)
                        } else {
                            startAIThread()
                        }
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        JOptionPane.showMessageDialog(null, e.message, "错误", JOptionPane.ERROR_MESSAGE)
                        false
                    }
                }
                break
            }
        }
        return false
    }

    /**
     * AI走棋
     */
    private fun startAIThread() {
        userGoWithoutAi = false
        aiThread = object : Thread() {
            override fun run() {
                println("ai thread start")
                val step = game.ai.startAnalysis(game)
                if (!userGoWithoutAi && step != null) {
                    for (btn in buttons) {
                        if (btn.chess.x == step.fromX && btn.chess.y == step.fromY) {
                            try {
                                for (b in buttons) {
                                    if (b.chess.x == step.toX && b.chess.y == step.toY) {
                                        cont.remove(b)
                                        break
                                    }
                                }
                                print("AI: ")
                                btn.chess.moveTo(step.toX, step.toY)
                                val fp = LocationUtility.chessBoardToFrame(btn.chess.x, btn.chess.y, game)
                                btn.move(fp.x, fp.y, cell_width_height - 10)
                                btn.chess.isSelected = false
                                if (game.checkKingWillDie(game.settings.userColor)) {
                                    showMessage("将军")
                                }
                                if (game.checkGameOver(game.settings.userColor)) {
                                    showMessage("电脑获胜")
                                    game.end(game.settings.computerColor)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                JOptionPane.showMessageDialog(null, e.message, "错误", JOptionPane.ERROR_MESSAGE)
                            }
                        }
                    }
                }
            }
        }
        aiThread?.start()
    }

    /**
     * 停止AI分析线程
     */
    @Suppress("DEPRECATION")
    private fun stopAIThread() {
        userGoWithoutAi = true
        aiThread?.stop()
        println("ai thread stop")
    }

    /**
     * 电脑AI未实现的时候为了测试某些功能,通过用户走棋代替电脑AI,后续会改变
     */
    fun computerMoveChess(px: Int, py: Int): Boolean {
        for (btn in buttons) {
            if (btn.chess.isSelected) {
                if (btn.chess.canGo(px, py)) {
                    return try {
                        print("User as Computer: ")
                        btn.chess.moveTo(px, py)
                        stopAIThread()
                        val fp = LocationUtility.chessBoardToFrame(btn.chess.x, btn.chess.y, game)
                        btn.move(fp.x, fp.y, cell_width_height - 10)
                        btn.chess.isSelected = false
                        if (game.checkKingWillDie(game.settings.userColor)) {
                            showMessage("将军")
                        }
                        if (game.checkGameOver(game.settings.userColor)) {
                            showMessage("电脑获胜")
                            game.end(game.settings.computerColor)
                        }
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        JOptionPane.showMessageDialog(null, e.message, "错误", JOptionPane.ERROR_MESSAGE)
                        false
                    }
                }
                break
            }
        }
        return false
    }

    /**
     * 显示一条1.5秒的信息
     */
    private fun showMessage(msg: String) {
        println(msg)
        val op = JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE)
        val dialog = op.createDialog("提示")
        dialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        dialog.isAlwaysOnTop = true
        dialog.isModal = false
        dialog.isVisible = true
        dialog.setSize(400, 200)
        dialog.setLocation(this.x + this.width / 2 - dialog.width / 2, this.y + this.height / 2 + dialog.height / 2)

        timer.schedule(object : TimerTask() {
            override fun run() {
                dialog.isVisible = false
                dialog.dispose()
            }
        }, 1500)

    }

}
