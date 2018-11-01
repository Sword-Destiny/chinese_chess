package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.recoder.Step

import java.awt.*
import java.io.Serializable
import java.util.ArrayList

/**
 * 棋子
 */
abstract class ChessMan internal constructor(val game: Game, val color: ChessColor/*红，黑*/, val name: String) : Serializable {
    private var isAlive: Boolean = false
    private var isSelected: Boolean = false //棋子是否被选中
    val location: Point//棋子位置

    init {
        isAlive = true
        isSelected = false
        location = Point()
    }

    internal fun setLocation(x: Int, y: Int) {
        location.setLocation(x, y)
    }

    /**
     * 下一步能不能去某一个位置,
     * 这个函数不检查将帅碰面或者自杀的情况，这些情况由子类的moveTo函数检查
     */
    open fun canGo(x: Int, y: Int): Boolean {
        return if (checkSameColorChessExists(x, y)) {
            false
        } else checkInBoard(x, y)
    }

    /**
     * 检查是否在棋盘上
     */
    internal fun checkInBoard(x: Int, y: Int): Boolean {
        return x >= Settings.MIN_X && y >= Settings.MIN_Y && x <= Settings.MAX_X && y <= Settings.MAX_Y
    }

    /**
     * 检查某个位置是否有己方棋子
     */
    internal fun checkSameColorChessExists(x: Int, y: Int): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (man.location.getX() == x.toDouble() && man.location.y == y) {
                // 此位置上已有己方棋子
                return true
            }
        }
        return false
    }

    /**
     * 先假装移动到新位置
     * 检查将帅照面
     */
    internal fun checkCommanderConflict(x: Int, y: Int): Boolean {
        val p = Point(location.x, location.y)
        setLocation(x, y)
        val conflict = game.checkCommanderConflict()
        setLocation(p.x, p.y)
        return conflict
    }

    /**
     * 移动到某个新位置,执行这个函数前需要做一系列的检查，比如canGo检查，将帅冲突检查等
     * 子类的实现应该在最后调用父类函数
     */
    @Throws(Exception::class)
    open fun moveTo(x: Int, y: Int) {
        for (man in game.getDifferentColorChesses(color)) {
            if (man.location.x == x && man.location.y == y) {
                man.die()
                break
            }
        }
        val s = Step(Point(location.x, location.y), Point(x, y), name)
        game.recode(s)
        setLocation(x, y)
    }

    internal abstract fun setInitialLocation()

    /**
     * 列出下一步能走的所有位置
     */
    abstract fun listAllLocationsCanGo(): ArrayList<Point>

    /**
     * 棋子被吃掉
     */
    private fun die() {
        isAlive = false
        if (color === ChessColor.red) {
            game.redAliveChesses.remove(this)
            game.redDeadChesses.add(this)
        } else {
            game.blackAliveChesses.remove(this)
            game.blackDeadChesses.add(this)
        }
    }

}

