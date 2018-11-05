package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.main.Game
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

class Saver {

    companion object {
        fun saveGame(g: Game) {
            try {
                val uuid = UUID.randomUUID().toString()
                val text = g.toString()
                val objFile = "data/$uuid.chess"
                val textFile = "data/$uuid.txt"
                ObjectOutputStream(FileOutputStream(File(objFile))).use { oos ->
                    oos.writeObject(g)
                    println("文件已保存: $objFile")
                }
                FileOutputStream(File(textFile)).use { fos ->
                    fos.write(text.toByteArray(Charset.forName("UTF-8")))
                    println("文件已保存: $textFile")
                }
            } catch (e: Exception) {
                println("保存出错:" + e.message)
            }
        }
    }

}