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
 * TODO:考虑对手的策略
 * NOTICE: 静态局面的打分才是有意义的,动态局面的打分应该是后续静态局面打分的加权平均,
 * 越有可能的局面权重越大,其中哪种局面可能性比较大呢,那要看对方的局面判断了,
 * 因此对手的策略很重要,对手下棋也是要冲着获胜去的,不是每种情况概率都一样.
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
        private const val MAX_STEPS = 200 // 为了性能考虑,只保留200种情况,注意,这个数还是很大,在两步发散后,可能就会变成100000种
    }

    /**
     * 开始AI分析
     */
    fun startAnalysis(g: Game): Step? {
        // 将序列化为临时文件改为内存中的字节流序列化,也可以达到复制的目的
        val game = CloneUtility.clone(g)
        if (game != null) {
            val goColor = if (game.userGo) game.settings.userColor else game.settings.computerColor
            bfs(game, goColor, predictSteps)
            val step = decideStep()
            step1s.clear()
            stepNs.clear()
            return step
        }
        return null
    }

    /**
     * 用于统计局势情况,往局势优的方向走棋
     */
    private val step1s = HashMap<Step, com.yuanhao.chinesechess.utilities.common.Pair<Int, Double>>()
    /**
     * 按照棋面价值进行排序,保留最好的MAX_STEP个情况
     */
    private var stepNs = ArrayList<Pair<Step, ArrayList<Step>>>()

    /**
     * 最终决定走哪一步棋
     * TODO 判断一步棋的分数,还要看下一步棋可能的情况,情况越少的,权重越高
     */
    private fun decideStep(): Step? {
        for (pair in stepNs) {
            step1s[pair.first]!!.first++
            step1s[pair.first]!!.second += pair.second.last().up
        }
        val finalScore = ArrayList<Pair<Step, Double>>()
        step1s.forEach { k, v -> if (v.first > 0) finalScore.add(Pair(k, v.second / v.first.toDouble())) }
        finalScore.sortWith(Comparator { s1, s2 -> return@Comparator if (s1.second > s2.second) -1 else if (s1.second < s2.second) 1 else 0 })
        if (finalScore.size > 0) {
            if (finalScore.size > randomSize) {
                if (finalScore[0].first.getSameColorScore(finalScore[0].first.chess.color) >= Score.WIN) {
                    // 胜局不随机
                    return finalScore[0].first
                }
                // 其他情况适当随机一下
                val r = random.nextInt(randomSize)
                return finalScore[r].first
            } else {
                return finalScore[0].first
            }
        } else {
            return null
        }
    }

    /**
     * 宽度优先搜索
     */
    private fun bfs(g: Game, c: ChessColor, ps: Int) {
        val last = if (g.recorder.steps.isNotEmpty()) g.recorder.steps.last() else null
        val steps = listAllLocationCanGo(g, c)
        for (s in steps) {
            computeUp(s, last, g, c)
        }
        for (s in steps) {
            step1s[s] = com.yuanhao.chinesechess.utilities.common.Pair(0, 0.0)
        }
        for (s in steps) {
            stepNs.add(Pair(s, ArrayList()))
        }
        var nStep = ps
        var cc = c

        while (nStep > 0) {
            var tmp = ArrayList<Pair<Step, ArrayList<Step>>>()
            analysisAStep(tmp, g, c, cc, last) // 分析己方一步
            stepNs.clear()
            stepNs = tmp
            cc = if (cc == ChessColor.RED) ChessColor.BLACK else ChessColor.RED
            tmp = ArrayList()
            analysisAStep(tmp, g, c, cc, last) // 分析对方一步
            stepNs.clear()
            stepNs = tmp
            cc = if (cc == ChessColor.RED) ChessColor.BLACK else ChessColor.RED
            // 通过局势变化增量来进行剪枝
            if (stepNs.size > MAX_STEPS) {
                sortByUp(stepNs)
                stepNs.subList(MAX_STEPS, stepNs.size).clear()
            }
            nStep--
        }

    }

    /**
     * 分析一步
     * c:电脑颜色
     * cc:当前走棋颜色
     */
    private fun analysisAStep(tmp: ArrayList<Pair<Step, ArrayList<Step>>>, g: Game, c: ChessColor, cc: ChessColor, last: Step?) {
        for (pair in stepNs) {
            for (s in pair.second) {
                g.recorder.applyStep(s)
            }
            val next = listAllLocationCanGo(g, cc)
            if (c != cc) {
                /**
                 * TODO:对手的走棋不是随便的,选择最有可能的3-5步棋
                 */
            }
            if (next.size == 0) {
                if (c != cc) {
                    /**
                     * TODO 胜局
                     */
                } else {
                    /**
                     * TODO 死局
                     */
                }
            }
            for (s in next) {
                computeUp(s, last, g, c)
                val list = ArrayList<Step>()
                list.addAll(pair.second)
                list.add(s)
                tmp.add(Pair(pair.first, list))
            }
            for (s in pair.second) {
                g.recorder.cancel()
            }
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
        Score.countChessScores(g)
        val s = Step(p.x, p.y, x, y, chess, g.redScore, g.blackScore, eatScore)
        g.recorder.steps.add(s)
    }

    /**
     * 通过局势变化增量来排序
     */
    private fun sortByUp(steps: ArrayList<Pair<Step, ArrayList<Step>>>) {
        steps.sortWith(Comparator { o1, o2 ->
            val last1 = o1.second.last()
            val last2 = o2.second.last()
            if (last1.differentKingWillDie || last2.differentKingWillDie) {
                when {
                    last1.up > last2.up -> return@Comparator -1
                    last1.up < last2.up -> return@Comparator 1
                    else -> return@Comparator 0
                }
            }
            if (last1.isStaticStep() && !last2.isStaticStep()) {
                return@Comparator 1
            }
            if (!last1.isStaticStep() && last2.isStaticStep()) {
                return@Comparator -1
            }
            when {
                last1.up > last2.up -> return@Comparator -1
                last1.up < last2.up -> return@Comparator 1
                else -> return@Comparator 0
            }
        })
    }

    /**
     * 计算局势变化增量
     */
    private fun computeUp(step: Step, preStep: Step?, g: Game, c: ChessColor): Double {
        var meUp = step.getSameColorScore(c) - (preStep?.getSameColorScore(c) ?: 0.0)
        val youUp = step.getDifferentColorScore(c) - (preStep?.getDifferentColorScore(c)
                ?: 0.0)
        if (step.chess.color == g.settings.computerColor) {
            if (meUp >= 0.0) {
                meUp *= computerAggressive
            } else {
                meUp /= computerAggressive
            }
        } else {
            if (meUp >= 0.0) {
                meUp *= userAggressive
            } else {
                meUp /= userAggressive
            }
        }
        step.up = meUp - youUp
        return step.up
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
            val r = if (nRadical > 0) nRadical else 1
            val c = if (nConservative > 0) nConservative else 1
            userAggressive = r.toDouble() / c.toDouble()
            // 将电脑的激进程度设置为逐渐向用户接近,对弈过程中,针尖对麦芒,人类更容易犯错
            computerAggressive = (computerAggressive + userAggressive) / 2.0
        }
    }

