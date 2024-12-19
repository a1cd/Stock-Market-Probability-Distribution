package edu.wpi.a1cd

import edu.wpi.a1cd.edu.wpi.a1cd.Investment
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

fun main() {
    val file = File("StockData.dat")
    println(file.length())
    var count: Int
    var all: ArrayList<Investment>
    FileInputStream(file).use { fis ->
        BufferedInputStream(fis).use {bis ->
//        ZipInputStream(fileInputStream).use { zipInputStream ->
//            zipInputStream.nextEntry
            ObjectInputStream(bis).use { objectInputStream ->
                count = objectInputStream.readLong().toInt()
                all = ArrayList<Investment>(count)
                for (i in 0 until count - 4) {
                    val inv = Investment(objectInputStream)
                    all.add(inv)
                }
            }
//        }
        }
    }
    println(all)
}