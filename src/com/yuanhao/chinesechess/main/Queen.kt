package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import com.yuanhao.chinesechess.exceptions.KingWillDieException

import java.awt.*
import java.util.ArrayList

/**
 * 士
 */
class Queen internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, "士") {

    override fun matrixNumber(): Int =
            if (color == ChessColor.RED)
                if (left) 21 else 23
            else
                if (left) 22 else 24

    @Throws(Exception::class)
    override fun moveTo(x: Int, y: Int) {
        if (!canGo(x, y)) {
            return
        }
        if (checkCommanderConflict(x, y)) {
            throw CommanderConflictException("将帅不能照面")
        }
        if (checkKingWillDie(x, y)) {
            throw KingWillDieException((if (color == ChessColor.RED) "帅" else "将") + "会被吃掉")
        }
        super.moveTo(x, y)
    }

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        if (x < 3 || y < 0 || x > 5 || y > 2) {
            if (color == ChessColor.RED) {
                return false
            }
        }
        if (x < 3 || y < 7 || x > 5 || y > 9) {
            if (color == ChessColor.BLACK) {
                return false
            }
        }
        val move = arrayOf(intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1))
        for (m in move) {
            if (x == location.x + m[0] && y == location.y + m[1]) {
                return true
            }
        }
        return false
    }

    override fun setInitialLocation() {
        if (color === ChessColor.RED) {
            if (left) {
                setLocation(3, 0)
            } else {
                setLocation(5, 0)
            }
        } else {
            if (left) {
                setLocation(3, 9)
            } else {
                setLocation(5, 9)
            }
        }
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        val move = arrayOf(intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (checkInBoard(p.x, p.y) && !checkSameColorChessExists(p.x, p.y)) {
                if (p.x < 3 || p.y < 0 || p.x > 5 || p.y > 2) {
                    if (color == ChessColor.RED) {
                        points.add(p)
                    }
                }
                if (p.x < 3 || p.y < 7 || p.x > 5 || p.y > 9) {
                    if (color == ChessColor.BLACK) {
                        points.add(p)
                    }
                }
            }
        }
        return points
    }

}
