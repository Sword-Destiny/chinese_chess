package com.yuanhao.chinesechess.utilities.common

import com.yuanhao.chinesechess.gui.MainFrame
import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.settings.Settings
import java.awt.Point

/**
 * 坐标处理工具类
 * 当用户执黑棋的时候,界面会旋转
 * 由于Windows窗口的y轴是向下的,所以还涉及到y轴反转
 */
object LocationUtility {
    /**
     * 检测某个棋子是不是在两个棋子中间(横向)
     * check为待测点
     */
    private fun checkBetweenX(checkX: Int, checkY: Int, pX: Int, pY: Int, qX: Int, qY: Int): Boolean {
        if (checkY == pY && checkY == qY) {
            if (checkX in (qX + 1)..(pX - 1)) {
                return true
            }
            if (checkX in (pX + 1)..(qX - 1)) {
                return true
            }
        }
        return false
    }

    /**
     * 检测某个棋子是不是在两个棋子中间(纵向)
     * check为待测点
     */
    internal fun checkBetweenY(checkX: Int, checkY: Int, pX: Int, pY: Int, qX: Int, qY: Int): Boolean {
        if (checkX == pX && checkX == qX) {
            if (checkY in (qY + 1)..(pY - 1)) {
                return true
            }
            if (checkY in (pY + 1)..(qY - 1)) {
                return true
            }
        }
        return false
    }

    internal fun checkBetweenXY(checkX: Int, checkY: Int, pX: Int, pY: Int, qX: Int, qY: Int): Boolean {
        return checkBetweenY(checkX, checkY, pX, pY, qX, qY) || checkBetweenX(checkX, checkY, pX, pY, qX, qY)
    }

    /**
     * 检查棋子是否在两个棋子中间,二维
     * check为待测点
     */
    internal fun checkBetween2D(checkX: Int, checkY: Int, pX: Int, pY: Int, qX: Int, qY: Int): Boolean {
        if (checkX in (qX + 1)..(pX - 1) || checkX in (pX + 1)..(qX - 1)) {
            if (checkY in (qY + 1)..(pY - 1) || checkY in (pY + 1)..(qY - 1)) {
                return true
            }
        }
        return false
    }

    /**
     * 检测日字，马脚
     * check为待测点
     */
    internal fun checkBetweenT2O1(checkX: Int, checkY: Int, sourceX: Int, sourceY: Int, targetX: Int, targetY: Int): Boolean {
        if (Math.abs(checkX - sourceX) == 1 && checkY == sourceY || Math.abs(checkY - sourceY) == 1 && checkX == sourceX) {
            if (Math.abs(checkX - targetX) == 1 && Math.abs(checkY - targetY) == 1) {
                return true
            }
        }
        return false
    }


    /**
     * 界面坐标到棋盘坐标
     */
    internal fun frameToChessBoard(pX: Int, pY: Int, game: Game): Point {
        val x = pX - MainFrame.panel_x - MainFrame.half_cell
        val y = pY - MainFrame.panel_y - MainFrame.half_cell
        var px = Math.round((x.toDouble()) / (MainFrame.cell_width_height.toDouble())).toInt()
        var py = Math.round((y.toDouble()) / (MainFrame.cell_width_height.toDouble())).toInt()

        // 反转y轴
        py = Settings.MAX_Y - py

        // 用户执黑,棋子位置旋转
        if (game.settings.userColor == ChessColor.BLACK) {
            px = Settings.MAX_X - px
            py = Settings.MAX_Y - py
        }
        return Point(px, py)
    }

    /**
     * 棋盘坐标到界面坐标
     */
    internal fun chessBoardToFrame(pX: Int, pY: Int, game: Game): Point {
        var px = pX
        var py = pY

        // 用户执黑,棋子位置旋转
        if (game.settings.userColor == ChessColor.BLACK) {
            px = Settings.MAX_X - px
            py = Settings.MAX_Y - py
        }

        // 反转y轴
        py = Settings.MAX_Y - py

        val x = px * MainFrame.cell_width_height + MainFrame.half_cell + MainFrame.panel_x
        val y = py * MainFrame.cell_width_height + MainFrame.half_cell + MainFrame.panel_y
        return Point(x, y)
    }
}

