package edu.wpi.a1cd.edu.wpi.a1cd

enum class MarketCategory {
    Q, S, G;
    companion object {
        fun make(it: String): MarketCategory? = when (it) {
            "Q" -> Q
            "S" -> S
            "G" -> G
            else -> null
        }
    }
}