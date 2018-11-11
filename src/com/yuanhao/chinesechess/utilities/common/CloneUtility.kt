package com.yuanhao.chinesechess.utilities.common

import java.io.*

object CloneUtility {

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> clone(obj: T): T? {
        var cloneObj: T? = null
        try {
            //写入字节流
            ByteArrayOutputStream().use { out ->
                ObjectOutputStream(out).use { obs ->
                    obs.writeObject(obj)
                    obs.close()
                    ByteArrayInputStream(out.toByteArray()).use { ios ->
                         ObjectInputStream(ios).use { ois ->
                             cloneObj = ois.readObject() as T
                         }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cloneObj
    }
}