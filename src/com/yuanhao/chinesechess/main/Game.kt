package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.ai.AI
import com.yuanhao.chinesechess.settings.FirstStep
import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility
import com.yuanhao.chinesechess.utilities.recoder.Recorder
import com.yuanhao.chinesechess.utilities.recoder.Saver
import com.yuanhao.chinesechess.utilities.recoder.Step
import java.awt.Point

import java.io.Serializable
import java.util.ArrayList

/**
 * 游戏
 * 默认红方在下
 * TODO 和棋判断
 */
class Game @JvmOverloads constructor(val settings: Settings = Settings()) : Serializable {

    val redAliveChesses: ArrayList<ChessMan> = ArrayList()//红方剩余棋子
    val blackAliveChesses: ArrayList<ChessMan> = ArrayList()//黑方剩余棋子
    val redDeadChesses: ArrayList<ChessMan> = ArrayList()//红方阵亡棋子
    val blackDeadChesses: ArrayList<ChessMan> = ArrayList()//黑方阵亡棋子

    private val kr = King(this, ChessColor.RED)//红帅
    private val kb = King(this, ChessColor.BLACK)//黑将
    private val qrl = Queen(this, ChessColor.RED, true)//红士左
    private val qrr = Queen(this, ChessColor.RED, false)//红士右
    private val qbl = Queen(this, ChessColor.BLACK, true)//黑士左
    private val qbr = Queen(this, ChessColor.BLACK, false)//黑士右
    private val brl = Bishop(this, ChessColor.RED, true)//红相左
    private val brr = Bishop(this, ChessColor.RED, false)//红相右
    private val bbl = Bishop(this, ChessColor.BLACK, true)//黑象左
    private val bbr = Bishop(this, ChessColor.BLACK, false)//黑象右
    private val krl = Knight(this, ChessColor.RED, true)//红马左
    private val krr = Knight(this, ChessColor.RED, false)//红马右
    private val kbl = Knight(this, ChessColor.BLACK, true)//黑马左
    private val kbr = Knight(this, ChessColor.BLACK, false)//黑马右
    private val rrl = Rook(this, ChessColor.RED, true)//红车左
    private val rrr = Rook(this, ChessColor.RED, false)//红车右
    private val rbl = Rook(this, ChessColor.BLACK, true)//黑车左
    private val rbr = Rook(this, ChessColor.BLACK, false)//黑车右
    private val crl = Cannon(this, ChessColor.RED, true)//红炮左
    private val crr = Cannon(this, ChessColor.RED, false)//红炮右
    private val cbl = Cannon(this, ChessColor.BLACK, true)//黑炮左
    private val cbr = Cannon(this, ChessColor.BLACK, false)//黑炮右
    private val pr0 = Pawn(this, ChessColor.RED, 0)//红兵0
    private val pr1 = Pawn(this, ChessColor.RED, 1)//红兵1
    private val pr2 = Pawn(this, ChessColor.RED, 2)//红兵2
    private val pr3 = Pawn(this, ChessColor.RED, 3)//红兵3
    private val pr4 = Pawn(this, ChessColor.RED, 4)//红兵4
    private val pb0 = Pawn(this, ChessColor.BLACK, 0)//黑卒0
    private val pb1 = Pawn(this, ChessColor.BLACK, 1)//黑卒1
    private val pb2 = Pawn(this, ChessColor.BLACK, 2)//黑卒2
    private val pb3 = Pawn(this, ChessColor.BLACK, 3)//黑卒3
    private val pb4 = Pawn(this, ChessColor.BLACK, 4)//黑卒4

    internal val recorder: Recorder // 记录器
    internal var status: GameStatus // 当前状态
    private var winner: ChessColor? = null // 胜者
    internal var userGo: Boolean // 是否轮到用户走棋
    var redScore = 0.0 // 红方棋面得分
    var blackScore = 0.0 // 黑方棋面得分
    val ai = AI() // AI

    init {
        status = GameStatus.PREPARE
        addChesses()
        init()
        recorder = Recorder(numberMatrix(), this)
        userGo = FirstStep.USER == settings.firstStep
    }

    /**
     * 添加棋子
     */
    private fun addChesses() {
        redAliveChesses.clear()
        blackAliveChesses.clear()
        redDeadChesses.clear()
        blackDeadChesses.clear()

        redAliveChesses.add(kr)
        blackAliveChesses.add(kb)

        redAliveChesses.add(qrl)
        redAliveChesses.add(qrr)
        blackAliveChesses.add(qbl)
        blackAliveChesses.add(qbr)

        redAliveChesses.add(brl)
        redAliveChesses.add(brr)
        blackAliveChesses.add(bbl)
        blackAliveChesses.add(bbr)


        redAliveChesses.add(krl)
        redAliveChesses.add(krr)
        blackAliveChesses.add(kbl)
        blackAliveChesses.add(kbr)

        redAliveChesses.add(rrl)
        redAliveChesses.add(rrr)
        blackAliveChesses.add(rbl)
        blackAliveChesses.add(rbr)

        redAliveChesses.add(crl)
        redAliveChesses.add(crr)
        blackAliveChesses.add(cbl)
        blackAliveChesses.add(cbr)

        redAliveChesses.add(pr0)
        redAliveChesses.add(pr1)
        redAliveChesses.add(pr2)
        redAliveChesses.add(pr3)
        redAliveChesses.add(pr4)
        blackAliveChesses.add(pb0)
        blackAliveChesses.add(pb1)
        blackAliveChesses.add(pb2)
        blackAliveChesses.add(pb3)
        blackAliveChesses.add(pb4)
    }

