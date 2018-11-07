package com.yuanhao.chinesechess.ai

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.Game

class Score {
    companion object {

        /**
         * 基础分数
         */
        const val BASIC_SCORE = 100.0

        /**
         * 威胁性因子
         */
        const val THREAT_RATE = 0.01

        /**
         * 安全性因子
         */
        const val SAFETY_RATE = 0.1

        /**
         * 获胜局面
         */
        const val WIN = 1000000.0

        /**
         * 吃子得分因子
         */
        const val EAT_FACTOR = 0.5

        /**
         * 计算所有棋子的分数
         */
        fun countChessScores(game:Game) {

            for (man in game.redAliveChesses){
                man.countStaticScore()
            }

            for (man in game.blackAliveChesses){
                man.countStaticScore()
            }

            for (man in game.redAliveChesses){
                man.countScore()
            }

            for (man in game.blackAliveChesses){
                man.countScore()
            }

            countBasicScore(game)
        }

        /**
         * 计算当前红方棋面得分
         */
        private fun countBasicScore(game: Game) {
            game.redScore = 0.0
            game.blackScore = 0.0
            val c = game.getGoColor()
            if (c == ChessColor.BLACK) {
                if (game.checkKingWillDie(ChessColor.BLACK)) {
                    // 黑方死局
                    game.redScore = WIN
                    game.blackScore = 0.0
                    return
                } else {
                    if(game.recorder.steps.isNotEmpty()) {
                        game.redScore += game.recorder.lastStep().eatScore
                    }
                }
            } else {
                if (game.checkKingWillDie(ChessColor.RED)) {
                    // 红方死局
                    game.redScore = 0.0
                    game.blackScore = WIN
                    return
                } else {
                    if(game.recorder.steps.isNotEmpty()) {
                        game.blackScore += game.recorder.lastStep().eatScore
                    }
                }
            }
            var score = 0.0
            for (man in game.redAliveChesses) {
                score += man.score
            }
            game.redScore += score
            score = 0.0
            for (man in game.blackAliveChesses) {
                score += man.score
            }
            game.blackScore += score
        }

    }
}