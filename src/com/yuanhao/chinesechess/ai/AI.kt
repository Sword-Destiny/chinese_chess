package com.yuanhao.chinesechess.ai

import com.yuanhao.chinesechess.main.ChessColor
import com.yuanhao.chinesechess.main.ChessMan
import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.utilities.common.CloneUtility
import com.yuanhao.chinesechess.utilities.recoder.Step
import java.awt.Point
import java.io.Serializable
import kotlin.random.Random

/**
 * AI 分析,使用打分机制,结合搜索,求最优局面,初始目标大概分析6步左右
 *
 * NOTICE: 考虑对手的策略
 *
 * NOTICE: 静态局面的打分才是有意义的,动态局面的打分应该是后续静态局面打分的加权平均,
 * 越有可能的局面权重越大,其中哪种局面可能性比较大呢,那要看对方的局面判断了,
 * 因此对手的策略很重要,对手下棋也是要冲着获胜去的,不是每种情况概率都一样.
 *
 * TODO: 静态局面的搜索深度还不够
 *
 * NOTICE: 如果遇到动态局面应该加深搜索层数,直到静态局面出现为止(需要考虑连续将军怎么处理,这个有可能是无穷的步数)
 *
 * TODO 加入将军历史表,如果遇到完全相同的将军,就不分析了,或者加入历史表,这样能解决反复走相同棋着的问题
 */
class AI(ps: Int = 3) : Serializable {

    private val predictSteps = ps // 预测的步数

    /**
     * 棋风侵略性
     * 1代表平衡,大于1代表以削减对方棋面分数为主,也就是以进攻为主,小于1即代表保守,保守就是更倾向于步步为营扩大自己优势
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
     * 在最好的三种情况中随机走棋
     */
    private var randomSize = 3

    companion object {
        private val random = Random(System.currentTimeMillis()) // 随机数
    }

    /**
     * 开始AI分析
     */
    fun startAnalysis(g: Game): Step? {
        // 将序列化为临时文件改为内存中的字节流序列化,也可以达到复制的目的
        val game = CloneUtility.clone(g)
        if (game != null) {
            val goColor = if (game.userGo) game.settings.userColor else game.settings.computerColor
            dfs(g, goColor)
            val step = decideStep(goColor)
            step1s.clear()
            return step
        }
        return null
    }

    /**
     * 用于统计局势情况,往局势优的方向走棋
     */
    private var step1s = ArrayList<Step>()

    /**
     * 最终决定走哪一步棋
     * 如果有胜局就不随机,否则就随机选择一个比较好的,当然还是分数越高概率越大
     */
    private fun decideStep(aiColor: ChessColor): Step? {
        sortByAverageUp(step1s, true)
        if (step1s.size < 1) {
            return null
        }
        if (step1s[0].aiAverageUp <= 0.0) {
            return step1s[0]
        }
        if (step1s[0].getSameColorScore(aiColor) < Score.WIN) {
            var s = 0.0
            val size = if (randomSize <= step1s.size) randomSize else step1s.size
            for (i in 0..(size - 1)) {
                s += step1s[i].aiAverageUp
            }
            var d = random.nextDouble(s)
            for (i in 0..(size - 1)) {
                d -= step1s[i].aiAverageUp
                if (d <= 0) {
                    return step1s[i]
                }
            }
        }
        return step1s[0]
    }

    private fun dfs(g: Game, aiColor: ChessColor) {
        val last = if (g.recorder.steps.isNotEmpty()) g.recorder.steps.last() else null
        step1s = listAllLocationCanGo(g, aiColor)
        for (s in step1s) {
            computeUp(s, last, g)
            // TODO 判断历史
            if (s.isStaticStep()) {
                s.aiAverageUp = if (s.chess.color == aiColor) s.myUp else s.yourUp
                s.userAverageUp = if (s.chess.color == aiColor) s.yourUp else s.myUp
            } else {
                g.recorder.applyStep(s)
                dfs(s, g, aiColor)
                g.recorder.cancel()
                computeAverageUp(s, aiColor)
            }
        }
    }

