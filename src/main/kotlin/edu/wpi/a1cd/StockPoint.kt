package edu.wpi.a1cd.edu.wpi.a1cd

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.*

class StockPoint private constructor (
    var date: Date = Date(),
    var open: Double = 0.0,
    var high: Double = 0.0,
    var low: Double = 0.0,
    var close: Double = 0.0,
    var adjustedClose: Double = 0.0,
    var volume: Int = 0
): Externalizable {
    constructor(`in`: ObjectInput) : this() {
        this.readExternal(`in`)
    }
    constructor(it: List<String?>): this(
        date = kotlin.run {
            val year = it[0]!!.toInt()
            val month = it[1]!!.toInt()
            val day = it[2]!!.toInt()
            return@run synchronized(calendar) {
                calendar.set(year, month, day)
                return@synchronized calendar.time
            }
        },
        open = it[3]?.toDouble() ?: 0.0,
        high = it[4]?.toDouble() ?: 0.0,
        low = it[5]?.toDouble() ?: 0.0,
        close = it[6]?.toDouble() ?: 0.0,
        adjustedClose = it[7]?.toDouble() ?: 0.0,
        volume = it[8]?.toIntOrNull() ?: 0
    )
    companion object {
        private val calendar get() = Calendar.getInstance()
    }

    override fun writeExternal(out: ObjectOutput) {
        out.writeLong(date.time)
        out.writeDouble(open)
        out.writeDouble(high)
        out.writeDouble(low)
        out.writeDouble(close)
        out.writeDouble(adjustedClose)
        out.writeInt(volume)
    }

    override fun readExternal(`in`: ObjectInput) {
        date = Date(`in`.readLong())
        open = `in`.readDouble()
        high = `in`.readDouble()
        low = `in`.readDouble()
        close = `in`.readDouble()
        adjustedClose = `in`.readDouble()
        volume = `in`.readInt()
    }
}