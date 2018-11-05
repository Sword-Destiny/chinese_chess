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
class LocationUtility {

    companion object {
        /**
         * 检测某个棋子是不是在两个棋子中间(横向)
         * check为待测点
         */
        private fun checkBetweenX(check: Point, p: Point, q: Point): Boolean {
            if (check.y == p.y && check.y == q.y) {
                if (check.x < p.x && check.x > q.x) {
                    return true
                }
                if (check.x > p.x && check.x < q.x) {
                    return true
                }
            }
            return false
        }

        /**
         * 检测某个棋子是不是在两个棋子中间(纵向)
         * check为待测点
         */
        internal fun checkBetweenY(check: Point, p: Point, q: Point): Boolean {
            if (check.x == p.x && check.x == q.x) {
                if (check.y < p.y && check.y > q.y) {
                    return true
                }
                if (check.y > p.y && check.y < q.y) {
                    return true
                }
            }
            return false
        }

        internal fun checkBetweenXY(check: Point, p: Point, q: Point): Boolean {
            return checkBetweenY(check, p, q) || checkBetweenX(check, p, q)
        }

        /**
         * 检查棋子是否在两个棋子中间,二维
         * check为待测点
         */
        internal fun checkBetween2D(check: Point, p: Point, q: Point): Boolean {
            if (check.x < p.x && check.x > q.x || check.x > p.x && check.x < q.x) {
                if (check.y < p.y && check.y > q.y || check.y > p.y && check.y < q.y) {
                    return true
                }
            }
            return false
        }

        /**
         * 检测日字，马脚
         * check为待测点
         */
        internal fun checkBetweenT2O1(check: Point, source: Point, target: Point): Boolean {
            if (Math.abs(check.x - source.x) == 1 && check.y == source.y || Math.abs(check.y - source.y) == 1 && check.x == source.x) {
                if (Math.abs(check.x - target.x) == 1 && Math.abs(check.y - target.y) == 1) {
                    return true
                }
            }
            return false
        }


        /**
         * 界面坐标到棋盘坐标
         */
        internal fun frameToChessBoard(p: Point, game: Game): Point {
            val x = p.x - MainFrame.panel_x - MainFrame.half_cell
            val y = p.y - MainFrame.panel_y - MainFrame.half_cell
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
        internal fun chessBoardToFrame(p: Point, game: Game): Point {
            var px = p.x
            var py = p.y

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
}