    /**
     * 检查将帅冲突
     */
    fun checkKingConflict(x: Int, y: Int, c: ChessColor): Boolean {
        var r: King? = null
        for (man in redAliveChesses) {
            if (man is King) {
                r = man
                break
            }
        }
        if (r == null) {
            return false
        }
        if (r.x == x && r.y == y && c == ChessColor.BLACK) {
            return false
        }
        var b: King? = null
        for (man in blackAliveChesses) {
            if (man is King) {
                b = man
                break
            }
        }
        if (b == null) {
            return false
        }
        if (b.x == x && b.y == y && c == ChessColor.RED) {
            return false
        }
        if (r.x != b.x) {
            return false
        }

        for (man in redAliveChesses) {
            if (man !is King) {
                if (LocationUtility.checkBetweenY(man.x, man.y, r.x, r.y, b.x, b.y)) {
                    return false
                }
            }
        }
        for (man in blackAliveChesses) {
            if (man !is King) {
                if (LocationUtility.checkBetweenY(man.x, man.y, r.x, r.y, b.x, b.y)) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * 记录操作
     */
    fun recode(s: Step) {
        recorder.record(s)
//        println("${s.info}, red:${s.redScore}, black:${s.blackScore}")
    }

    /**
     * 开始游戏
     */
    fun start() {
        status = GameStatus.STARTED
        recorder.clear()
    }

    /**
     * 游戏结束
     */
    fun end(winnerColor: ChessColor) {
        winner = winnerColor
        status = GameStatus.ENDED
        Saver.saveGame(this)
    }

    /**
     * 初始化游戏
     */
    private fun init() {
        for (man in redAliveChesses) {
            man.setInitialLocation()
        }
        for (man in blackAliveChesses) {
            man.setInitialLocation()
        }
    }

    /**
     * 返回一个用于分析的数字矩阵
     */
    private fun numberMatrix(): Array<Array<Int>> {
        val m = Array(Settings.MAX_X + 1) { Array(Settings.MAX_Y + 1) { 0 } }
        for (man in redAliveChesses) {
            m[man.x][man.y] = man.matrixNumber()
        }
        for (man in blackAliveChesses) {
            m[man.x][man.y] = man.matrixNumber()
        }
        return m
    }

    /**
     * 剩余的所有己方棋子
     */
    fun getSameColorChesses(color: ChessColor): ArrayList<ChessMan> {
        return if (color === ChessColor.RED) {
            redAliveChesses
        } else {
            blackAliveChesses
        }
    }

    /**
     * 剩余的所有对方棋子
     */
    fun getDifferentColorChesses(color: ChessColor): ArrayList<ChessMan> {
        return if (color === ChessColor.BLACK) {
            redAliveChesses
        } else {
            blackAliveChesses
        }
    }

    /**
     * 敌方已死亡的棋子
     */
    fun getDifferentDeadChesses(color: ChessColor): ArrayList<ChessMan> {
        return if (color == ChessColor.BLACK) {
            redDeadChesses
        } else {
            blackDeadChesses
        }
    }

    /**
     * 检查将帅是否被将军
     */
    fun checkKingWillDie(c: ChessColor): Boolean {
        var king: King? = null
        for (man in getSameColorChesses(c)) {
            if (man is King) {
                king = man
                break
            }
        }
        if (king == null) {
            return false
        }
        for (man in getDifferentColorChesses(c)) {
            if (man.canGo(king.x, king.y)) {
                return true
            }
        }
        return false
    }

    /**
     * 检查游戏是否分出胜负
     */
    fun checkGameOver(c: ChessColor): Boolean {
        for (man in getSameColorChesses(c)) {
            val locations = man.listAllLocationsCanGo()
            val loc = Point(man.x, man.y)
            for (l in locations) {
                man.setLocation(l.x, l.y)
                val m = getDifferentExistsChess(l.x, l.y, c)
                m?.die()
                if (!checkKingWillDie(c) && !checkKingConflict(l.x, l.y, c)) {
                    man.setLocation(loc.x, loc.y)
                    m?.alive()
                    return false
                }
                m?.alive()
            }
            man.setLocation(loc.x, loc.y)
        }
        return true
    }

    /**
     * 返回敌方在(x,y)的棋子,没有则返回null
     */
    fun getDifferentExistsChess(x: Int, y: Int, c: ChessColor): ChessMan? {
        for (man in getDifferentColorChesses(c)) {
            if (man.x == x && man.y == y) {
                return man
            }
        }
        return null
    }

    override fun toString(): String {
        var s = "中国象棋游戏记录:\n" + "最终游戏状态:" + status.name + "\n"
        if (status == GameStatus.ENDED) {
            s += "胜利者: " + (if (winner == ChessColor.RED) "红" else "黑") + "方\n"
        }
        s += "\n"
        s += settings.toString()
        s += "\n"
        s += recorder.toString()
        s += "\n"
        return s
    }

    /**
     * 返回当前走棋颜色
     */
    fun getGoColor(): ChessColor {
        return if (userGo) settings.userColor else settings.computerColor
    }

}
