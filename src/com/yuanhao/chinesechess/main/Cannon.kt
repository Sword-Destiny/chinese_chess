package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import com.yuanhao.chinesechess.settings.Settings
import java.awt.Point
import java.util.ArrayList

class Cannon internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, "炮") {

    override fun matrixNumber(): Int =
            if (color == ChessColor.red)
                if (left) 61 else 63
            else
                if (left) 62 else 64

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move = ArrayList<Point>()
        for (mx in Settings.MIN_X..Settings.MAX_X + 1) {
            if (mx != location.x)
                move.add(Point(mx, location.y))
        }
        for (my in Settings.MIN_Y..Settings.MAX_Y + 1) {
            if (my != location.y)
                move.add(Point(location.x, my))
        }
        for (m in move) {
            if (m.x == x && m.y == y) {
                if (!checkCannonBan(m)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检查炮能不能走
     */
    private fun checkCannonBan(target: Point): Boolean {
        if (!checkDifferentColorChessExists(target.x, target.y) && 0 == countMidChessNum(target)) {
            return true
        }
        if (checkDifferentColorChessExists(target.x, target.y) && 1 == countMidChessNum(target)) {
            return true
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
                setLocation(1, 2)
            } else {
                setLocation(7, 2)
            }
        } else {
            if (left) {
                setLocation(1, 7)
            } else {
                setLocation(7, 7)
            }
        }
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        val move = ArrayList<Point>()
        for (mx in Settings.MIN_X..Settings.MAX_X + 1) {
            if (mx != location.x)
                move.add(Point(mx, location.y))
        }
        for (my in Settings.MIN_Y..Settings.MAX_Y + 1) {
            if (my != location.y)
                move.add(Point(location.x, my))
        }
        for (m in move) {
            if (checkInBoard(m.x, m.y) && !checkSameColorChessExists(m.x, m.y)) {
                if (!checkCannonBan(m)) {
                    points.add(m)
                }
            }
        }
        return points
    }

}