//    /**
//     * 有价值的棋着
//     */
//    private val betterSteps = ArrayList<Pair<Step, Step>>()
//    /**
//     * 静态棋着
//     */
//    private val staticSteps = ArrayList<Pair<Step, Step>>()
//    /**
//     * 所有棋着
//     */
//    private val allSteps = ArrayList<Pair<Step, Step>>()
//
//    /**
//     * 深度优先搜索函数
//     */
//    private fun searchStep(g: Game, c: ChessColor, ps: Int, preStep: Step?) {
//        if (ps <= 0) {
//            return
//        }
//        if (preStep == null) {
//            staticSteps.clear()
//            betterSteps.clear()
//            allSteps.clear()
//        }
//        if (g.checkKingWillDie(c)) {
//            return
//        }
//        val steps = listAllLocationCanGo(g, c)
//        findStaticSteps(steps, preStep)
//        findBetterSteps(g, steps, preStep)
//        for (step in steps) {
//            g.recorder.applyStep(step)
//            searchStep(g, if (c == ChessColor.RED) ChessColor.BLACK else ChessColor.RED, ps - 1, preStep ?: step)
//            g.recorder.cancel()
//        }
//    }
//
//    /**
//     * 寻找变稳定的局势
//     */
//    private fun findStaticSteps(steps: ArrayList<Step>, preStep: Step?) {
//        for (step in steps) {
//            val go = preStep ?: step
//            allSteps.add(Pair(go, step))
//            if (step.isStaticStep()) {
//                staticSteps.add(Pair(go, step))
//            }
//        }
//    }
//
//    /**
//     * 寻找变优的局势
//     */
//    private fun findBetterSteps(g: Game, steps: ArrayList<Step>, preStep: Step?) {
//        if (g.recorder.steps.isNotEmpty()) {
//            val last = g.recorder.lastStep()
//            for (step in steps) {
//                if (step.isStaticStep()) {
//                    if (preStep != null) {
//                        val up = computeUp(step, preStep, g)
//                        if (up > 0.0) {
//                            betterSteps.add(Pair(preStep, step))
//                        }
//                    } else {
//                        val up = computeUp(step, last, g)
//                        if (up > 0.0) {
//                            betterSteps.add(Pair(step, step))
//                        }
//                    }
//                }
//            }
//        }
//    }

}