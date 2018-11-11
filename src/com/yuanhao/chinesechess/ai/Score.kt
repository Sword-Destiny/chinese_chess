package com.yuanhao.chinesechess.ai

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.Game

/**
 * 计分系统
 */
class Score {
    companion object {

        /**
         * 基础分数
         * 每个棋子的基础分数都不一样,一般以一个兵的基础价值为100,将帅的基础价值为10000
         */
        const val BASIC_SCORE = 100.0

        /**
         * 威胁性因子
         * TODO 当一个棋子可以走到对方棋子的位置的时候,就产生威胁性,威胁性要考虑对方的棋子被保护的情况,目前还没有考虑
         * 当然即使棋子处于被保护的状态,依然是可以威胁的,威胁不等于就要吃掉
         */
        const val THREAT_RATE = 0.2

        /**
         * 安全性因子
         * 如果一个小兵孤军深入,威胁并不大,但是后面如果有车马炮保护,就有巨大威胁
         * 棋子越安全得分越高,将帅除外,将帅的安全判定方式不一样,
         * TODO 将帅的安全一般情况下可以看对方在自己这边的兵力和自己的防御兵力的对比,但是目前这个不好判断,我没有处理,只考虑将军的情况
         */
        const val SAFETY_RATE = 0.1

        /**
         * 获胜局面
         */
        const val WIN = 1000000.0

        /**
         * 吃子得分因子
         * 产生吃子应该有一定的加分
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
         * 计算当前棋面得分
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