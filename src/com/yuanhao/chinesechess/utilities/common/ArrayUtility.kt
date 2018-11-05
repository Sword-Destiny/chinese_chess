package com.yuanhao.chinesechess.utilities.common

/**
 * 数组输出工具类
 */
class ArrayUtility {

    companion object {
        fun <T> outputArray2D(a: Array<Array<T>>, len: Int): String {
            var s = ""
            var i = 0
            while (i < a.size - 1) {
                s += outputArray1D(a[i], len)
                s += "\n"
                i++
            }
            if (a.isNotEmpty()) {
                s += outputArray1D(a[i], len)
                s += "\n"
            }
            return s
        }

        private fun <T> outputArray1D(a: Array<T>, len: Int): String {
            val f = "%${len}d"
            var s = "["
            var i = 0
            while (i < a.size - 1) {
                s = s + f.format(a[i]) + ", "
                i++
            }
            if (a.isNotEmpty()) {
                s += a[i]
            }
            s += "]"
            return s
        }
    }
}