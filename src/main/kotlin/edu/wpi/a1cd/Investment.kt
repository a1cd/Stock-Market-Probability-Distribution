package edu.wpi.a1cd.edu.wpi.a1cd

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput

class Investment : Comparable<Investment>, Externalizable {
    var isNasdaqTraded: Boolean
    var symbol: String
    var securityName: String
    var listingExchange: String
    var marketCategory: MarketCategory?
    var isETF: Boolean
    var roundLotSize: Int
    var testIssue: Boolean
    var financialStatus: FinancialStatus?
    var CQSSymbol: String
    var NASDAQSymbol: String
    var nextShares: Boolean

    constructor(
        isNasdaqTraded: Boolean = false,
        symbol: String = "",
        securityName: String = "",
        listingExchange: String = "",
        marketCategory: MarketCategory? = null,
        isETF: Boolean = false,
        roundLotSize: Int = -1,
        testIssue: Boolean = false,
        financialStatus: FinancialStatus? = null,
        CQSSymbol: String = "",
        NASDAQSymbol: String = "",
        nextShares: Boolean = false
    ) {
        this.isNasdaqTraded = isNasdaqTraded
        this.symbol = symbol
        this.securityName = securityName
        this.listingExchange = listingExchange
        this.marketCategory = marketCategory
        this.isETF = isETF
        this.roundLotSize = roundLotSize
        this.testIssue = testIssue
        this.financialStatus = financialStatus
        this.CQSSymbol = CQSSymbol
        this.NASDAQSymbol = NASDAQSymbol
        this.nextShares = nextShares
        this.symbolComparator = Comparator.comparing(Investment::symbol)
    }
    constructor(`in`:ObjectInput): this() {
        this.readExternal(`in`)
    }

    lateinit var data: Collection<StockPoint>
    override fun compareTo(other: Investment): Int =
        Comparator.comparing(Investment::symbol)
            .thenComparing(Investment::securityName)
            .compare(this, other)
    val symbolComparator: Comparator<Investment>
    override fun writeExternal(out: ObjectOutput) {
        out.writeBoolean(isNasdaqTraded)
        out.writeBoolean(isETF)
        out.writeBoolean(testIssue)
        out.writeBoolean(nextShares)
        out.writeInt(marketCategory?.ordinal?:-1)
        out.writeInt(financialStatus?.ordinal?:-1)
        out.writeInt(roundLotSize)
        out.writeUTF(symbol)
        out.writeUTF(securityName)
        out.writeUTF(listingExchange)
        out.writeUTF(CQSSymbol)
        out.writeUTF(NASDAQSymbol)
        out.writeInt(data.size)
        for(i in data) {
            i.writeExternal(out)
        }
    }

    override fun readExternal(`in`: ObjectInput) {
        isNasdaqTraded = `in`.readBoolean()
        isETF = `in`.readBoolean()
        testIssue = `in`.readBoolean()
        nextShares = `in`.readBoolean()
        marketCategory = MarketCategory.entries.getOrNull(`in`.readInt())
        financialStatus =  FinancialStatus.entries.getOrNull(`in`.readInt())
        roundLotSize = `in`.readInt()
        symbol = `in`.readUTF()
        securityName = `in`.readUTF()
        listingExchange = `in`.readUTF()
        CQSSymbol = `in`.readUTF()
        NASDAQSymbol = `in`.readUTF()
        val size = `in`.readInt()
        val array = ArrayList<StockPoint>(size)
        for (i in 0 until size) {
            array.add(StockPoint(`in`))
        }
        data = array
    }
}