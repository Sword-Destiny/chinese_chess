package com.yuanhao.chinesechess.ai

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.utilities.recoder.Saver
import com.yuanhao.chinesechess.utilities.recoder.Step
import java.io.Serializable

/**
 * AI 分析,使用打分机制,结合搜索,求最优局面,初始目标大概分析10步左右
 */
class AI(ps: Int = 5) : Serializable {

    private val predictSteps = ps // 预测的步数

    /**
     * 棋风侵略性
     * 1代表平衡,大于1代表以削减对方棋面分数为主,也就是以进攻为主,小于1即代表保守
     */
    private var computerAggressive = 0.98

    /**
     * 用户棋风侵略性
     * 通过走棋历史来侦测用户的棋风是激进还是保守
     */
    private var userAggressive = 1.0
    private var nStep = 0 // 总分析步数
    private var nConservative = 0 // 保守
    private var nRadical = 0 // 激进

    /**
     * 开始AI分析
     */
    fun startAnalysis(g: Game): Step? {
        val file = Saver.saveGame(g, Saver.TEMP_DATA, Saver.TEMP_TEXT)
        if (file != null) {
            val game = Saver.loadGame(file)
            if (game != null) {
                // 上面两步序列化和反序列化的操作其实就是为了复制game并且备份
                // 下面开始 AI 分析
                // TODO
                var i = 0
                while (i < 10000) {
                    Thread.sleep(1000)
                    //println("ai thread running $i")
                    i++
                }
                Saver.deleteTempFile()
                return null
            }
        }

        return null
    }

    /**
     * 学习用户的侵略性
     */
    fun learnUserAggressive(g: Game) {
        if (g.recorder.steps.size < 2) {
            return
        }
        val s = g.recorder.lastStep()
        val sp = g.recorder.steps[g.recorder.steps.size - 2]
        nStep++
        if (s.color == ChessColor.RED) {
            val meUp = s.redScore - sp.redScore
            val youUp = s.blackScore - sp.blackScore
            if (meUp >= 0.0 && youUp >= 0.0) {
                nConservative++
            }
            if (meUp < 0.0 && youUp < 0.0) {
                nRadical++
            }
        } else {
            val meUp = s.blackScore - sp.blackScore
            val youUp = s.redScore - sp.redScore
            if (meUp >= 0.0 && youUp >= 0.0) {
                nConservative++
            }
            if (meUp < 0.0 && youUp < 0.0) {
                nRadical++
            }
        }
        if (nConservative > 0 && nRadical > 0) {
            userAggressive = nRadical.toDouble() / nConservative.toDouble()
        }
        if (nStep >= predictSteps) {
            // 将电脑的激进程度设置为像用户接近,针尖对麦芒,人类容易犯错
            computerAggressive = (computerAggressive + userAggressive) / 2.0
        }
    }
}