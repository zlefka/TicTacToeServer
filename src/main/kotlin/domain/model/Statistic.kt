package domain.model

import java.util.UUID

data class Statistic(
    val userId: UUID,
    val wins: Int,
    val loses: Int,
    val draws: Int,
    val ratio: Double
)

data class PlayerStats(
    var wins: Int = 0,
    var loses: Int = 0,
    var draws: Int = 0
)
