package edu.wpi.a1cd.edu.wpi.a1cd

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


/**
 * Unpacks everything
 */
fun main() {
    val start = System.currentTimeMillis()
    val classloader = Thread.currentThread().contextClassLoader
    val uri = classloader.getResource("StockData.zip")!!
    val file = File(uri.toURI())
    val zipFile = ZipFile(file)
    val collector = Collectors.groupingBy(
        { it: ZipEntry ->
            return@groupingBy when (it.name.substringBefore("/", "")) {
                "etfs" -> FileType.ETF
                "stocks" -> FileType.stock
                else -> FileType.meta
            }
        }, Collectors.toList()
    )
    val zipEntriesGrouped = zipFile.stream()
        .filter { !it.isDirectory }
        .filter { it.name.endsWith(".csv") }
        .collect(collector)

    val symbolsZipEntry = zipEntriesGrouped[FileType.meta]?.firstOrNull()!!
    val stockZipEntries = zipEntriesGrouped[FileType.stock].orEmpty()
    val etfZipEntries = zipEntriesGrouped[FileType.ETF].orEmpty()
    val symbolToInvestmentMap = zipFile.getInputStream(symbolsZipEntry).bufferedReader().lines()
        .skip(1)
        .map {
            it.split(Regex("(?!\\B\"[^\"]*),(?![^\"]*\"\\B)"))
        }
        .map {
            var isNasdaqTraded: Boolean? = null
            if (it[0] == "N") isNasdaqTraded = false
            else if (it[0] == "Y") isNasdaqTraded = true

            val symbol: String = it[1]

            val securityName: String = it[2]

            val listingExchange: String = it[3]

            val marketCategory: MarketCategory? = MarketCategory.make(it[4])

            var isETF: Boolean? = null
            if (it[5] == "N") isETF = false
            else if (it[5] == "Y") isETF = true

            val roundLotSize: Int = it[6].toDouble().toInt()

            var testIssue: Boolean? = null
            if (it[7] == "N") testIssue = false
            else if (it[7] == "Y") testIssue = true

            val financialStatus: FinancialStatus? = FinancialStatus.make(it[8])

            val CQSSymbol: String = it[9]

            val NASDAQSymbol: String = it[10]

            var nextShares: Boolean? = null
            if (it[7] == "N") nextShares = false
            else if (it[7] == "Y") nextShares = true

            return@map Investment(
                isNasdaqTraded!!,
                symbol,
                securityName,
                listingExchange,
                marketCategory,
                isETF!!,
                roundLotSize,
                testIssue!!,
                financialStatus,
                CQSSymbol,
                NASDAQSymbol,
                nextShares!!
            )
        }
        .collect(Collectors.toMap(Investment::symbol, Function.identity())).let { mutableMap ->
            val hashMap = HashMap<String, Investment>(mutableMap.size)
            hashMap.putAll(mutableMap)
            return@let mutableMap
        }

    println("mapped")
    val completedInvestments: Stream<Investment?> = Stream.concat(stockZipEntries.stream(), etfZipEntries.stream())
        .parallel()
        .map {
            val symbol = it.name.substringAfter("/").substringBefore(".")
            symbolToInvestmentMap[symbol] to zipFile.getInputStream(it)
        }
        .map { pair ->
            val bufferedReader = pair.second!!.bufferedReader()
            val stockPoints = bufferedReader
                .lines()
                .skip(1)
                .parallel()
                .filter {
                    var last: Char = (-1).toChar()
                    it.forEach { char ->
                        if (char == ',' && char == last) return@filter false
                        last = char
                    }
                    return@filter true
                }
                .map {
                    val characterIndices = TreeSet<Int>(Collections.singleton(0))
                    it.forEachIndexed { index: Int, c: Char ->
                        // 0123_4_56_7_89_10_
                        if (c == ',' || (c == '-' && index <= 10)) {
                            characterIndices.add(index)
                        }
                    }
                    characterIndices.add(it.lastIndex)
                    val iterator = characterIndices.iterator()
                    var lastIndex = iterator.next()
                    val ranges = Array(characterIndices.size - 1, {
                        val currentIndex = iterator.next()
                        val out = (lastIndex + 1..<currentIndex)
                        lastIndex = currentIndex
                        return@Array out
                    })
                    val k = ranges.map { range ->
                        it.substring(range)
                    }
//                        .map { s -> if (s.isNotEmpty()) s else null }
                    StockPoint(k)
                }
            pair.first?.data = stockPoints.toList()
            return@map pair.first
        }
        .filter(Objects::nonNull)!!

    val fos = FileOutputStream("StockData.dat")
    BufferedOutputStream(fos, 8192 * 32).use { bos ->
//        ZipOutputStream(bos).use { zos ->
//            zos.putNextEntry(ZipEntry("a.dat"))
//            zos.setLevel(Deflater.FILTERED)
            val count = Stream.concat(stockZipEntries.stream(), etfZipEntries.stream()).count()
//            println("count: $count")
                ObjectOutputStream(bos).use { batchOos ->
                batchOos.writeLong(count)
            completedInvestments.parallel().forEach { investment ->
//                val batchBuffer = ByteArrayOutputStream()
                    synchronized(batchOos) { // Synchronize access to ObjectOutputStream
                        investment?.writeExternal(batchOos)
                    }
                }
//                ObjectOutputStream(batchBuffer).use { batchOos ->
//                    synchronized(batchOos) { // Synchronize access to ObjectOutputStream
//                        investment?.writeExternal(batchOos)
//                    }
//                }
//                synchronized(zos) {
//                    zos.write(batchBuffer.toByteArray())
//                }
            }
//            zos.flush()
//            zos.closeEntry()
//        }
    }
    println(fos.fd)
    println(fos.channel)
    println("Finished writing in ${System.currentTimeMillis() - start} ms")
}