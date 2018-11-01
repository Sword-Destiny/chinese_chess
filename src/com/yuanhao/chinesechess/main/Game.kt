package com.yuanhao.chinesechess.main

import com.yuanhao.chinesechess.settings.Settings
import com.yuanhao.chinesechess.utilities.common.LocationUtility

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

    private val kr = King(this, ChessColor.red)//红帅
    private val kb = King(this, ChessColor.black)//黑将
    private val qrl = Queen(this, ChessColor.red, true)//红士左
    private val qrr = Queen(this, ChessColor.red, false)//红士右
    private val qbl = Queen(this, ChessColor.black, true)//黑士左
    private val qbr = Queen(this, ChessColor.black, false)//黑士右
    private val brl = Bishop(this, ChessColor.red, true)//红相左
    private val brr = Bishop(this, ChessColor.red, false)//红相右
    private val bbl = Bishop(this, ChessColor.black, true)//黑象左
    private val bbr = Bishop(this, ChessColor.black, false)//黑象右
    private val krl = Knight(this, ChessColor.red, true)//红马左
    private val krr = Knight(this, ChessColor.red, false)//红马右
    private val kbl = Knight(this, ChessColor.black, true)//黑马左
    private val kbr = Knight(this, ChessColor.black, false)//黑马右
    private val rrl = Rook(this, ChessColor.red, true)//红车左
    private val rrr = Rook(this, ChessColor.red, false)//红车右
    private val rbl = Rook(this, ChessColor.black, true)//黑车左
    private val rbr = Rook(this, ChessColor.black, false)//黑车右

    init {

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
    }

    fun checkCommanderConflict(): Boolean {
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

        for (man in redAliveChesses) {
            if (man !is King) {
                if (LocationUtility.checkBetweenY(man.location, r.location, b.location)) {
                    return false
                }
            }
        }
        for (man in blackAliveChesses) {
            if (man !is King) {
                if (LocationUtility.checkBetweenY(man.location, r.location, b.location)) {
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
