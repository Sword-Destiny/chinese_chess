package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.ai.Score
import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import com.yuanhao.chinesechess.utilities.recoder.Step

import java.awt.*
import java.io.Serializable
import java.util.ArrayList

/**
 * 棋子
 */
abstract class ChessMan internal constructor(val game: Game, val color: ChessColor/*红，黑*/, val name: String, bs: Double) : Serializable {
    private var isAlive: Boolean = false
    var isSelected: Boolean = false // 棋子是否被选中
    val location: Point // 棋子位置
    var lastGo: Boolean // 最后一个移动的棋子
    val basicScore = bs // 基本子力
    var score = 0.0 // 棋子当前子力
    var staticScore = 0.0 // 静态得分
    var locationScore = 0.0 // 位置得分
    var safetyScore = 0.0 // 安全性打分
    var flexibilityScore = 0.0 // 灵活性打分
    private var threatScore = 0.0 // 威胁性打分

    init {
        lastGo = false
        isAlive = true
        isSelected = false
        location = Point(0, 0)
    }

    /**
     * 设置棋子位置
     */
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
     * 检查目标位置是否在棋盘上
     */
    internal fun checkInBoard(x: Int, y: Int): Boolean {
        return x >= Settings.MIN_X && y >= Settings.MIN_Y && x <= Settings.MAX_X && y <= Settings.MAX_Y
    }

    /**
     * 检查某个位置是否有己方棋子
     */
    internal fun checkSameColorChessExists(x: Int, y: Int): Boolean {
        for (man in game.getSameColorChesses(color)) {
            if (man.location.x == x && man.location.y == y) {
                // 此位置上已有己方棋子
                return true
            }
        }
        return false
    }

    /**
     * 检查某个位置是否有己方棋子
     */
    internal fun checkDifferentColorChessExists(x: Int, y: Int): Boolean {
        for (man in game.getDifferentColorChesses(color)) {
            if (man.location.x == x && man.location.y == y) {
                // 此位置上已有己方棋子
                return true
            }
        }
        return false
    }

    /**
     * 先试探移动到新位置
     * 检查将帅照面
     * 然后再移动回原位置
     */
    internal fun checkKingConflict(x: Int, y: Int): Boolean {
        val p = Point(location.x, location.y)
        setLocation(x, y)
        val man = game.getDifferentExistsChess(x, y, color)
        man?.die()
        val conflict = game.checkKingConflict(x, y, color)
        setLocation(p.x, p.y)
        man?.alive()
        return conflict
    }

    /**
     * 先试探移动到新位置
     * 检查己方将帅是否被将军
     * 然后再移动回原位置
     */
    internal fun checkKingWillDie(x: Int, y: Int): Boolean {
        val p = Point(location.x, location.y)
        setLocation(x, y)
        val man = game.getDifferentExistsChess(x, y, color)
        man?.die()
        val willDie = game.checkKingWillDie(color)
        setLocation(p.x, p.y)
        man?.alive()
        return willDie
    }

    /**
     * 计算两个棋子中间棋子的数量
     */
    internal fun countMidChessNum(target: Point): Int {
        var res = 0
        for (man in game.getSameColorChesses(color)) {
            if (LocationUtility.checkBetweenXY(man.location, location, target)) {
                res++
            }
        }

        for (man in game.getDifferentColorChesses(color)) {
            if (LocationUtility.checkBetweenXY(man.location, location, target)) {
                res++
            }
        }

        return res
    }

    /**
     * 移动到某个新位置
     * 执行这个函数前需要做一系列的检查，比如canGo检查，将帅冲突检查等
     * 子类的实现应该在最后调用父类函数
     */
    @Throws(Exception::class)
    open fun moveTo(x: Int, y: Int) {
        for (man in game.getDifferentColorChesses(color)) {
            if (man.lastGo) {
                man.lastGo = false
                break
            }
        }
        var eatScore = 0.0
        for (man in game.getDifferentColorChesses(color)) {
            if (man.location.x == x && man.location.y == y) {
                eatScore = man.score
                man.die()
                println("eat: ${man.name}")
                break
            }
        }
        val p = Point(location.x, location.y)
        setLocation(x, y)
        lastGo = true
        game.userGo = !game.userGo
        Score.countChessScores(game)
        val s = Step(p, Point(x, y), name, color, game.settings.userColor, game.redScore, game.blackScore, eatScore)
        game.recode(s)
        if (!game.userGo) {
            game.ai.learnUserAggressive(game)
        }
    }

    /**
     * 设置初始位置
     */
    internal abstract fun setInitialLocation()

    /**
     * 列出下一步能走的所有位置
     */
    internal abstract fun listAllLocationsCanGo(): ArrayList<Point>

    /**
     * 返回一个独一无二的整数,用于矩阵运算
     */
    internal abstract fun matrixNumber(): Int

    /**
     * 棋子被吃掉
     */
    internal fun die() {
        isAlive = false
        if (color === ChessColor.RED) {
            game.redAliveChesses.remove(this)
            game.redDeadChesses.add(this)
        } else {
            game.blackAliveChesses.remove(this)
            game.blackDeadChesses.add(this)
        }
    }

    /**
     * 棋子活过来(有时候需要判断棋面形式的时候,会假设一些步骤,所以有一些棋子会被假设吃掉)
     */
    internal fun alive() {
        isAlive = true
        if (color === ChessColor.RED) {
            game.redAliveChesses.add(this)
            game.redDeadChesses.remove(this)
        } else {
            game.blackAliveChesses.add(this)
            game.blackDeadChesses.remove(this)
        }
    }

    /**
     * 计算静态得分,不包含威胁得分
     */
    abstract fun countStaticScore()

    /**
     * 计算最终得分,包含威胁性得分
     * 此函数可以迭代
     */
    fun countScore() {
        threatScore = 0.0
        for (man in game.getDifferentColorChesses(color)) {
            if (canGo(man.location.x, man.location.y)) {
                threatScore += if(man is King){
                    Score.BASIC_SCORE
                }else {
                    man.staticScore * Score.THREAT_RATE
                }
            }
        }
        score = staticScore + threatScore
        staticScore = score
    }

}

