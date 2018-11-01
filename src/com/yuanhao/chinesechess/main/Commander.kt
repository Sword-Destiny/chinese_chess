package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.exceptions.CommanderConflictException
import com.yuanhao.chinesechess.exceptions.LocationErrorException

import java.awt.*
import java.util.ArrayList

/**
 * 将，帅
 */
class Commander internal constructor(g: Game, c: ChessColor) : ChessMan(g, c) {

    override fun canGo(x: Int, y: Int): Boolean {
        if (!super.canGo(x, y)) {
            // 棋盘外位置
            return false
        }
        if (x < 3 || y < 0 || x > 5 || y > 2) {
            //throw new LocationErrorException("移动位置违反规则!");
            return false
        }
        // 下一步能去的位置
        val move = arrayOf(intArrayOf(0, 1), intArrayOf(0, -1), intArrayOf(-1, 0), intArrayOf(1, 0))
        for (m in move) {
            if (x == location.x + m[0] && y == location.y + m[1]) {
                return true
            }
        }
        return false
    }

    override fun listAllLocationsCanGo(): ArrayList<Point> {
        val points = ArrayList<Point>()
        val move = arrayOf(intArrayOf(0, 1), intArrayOf(0, -1), intArrayOf(-1, 0), intArrayOf(1, 0))
        for (m in move) {
            val p = Point(location.x + m[0], location.y + m[1])
            if (checkInBoard(p.x, p.y) && !checkSameColorChessExists(p.x, p.y)) {
                points.add(p)
            }
        }
        return points
    }

    override fun setInitialLocation() {
        if (color === ChessColor.red) {
            setLocation(4, 0)
        } else {
            setLocation(4, 8)
        }
    }

    @Throws(Exception::class)
    override fun moveTo(x: Int, y: Int) {
        if (!canGo(x, y)) {
            return
        }
        if (checkCommanderConflict(x, y)) {
            throw CommanderConflictException("将帅不能照面")
        }
        if (checkWillDie(x, y)) {
            throw LocationErrorException("将帅移动到这个位置会被吃掉")
        }
        super.moveTo(x, y)
    }

    /**
     * 检查移动的新位置是否会死
     */
    private fun checkWillDie(x: Int, y: Int): Boolean {
        for (man in game.getDifferentColorChesses(color)) {
            if (man.canGo(x, y)) {
                return true
            }
        }
        return false
    }

}