    private fun dfs(pre: Step, g: Game, aiColor: ChessColor) {
        pre.nextSteps = listAllLocationCanGo(g, if (pre.chess.color == ChessColor.RED) ChessColor.BLACK else ChessColor.RED)
        if (pre.nextSteps == null || pre.nextSteps!!.isEmpty()) {
            if (pre.chess.color == ChessColor.RED) {
                pre.redScore = Score.WIN
                pre.blackScore = 0.0
            } else {
                pre.redScore = 0.0
                pre.blackScore = Score.WIN
            }
            // NOTICE 胜局和死局
            pre.aiAverageUp = if (pre.chess.color == aiColor) pre.myUp else pre.yourUp
            pre.userAverageUp = if (pre.chess.color == aiColor) pre.yourUp else pre.myUp
        }
        for (s in pre.nextSteps!!) {
            computeUp(s, pre, g)
            // TODO 判断历史
            if (s.isStaticStep()) {
                s.aiAverageUp = if (s.chess.color == aiColor) s.myUp else s.yourUp
                s.userAverageUp = if (s.chess.color == aiColor) s.yourUp else s.myUp
            } else {
                g.recorder.applyStep(s)
                dfs(s, g, aiColor)
                g.recorder.cancel()
                computeAverageUp(s, aiColor)
            }
        }
    }

    /**
     * 这个地方是根据后续步骤的评分来决定当前步骤的评分,
     * 后续步骤的up越大,选择走这一步的概率越大,
     * 所以这里要分清楚这一步是自己走的还是对方走的,是AI走的还是用户走的.
     * 对手不傻,只有对手觉得比较好的走法他才会走.
     */
    private fun computeAverageUp(pre: Step, aiColor: ChessColor) {
        val next = pre.nextSteps!!
        if (next.size < 1) {
            System.err.println("不应该出现这个情况")
            return
        }

        if (pre.chess.color == aiColor) {
            // 前一步是ai走的,这一步是用户走的,所以只有userAverageUP大于0的用户才有可能会走,如果没有大于0的,那么说明这一步用户的情况很不妙
            sortByAverageUp(next, false)
            if (next[0].userAverageUp <= 0) {
                pre.aiAverageUp = next[0].aiAverageUp
                pre.userAverageUp = next[0].userAverageUp
                return
            }
            // 在所有用户可能走的棋着上求概率平均
            var userS = 0.0
            for (s in next) {
                if (s.userAverageUp > 0) {
                    userS += s.userAverageUp
                }
            }
            for (s in next) {
                if (s.userAverageUp > 0) {
                    pre.aiAverageUp += s.aiAverageUp * s.userAverageUp / userS
                    pre.userAverageUp += s.userAverageUp * s.userAverageUp / userS
                }
            }
            pre.aiAverageUp += pre.myUp / 3
            pre.userAverageUp += pre.yourUp / 3
        } else {
            sortByAverageUp(next, true)
            if (next[0].aiAverageUp <= 0) {
                pre.aiAverageUp = next[0].aiAverageUp
                pre.userAverageUp = next[0].userAverageUp
                return
            }
            var aiS = 0.0
            for (s in next) {
                if (s.aiAverageUp > 0) {
                    aiS += s.aiAverageUp
                }
            }
            for (s in next) {
                if (s.aiAverageUp > 0) {
                    pre.aiAverageUp += s.aiAverageUp * s.aiAverageUp / aiS
                    pre.userAverageUp += s.userAverageUp * s.aiAverageUp / aiS
                }
            }
            pre.aiAverageUp += pre.yourUp / 3
            pre.userAverageUp += pre.myUp / 3
        }
    }

    /**
     * 所有能走的位置
     */
    private fun listAllLocationCanGo(g: Game, c: ChessColor): ArrayList<Step> {
        val steps = ArrayList<Step>()
        for (man in g.getSameColorChesses(c)) {
            val locations = man.listAllLocationsCanGo()
            for (loc in locations) {
                if (man.checkKingWillDie(loc.x, loc.y) || man.checkKingConflict(loc.x, loc.y)) {
                    continue
                }
                val kwd = g.checkKingWillDie(if (man.color == ChessColor.RED) ChessColor.BLACK else ChessColor.RED)
                g.recorder.lastStep().differentKingWillDie = kwd
                moveChess(man, g, loc.x, loc.y)
                steps.add(g.recorder.lastStep())
                g.recorder.cancel()
            }
        }
        return steps
    }

