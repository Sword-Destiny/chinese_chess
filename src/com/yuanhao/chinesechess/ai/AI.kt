package com.yuanhao.chinesechess.ai

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.utilities.recoder.Saver
import com.yuanhao.chinesechess.utilities.recoder.Step

/**
 * AI 分析,使用打分机制,结合搜索,求最优局面,初始目标大概分析10步左右
 */
class AI {
    companion object {

        val predictSteps = 5 // 预测的步数

        /**
         * 棋风侵略性
         * 1代表平衡,大于1代表以削减对方棋面分数为主,也就是以进攻为主,小于1即代表保守
         */
        var computerAggressive = 0.98

        /**
         * 用户棋风侵略性
         * 通过走棋历史来侦测用户的棋风是激进还是保守
         */
        var userAggressive = 1.0

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
    }
}