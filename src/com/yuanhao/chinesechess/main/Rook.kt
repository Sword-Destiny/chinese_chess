package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.ai.Score
import com.yuanhao.chinesechess.exceptions.KingConflictException
import com.yuanhao.chinesechess.exceptions.KingWillDieException
import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import java.awt.Point
import java.util.ArrayList

/**
 * 车
 */
class Rook internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, "车", 400.0) {

    override fun countStaticScore() {
        locationScore = 0.0
        if (this.x in 3..5) {
            locationScore += 30
        }
        if (this.y in 7..9 && color == ChessColor.RED) {
            locationScore += 30
        }
        if (this.y in 0..2 && color == ChessColor.BLACK) {
            locationScore += 30
        }
        flexibilityScore = Score.BASIC_SCORE * listAllLocationsCanGo().size / 17.0
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
                if (left) 51 else 53
            else
                if (left) 52 else 54

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            return false
        }
        val move = ArrayList<Point>()
        for (mx in Settings.MIN_X..Settings.MAX_X + 1) {
            if (mx != this.x)
                move.add(Point(mx, this.y))
        }
        for (my in Settings.MIN_Y..Settings.MAX_Y + 1) {
            if (my != this.y)
                move.add(Point(this.x, my))
        }
        for (m in move) {
            if (m.x == x && m.y == y) {
                if (!checkRookBan(m.x, m.y)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检查车和目标位置之间有没有被其他棋子挡住
     */
    private fun checkRookBan(targetX: Int, targetY: Int): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (LocationUtility.checkBetweenXY(man.x, man.y, x, y, targetX, targetY)) {
                return true
            }
        }

        for (man in game.getDifferentColorChesses(color)) {
            if (LocationUtility.checkBetweenXY(man.x, man.y, x, y, targetX, targetY)) {
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
            if (mx != this.x)
                move.add(Point(mx, this.y))
        }
        for (my in Settings.MIN_Y..Settings.MAX_Y + 1) {
            if (my != this.y)
                move.add(Point(this.x, my))
        }
        for (m in move) {
            if (checkInBoard(m.x, m.y) && !checkSameColorChessExists(m.x, m.y)) {
                if (!checkRookBan(m.x, m.y)) {
                    points.add(m)
                }
            }
        }
        return points
    }

}