    /**
     * ai移动棋子
     */
    private fun moveChess(chess: ChessMan, g: Game, x: Int, y: Int) {
        var eatScore = 0.0
        for (man in g.getDifferentColorChesses(chess.color)) {
            if (man.x == x && man.y == y) {
                eatScore = man.score * Score.THREAT_RATE
                man.die()
                break
            }
        }
        val p = Point(chess.x, chess.y)
        chess.setLocation(x, y)
        g.userGo = !g.userGo
        Score.countChessScores(g, true)
        val s = Step(p.x, p.y, x, y, chess, g.redScore, g.blackScore, eatScore)
        g.recorder.steps.add(s)
    }

    /**
     * 通过局势变化增量来排序
     */
    private fun sortByAverageUp(steps: ArrayList<Step>, ai: Boolean) {
        steps.sortWith(Comparator { o1, o2 ->
            if (ai) {
                when {
                    o1.aiAverageUp > o2.aiAverageUp -> return@Comparator -1
                    o1.aiAverageUp < o2.aiAverageUp -> return@Comparator 1
                    else -> return@Comparator 0
                }
            } else {
                when {
                    o1.userAverageUp > o2.userAverageUp -> return@Comparator -1
                    o1.userAverageUp < o2.userAverageUp -> return@Comparator 1
                    else -> return@Comparator 0
                }
            }
        })
    }

    /**
     * 计算局势变化增量
     */
    private fun computeUp(step: Step, preStep: Step?, g: Game) {
        val c = step.chess.color
        val meUp = step.getSameColorScore(c) - (preStep?.getSameColorScore(c) ?: 0.0)
        val youUp = step.getDifferentColorScore(c) - (preStep?.getDifferentColorScore(c)
                ?: 0.0)
        val myUp = if (c == g.settings.computerColor) {
            if (meUp >= 0.0) {
                meUp * computerAggressive
            } else {
                meUp / computerAggressive
            }
        } else {
            if (meUp >= 0.0) {
                meUp * userAggressive
            } else {
                meUp / userAggressive
            }
        }
        val yourUp = if (c == g.settings.computerColor) {
            if (youUp >= 0.0) {
                youUp * userAggressive
            } else {
                youUp / userAggressive
            }
        } else {
            if (youUp >= 0.0) {
                youUp * computerAggressive
            } else {
                youUp / computerAggressive
            }
        }
        step.myUp = myUp - youUp
        step.yourUp = yourUp - meUp
    }

    /**
     * 学习用户的棋风侵略性
     * NOTICE:在这个函数里面修改可以一定程度上改变电脑的风格
     */
    fun learnUserAggressive(g: Game) {
        if (g.recorder.steps.size < 2) {
            return
        }
        val s = g.recorder.lastStep()
        val sp = g.recorder.steps[g.recorder.steps.size - 2]
        nStep++
        val meUp = s.getSameColorScore(s.chess.color) - sp.getSameColorScore(s.chess.color)
        val youUp = s.getDifferentColorScore(s.chess.color) - sp.getDifferentColorScore(s.chess.color)
        if (meUp >= 0.0 && youUp >= 0.0) {
            nConservative++
            nStep++
        }
        if (meUp < 0.0 && youUp < 0.0) {
            nRadical++
            nStep++
        }
        if (nStep >= predictSteps * 2) {
            // val r = if (nRadical > 0) nRadical else 1
            // val c = if (nConservative > 0) nConservative else 1
            // userAggressive = r.toDouble() / c.toDouble()
            // 将电脑的激进程度设置为逐渐向用户接近,对弈过程中,针尖对麦芒,人类更容易犯错
            // computerAggressive = (computerAggressive + userAggressive) / 2.0
        }
    }

}