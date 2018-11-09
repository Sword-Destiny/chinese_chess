package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.ai.Score
import com.yuanhao.chinesechess.exceptions.KingConflictException
import com.yuanhao.chinesechess.exceptions.KingWillDieException
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.Point
import java.util.ArrayList

/**
 * 马
 */
class Knight internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, "马", 180.0) {

    override fun countStaticScore() {
        locationScore = 0.0
        for (man in game.getDifferentColorChesses(color)) {
            if (man is King) {
                val d = 16 - this.distance(man)
                locationScore += d * 10
            }
        }
        flexibilityScore = Score.BASIC_SCORE * listAllLocationsCanGo().size / 8.0
        safetyScore = 0.0
        for (man in game.getSameColorChesses(color)) {
            if (man != this) {
                if (man.canGo(this.x, this.y)) {
                    safetyScore += Score.SAFETY_RATE * (Score.BASIC_SCORE + locationScore)
                }
            }
        }
        staticScore = basicScore + locationScore + flexibilityScore + safetyScore
    }

    override fun matrixNumber(): Int =
            if (color == ChessColor.RED)
                if (left) 41 else 43
            else
                if (left) 42 else 44

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move = arrayOf(arrayOf(2, 1), arrayOf(2, -1), arrayOf(-2, 1), arrayOf(-2, -1), arrayOf(1, 2), arrayOf(1, -2), arrayOf(-1, 2), arrayOf(-1, -2))
        for (m in move) {
            val p = Point(this.x + m[0], this.y + m[1])
            if (p.x == x && p.y == y) {
                if (!checkKnightBan(p.x, p.y)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检查马脚中间有没有棋子
     */
    private fun checkKnightBan(targetX: Int, targetY: Int): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (LocationUtility.checkBetweenT2O1(man.x, man.y, x, y, targetX, targetY)) {
                return true
            }
        }

        for (man in game.getDifferentColorChesses(color)) {
            if (LocationUtility.checkBetweenT2O1(man.x, man.y, x, y, targetX, targetY)) {
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
        if (checkKingConflict(x, y)) {
            throw KingConflictException("将帅不能照面")
        }
        if (checkKingWillDie(x, y)) {
            throw KingWillDieException((if (color == ChessColor.RED) "帅" else "将") + "会被吃掉")
        }
        super.moveTo(x, y)
    }

    override fun setInitialLocation() {
        if (color === ChessColor.RED) {
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
            val p = Point(this.x + m[0], this.y + m[1])
            if (checkInBoard(p.x, p.y) && !checkSameColorChessExists(p.x, p.y)) {
                if (!checkKnightBan(p.x, p.y)) {
                    points.add(p)
                }
            }
        }
        return points
    }

}