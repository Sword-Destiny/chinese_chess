package com.yuanhao.chinesechess.utilities.recoder

import com.yuanhao.chinesechess.main.Game
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

/**
 * 保存文件工具类
 */
class Saver {

    companion object {

        const val TEMP_DATA = "data/tmp.dat"
        const val TEMP_TEXT = "data/tmp.txt"

        /**
         * 将对局保存到文件
         */
        fun saveGame(g: Game, data: String? = null, text: String? = null): String? {
            try {
                val uuid = UUID.randomUUID().toString()
                val txt = g.toString()
                val objFile = data ?: "data/$uuid.chess"
                val textFile = text ?: "data/$uuid.txt"
                ObjectOutputStream(FileOutputStream(File(objFile))).use { oos ->
                    oos.writeObject(g)
                    println("文件已保存: $objFile")
                }
                FileOutputStream(File(textFile)).use { fos ->
                    fos.write(txt.toByteArray(Charset.forName("UTF-8")))
                    println("文件已保存: $textFile")
                }
                return objFile
            } catch (e: Exception) {
                println("保存出错: " + e.message)
            }
            return null
        }

        /**
         * 加载游戏
         */
        fun loadGame(file: String): Game? {
            try {
                ObjectInputStream(FileInputStream(File(file))).use { ois ->
                    return ois.readObject() as Game
                }
            } catch (e: Exception) {
                println("加载出错: " + e.message)
            }
            return null
        }

        /**
         * 删除临时文件
         */
        fun deleteTempFile() {
            try {
                val dataFile = File(TEMP_DATA)
                if (dataFile.exists()) {
                    println("文件已删除: $TEMP_DATA")
                    dataFile.delete()
                }
                val textFile = File(TEMP_TEXT)
                if (textFile.exists()) {
                    println("文件已删除: $TEMP_TEXT")
                    textFile.delete()
                }

            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

}