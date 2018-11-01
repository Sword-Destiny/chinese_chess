package com.yuanhao.chinesechess.utilities.common

import java.awt.Point

class LocationUtility {
    companion object {
        /**
         * 检测某个棋子是不是在两个棋子中间(横向)
         */
        internal fun checkBetweenX(check: Point, p: Point, q: Point): Boolean {
            if (check.y == p.y && check.y == q.y) {
                if (check.x < p.x && check.x > q.x) {
                    return true
                }
                if (check.x > p.x && check.x < q.x) {
                    return true
                }
            }
            return false
        }

        /**
         * 检测某个棋子是不是在两个棋子中间(纵向)
         */
        internal fun checkBetweenY(check: Point, p: Point, q: Point): Boolean {
            if (check.x == p.x && check.x == q.x) {
                if (check.y < p.y && check.y > q.y) {
                    return true
                }
                if (check.y > p.y && check.y < q.y) {
                    return true
                }
            }
            return false
        }

        internal fun checkBetweenXY(check: Point, p: Point, q: Point): Boolean {
            return checkBetweenY(check, p, q) || checkBetweenX(check, p, q)
        }

        /**
         * 检查棋子是否在两个棋子中间
         */
        internal fun checkBetween2D(check: Point, p: Point, q: Point): Boolean {
            if (check.x < p.x && check.x > q.x || check.x > p.x && check.x < q.x) {
                if (check.y < p.y && check.y > q.y || check.y > p.y && check.y < q.y) {
                    return true
                }
            }
            return false
        }

        /**
         * 检测日字，马脚
         */
        internal fun checkBetweenT2O1(check: Point, source: Point, target: Point): Boolean {
            if (Math.abs(check.x - source.x) == 1 && check.y == source.y || Math.abs(check.y - source.y) == 1 && check.x == source.x) {
                if (Math.abs(check.x - target.x) == 1 && Math.abs(check.y - target.y) == 1) {
                    return true
                }
            }
            return false
        }
    }
}

