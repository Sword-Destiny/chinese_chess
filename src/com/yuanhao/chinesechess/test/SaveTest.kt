package com.yuanhao.chinesechess.test

import com.yuanhao.chinesechess.main.Game
import com.yuanhao.chinesechess.settings.Settings
import java.io.*
import java.util.*

fun main(args: Array<String>) {
    try {
        val g = Game(Settings())
        g.startGame()
        ObjectOutputStream(FileOutputStream(File("data/save.dat"))).use { oos ->
            oos.writeObject(g)
        }
        ObjectInputStream(FileInputStream(File("data/save.dat"))).use { ois ->
            val s = ois.readObject() as Game
            println("Save Test 1: " + if (Arrays.deepEquals(g.recorder.initStatus, s.recorder.initStatus)) "PASSED" else "FAILED")
            println("Save Test 2: " + if (Arrays.deepEquals(g.recorder.finalStatus, s.recorder.finalStatus)) "PASSED" else "FAILED")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}