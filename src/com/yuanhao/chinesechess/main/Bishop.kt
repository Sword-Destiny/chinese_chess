package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import com.yuanhao.chinesechess.exceptions.KingWillDieException
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.Point
import java.util.ArrayList

/**
 * 相，象
 */
class Bishop internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, if (c == ChessColor.RED) "相" else "象") {

    override fun matrixNumber(): Int =
            if (color == ChessColor.RED)
                if (left) 31 else 33
            else
                if (left) 32 else 34

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move = arrayOf(arrayOf(2, 2), arrayOf(2, -2), arrayOf(-2, 2), arrayOf(-2, -2))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (p.x == x && p.y == y) {
                if (color == ChessColor.RED) {
                    if (p.y <= 4) {
                        if (!checkBishopMidChess(p)) {
                            return true
                        }
                    }
                } else {
                    if (p.y >= 5) {
                        if (!checkBishopMidChess(p)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     * 检查象脚中间有没有棋子,能不能走
     */
    private fun checkBishopMidChess(target: Point): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (LocationUtility.checkBetween2D(man.location, location, target)) {
                return true
            }
        }

        for (man in game.getDifferentColorChesses(color)) {
            if (LocationUtility.checkBetween2D(man.location, location, target)) {
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
        if (checkKingWillDie(x, y)) {
            throw KingWillDieException((if (color == ChessColor.RED) "帅" else "将") + "会被吃掉")
        }
        super.moveTo(x, y)
    }

    override fun setInitialLocation() {
        if (color === ChessColor.RED) {
            if (left) {
                setLocation(2, 0)
            } else {
                setLocation(6, 0)
            }
        } else {
            if (left) {
                setLocation(2, 9)
            } else {
                setLocation(6, 9)
            }
        }
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        val move = arrayOf(arrayOf(2, 2), arrayOf(2, -2), arrayOf(-2, 2), arrayOf(-2, -2))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (checkInBoard(p.x, p.y) && !checkSameColorChessExists(p.x, p.y)) {
                if (color == ChessColor.RED) {
                    if (p.y <= 4) {
                        if (!checkBishopMidChess(p)) {
                            points.add(p)
                        }
                    }
                } else {
                    if (p.y >= 5) {
                        if (!checkBishopMidChess(p)) {
                            points.add(p)
                        }
                    }
                }
            }
        }
        return points
    }

}