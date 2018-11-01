package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.settings.Settings

import java.io.Serializable
import java.util.ArrayList

/**
 * 游戏
 * 默认红方在下
 */
class Game @JvmOverloads constructor(val settings: Settings = Settings()) : Serializable {

    val redAliveChesses: ArrayList<ChessMan> = ArrayList()//红方剩余棋子
    val blackAliveChesses: ArrayList<ChessMan> = ArrayList()//黑方剩余棋子
    val redDeadChesses: ArrayList<ChessMan> = ArrayList()//红方阵亡棋子
    val blackDeadChesses: ArrayList<ChessMan> = ArrayList()//黑方阵亡棋子

    private val cr = Commander(this, ChessColor.red)//红帅
    private val cb = Commander(this, ChessColor.black)//黑将
    private val qrl = Queen(this, ChessColor.red, true)//红士左
    private val qrr = Queen(this, ChessColor.red, false)//红士右
    private val qbl = Queen(this, ChessColor.black, true)//黑士左
    private val qbr = Queen(this, ChessColor.black, false)//黑士右

    init {

        redAliveChesses.add(cr)
        redAliveChesses.add(qrl)
        redAliveChesses.add(qrr)

        blackAliveChesses.add(cb)
        blackAliveChesses.add(qbl)
        blackAliveChesses.add(qbr)
    }

    fun checkCommanderConflict(): Boolean {
        var r: Commander? = null
        for (man in redAliveChesses) {
            if (man is Commander) {
                r = man
                break
            }
        }
        if (r == null) {
            return false
        }
        var b: Commander? = null
        for (man in blackAliveChesses) {
            if (man is Commander) {
                b = man
                break
            }
        }
        if (b == null) {
            return false
        }

        for (man in redAliveChesses) {
            if (man !is Commander) {
                if (ChessMan.checkMidY(man.location, r.location, b.location)) {
                    return false
                }
            }
        }
        for (man in blackAliveChesses) {
            if (man !is Commander) {
                if (ChessMan.checkMidY(man.location, r.location, b.location)) {
                    return false
                }
            }
        }

        return true
    }

    fun initGame() {
        for (man in redAliveChesses) {
            man.setInitialLocation()
        }
        for (man in blackAliveChesses) {
            man.setInitialLocation()
        }
    }

    /**
     * 剩余的所有己方棋子
     */
    fun getSameColorChesses(color: ChessColor): ArrayList<ChessMan> {
        return if (color === ChessColor.red) {
            redAliveChesses
        } else {
            blackAliveChesses
        }
    }

    /**
     * 剩余的所有对方棋子
     */
    fun getDifferentColorChesses(color: ChessColor): ArrayList<ChessMan> {
        return if (color === ChessColor.black) {
            redAliveChesses
        } else {
            blackAliveChesses
        }
    }
}
