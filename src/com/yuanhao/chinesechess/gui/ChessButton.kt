package com.yuanhao.chinesechess.gui

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.ChessMan
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton


/**
 * color:颜色
 * x,y:位置
 * s:大小
 */
class ChessButton(color: ChessColor, x: Int, y: Int, s: Int, man: ChessMan) : JButton(man.name) {

    private var hover = false
    var cellSize: Int
    var fontSize: Int
    val chess = man
    var lx: Int
    var ly: Int

    init {
        text = man.name
        isOpaque = false
        isBorderPainted = false
        foreground = if (color == ChessColor.RED) Color.RED else Color.BLACK
        isFocusPainted = false
        isContentAreaFilled = false
        lx = x
        ly = y
        cellSize = s
        fontSize = cellSize / 2 - 3
        font = Font("STZhongsong", Font.PLAIN, fontSize)
        setSize(cellSize, cellSize)
        setLocation(x - cellSize / 2, y - cellSize / 2)
        repaint()
        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                if(man.game.settings.userColor == man.color) {
                    cellSize += 6
                    fontSize = cellSize / 2 - 3
                    font = Font("STZhongsong", Font.PLAIN, fontSize)
                    setSize(cellSize, cellSize)
                    setLocation(lx - cellSize / 2, ly - cellSize / 2)
                    hover = true
                    repaint()
                }
            }

            override fun mouseExited(e: MouseEvent?) {
                if(man.game.settings.userColor == man.color) {
                    cellSize -= 6
                    fontSize = cellSize / 2 - 3
                    font = Font("STZhongsong", Font.PLAIN, fontSize)
                    setSize(cellSize, cellSize)
                    setLocation(lx - cellSize / 2, ly - cellSize / 2)
                    hover = false
                    repaint()
                }
            }
        })
    }

    fun move(x: Int, y: Int, s: Int) {
        lx = x
        ly = y
        cellSize = s
        fontSize = cellSize / 2 - 3
        font = Font("STZhongsong", Font.PLAIN, fontSize)
        setSize(cellSize, cellSize)
        setLocation(x - cellSize / 2, y - cellSize / 2)
        repaint()
    }

    override fun paintComponent(g: Graphics?) {
        val h = this.height
        val w = this.width
        val g2d = g!!.create() as Graphics2D
        if (chess.isSelected) {
            g2d.color = Color(180, 180, 180)
        } else {
            g2d.color = Color(240, 240, 240)
        }
        val strokeSize = 3
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.stroke = BasicStroke(strokeSize.toFloat())
        g2d.fillOval(1, 1, h - 3, w - 3)
        if (chess.color == ChessColor.RED) {
            g2d.color = Color.RED
        } else {
            g2d.color = Color.BLACK
        }
        g2d.drawOval(1, 1, h - 3, w - 3)
        g2d.drawOval(strokeSize * 2 + 1, strokeSize * 2 + 1, h - strokeSize * 4 - 3, w - strokeSize * 4 - 3)
        g2d.dispose()
        super.paintComponent(g)
    }

}