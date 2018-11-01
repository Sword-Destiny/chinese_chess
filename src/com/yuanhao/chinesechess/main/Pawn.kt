package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import java.awt.Point
import java.util.ArrayList

/**
 * 兵卒
 */
class Pawn internal constructor(g: Game, c: ChessColor, private val index: Int) : ChessMan(g, c, "车") {

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move =
                if (color == ChessColor.red)
                    if (location.y <= 4) arrayOf(arrayOf(0, 1))
                    else arrayOf(arrayOf(0, 1), arrayOf(-1, 0), arrayOf(1, 0))
                else
                    if (location.y >= 5) arrayOf(arrayOf(0, -1))
                    else arrayOf(arrayOf(0, -1), arrayOf(-1, 0), arrayOf(1, 0))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (p.x == x && p.y == y) {
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
            val x = index * 2
            setLocation(x, 3)
        } else {
            val x = index * 2
            setLocation(x, 6)
        }
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        val move =
                if (color == ChessColor.red)
                    if (location.y <= 4) arrayOf(arrayOf(0, 1))
                    else arrayOf(arrayOf(0, 1), arrayOf(-1, 0), arrayOf(1, 0))
                else
                    if (location.y >= 5) arrayOf(arrayOf(0, -1))
                    else arrayOf(arrayOf(0, -1), arrayOf(-1, 0), arrayOf(1, 0))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (checkInBoard(p.x, p.y) && !checkSameColorChessExists(p.x, p.y)) {
                points.add(p)
            }
        }
        return points
    }

}