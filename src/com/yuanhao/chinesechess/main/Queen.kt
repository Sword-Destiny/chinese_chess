package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException

import java.awt.*
import java.util.ArrayList

/**
 * 士
 */
class Queen internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c) {

    @Throws(Exception::class)
    override fun moveTo(x: Int, y: Int) {
        if (!canGo(x, y)) {
            return
        }
        if (checkCommanderConflict(x, y)) {
            throw CommanderConflictException("将帅不能照面")
        }
        super.moveTo(x, y)
    }

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        if (location.x != 4) {
            return if (color === ChessColor.red) {
                // 4,1
                x == 4 && y == 1
            } else {
                // 4,7
                x == 4 && y == 7
            }
        } else {
            val move = arrayOf(intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1))
            for (m in move) {
                if (x == location.x + m[0] && y == location.y + m[1]) {
                    return true
                }
            }
        }
        return false
    }

    override fun setInitialLocation() {
        if (color === ChessColor.red) {
            if (left) {
                setLocation(3, 0)
            } else {
                setLocation(5, 0)
            }
        } else {
            if (left) {
                setLocation(3, 8)
            } else {
                setLocation(5, 8)
            }
        }
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        if (location.x != 4) {
            if (color === ChessColor.red) {
                // 4,1
                if (!checkSameColorChessExists(4, 1)) {
                    points.add(Point(4, 1))
                }
            } else {
                // 4,7
                if (!checkSameColorChessExists(4, 7)) {
                    points.add(Point(4, 7))
                }
            }
        } else {
            val move = arrayOf(intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1))
            for (m in move) {
                val p = Point(location.x + m[0], location.y + m[1])
                if (!checkSameColorChessExists(p.x, p.y)) {
                    points.add(p)
                }
            }
        }
        return points
    }

}
