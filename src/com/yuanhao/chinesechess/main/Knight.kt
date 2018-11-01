package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.Point
import java.util.ArrayList

/**
 * 马
 */
class Knight internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c) {

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move = arrayOf(arrayOf(2, 1), arrayOf(2, -1), arrayOf(-2, 1), arrayOf(-2, -1), arrayOf(1, 2), arrayOf(1, -2), arrayOf(-1, 2), arrayOf(-1, -2))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (p.x == x && p.y == y) {
                if (!checkKnightBan(p)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检查马脚中间有没有棋子,能不能走
     */
    private fun checkKnightBan(target: Point): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (LocationUtility.checkBetweenT2O1(man.location, location, target)) {
                return true
            }
        }

        for (man in game.getDifferentColorChesses(color)) {
            if (LocationUtility.checkBetweenT2O1(man.location, location, target)) {
                return true
            }
        }
        return false
    }

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

    override fun setInitialLocation() {
        if (color === ChessColor.red) {
            if (left) {
                setLocation(1, 0)
            } else {
                setLocation(7, 0)
            }
        } else {
            if (left) {
                setLocation(1, 9)
            } else {
                setLocation(7, 9)
            }
        }
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        val move = arrayOf(arrayOf(2, 1), arrayOf(2, -1), arrayOf(-2, 1), arrayOf(-2, -1), arrayOf(1, 2), arrayOf(1, -2), arrayOf(-1, 2), arrayOf(-1, -2))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (checkInBoard(p.x, p.y) && !checkSameColorChessExists(p.x, p.y)) {
                if (!checkKnightBan(p)) {
                    points.add(p)
                }
            }
        }
        return points
    }

}