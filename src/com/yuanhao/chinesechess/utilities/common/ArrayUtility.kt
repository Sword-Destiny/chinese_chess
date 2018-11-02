package com.yuanhao.chinesechess.utilities.common

class ArrayUtility {

    companion object {
        fun <T> outputArray2D(a: Array<Array<T>>, len: Int) {
            var i = 0
            while (i < a.size - 1) {
                outputArray1D(a[i], len)
                println()
                i++
            }
            if (a.isNotEmpty()) {
                outputArray1D(a[i], len)
                println()
            }
        }

        fun <T> outputArray1D(a: Array<T>, len: Int) {
            val f = "%${len}d"
            print("[")
            var i = 0
            while (i < a.size - 1) {
                print(f.format(a[i]))
                print(", ")
                i++
            }
            if (a.isNotEmpty()) {
                print(a[i])
            }
            print("]")
        }
    }
}