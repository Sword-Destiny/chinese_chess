package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.Point
import java.util.ArrayList

class Rook internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, "车") {

    override fun matrixNumber(): Int =
            if (color == ChessColor.RED)
                if (left) 51 else 53
            else
                if (left) 52 else 54

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
                if (!checkRookBan(m)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检查车中间有没有棋子,能不能走
     */
    private fun checkRookBan(target: Point): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (LocationUtility.checkBetweenXY(man.location, location, target)) {
                return true
            }
        }

        for (man in game.getDifferentColorChesses(color)) {
            if (LocationUtility.checkBetweenXY(man.location, location, target)) {
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
        if (color === ChessColor.RED) {
            if (left) {
                setLocation(0, 0)
            } else {
                setLocation(8, 0)
            }
        } else {
            if (left) {
                setLocation(0, 9)
            } else {
                setLocation(8, 9)
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
                if (!checkRookBan(m)) {
                    points.add(m)
                }
            }
        }
        return points
    }

}