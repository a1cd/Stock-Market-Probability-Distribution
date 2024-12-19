package edu.wpi.a1cd.edu.wpi.a1cd

enum class FinancialStatus {
    N, D, E, H;
    companion object {
        fun make(it: String): FinancialStatus? = when (it) {
            "N" -> N
            "D" -> D
            "E" -> E
            "H" -> H
            else -> null
        }
    }
}