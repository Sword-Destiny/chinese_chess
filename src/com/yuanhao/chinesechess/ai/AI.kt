package com.yuanhao.chinesechess.ai

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.utilities.recoder.Saver
import com.yuanhao.chinesechess.utilities.recoder.Step

/**
 * AI 分析,使用打分机制,结合搜索,求最优局面,初始目标大概分析10步左右
 */
class AI {
    companion object {

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
                        println("ai thread running $i")
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