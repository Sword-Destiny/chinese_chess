package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.ai.Score
import com.yuanhao.chinesechess.exceptions.KingConflictException
import com.yuanhao.chinesechess.exceptions.KingWillDieException
import com.yuanhao.chinesechess.settings.Settings
import java.awt.Point
import java.util.ArrayList

/**
 * 炮
 */
class Cannon internal constructor(g: Game, c: ChessColor, private val left: Boolean) : ChessMan(g, c, "炮", 240.0) {

    override fun countStaticScore() {
        locationScore = 0.0
        if(location.x in 3..5){
            locationScore += 30
        }
        if(location.y in 7..9 && color == ChessColor.RED){
            locationScore += 30
        }
        if(location.y in 0..2 && color == ChessColor.BLACK){
            locationScore += 30
        }
        flexibilityScore = Score.BASIC_SCORE * listAllLocationsCanGo().size / 17.0
        safetyScore = 0.0
        for (man in game.getSameColorChesses(color)) {
            if (man != this) {
                if (man.canGo(location.x, location.y)) {
                    safetyScore += Score.SAFETY_RATE * (Score.BASIC_SCORE + locationScore)
                }
            }
        }
        staticScore = basicScore + locationScore + flexibilityScore + safetyScore
    }

    override fun matrixNumber(): Int =
            if (color == ChessColor.RED)
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
     * 检查炮有没有被ban,如果没有被ban,就可以走
     */
    private fun checkCannonBan(target: Point): Boolean {
        val differentColorExists = checkDifferentColorChessExists(target.x, target.y)
        val midChessNum = countMidChessNum(target)

        if (!differentColorExists && 0 == midChessNum) {
            // 中间没有棋子,也没用目标位置也没有棋子(有己方棋子的情况在canGo里面检查过了)
            return false
        }
        if (differentColorExists && 1 == midChessNum) {
            // 中间有一个棋子,目标位置有敌方棋子则可以走棋
            return false
        }
        return true
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