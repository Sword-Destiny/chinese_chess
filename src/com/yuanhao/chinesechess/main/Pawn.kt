package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.ai.Score
import com.yuanhao.chinesechess.exceptions.KingConflictException
import com.yuanhao.chinesechess.exceptions.KingWillDieException
import java.awt.Point
import java.util.ArrayList

/**
 * 兵卒
 */
class Pawn internal constructor(g: Game, c: ChessColor, private val index: Int) : ChessMan(g, c, if (c == ChessColor.RED) "兵" else "卒", 30.0) {

    override fun countStaticScore() {
        locationScore = 0.0
        for (man in game.getDifferentColorChesses(color)) {
            if (man is King) {
                val d = 16 - location.distance(man.location)
                locationScore += d * 10
                // 兵卒不能后退,如果太靠近底部则没有太大的威胁
                if (location.y > man.location.y && color == ChessColor.RED) {
                    locationScore -= 50
                }
                if (location.y < man.location.y && color == ChessColor.BLACK) {
                    locationScore -= 50
                }
            }
        }
        flexibilityScore = Score.BASIC_SCORE * listAllLocationsCanGo().size / 3.0
        safetyScore = 0.0
        for (man in game.getSameColorChesses(color)) {
            if (man != this) {
                if (man.canGo(location.x, location.y)) {
                    safetyScore += Score.SAFETY_RATE * 2 * (Score.BASIC_SCORE + locationScore)
                }
            }
        }
        staticScore = basicScore + locationScore + flexibilityScore + safetyScore
    }

    override fun matrixNumber(): Int =
            if (color == ChessColor.RED)
                71 + index * 2
            else
                70 + index * 2

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move =
                if (color == ChessColor.RED)
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
                if (color == ChessColor.RED)
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