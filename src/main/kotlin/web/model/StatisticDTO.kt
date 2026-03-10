package web.model

import kotlinx.serialization.Serializable

@Serializable
data class StatisticDTO(
    val userId: String,
    val login: String,
    val wins: Int,
    val loses: Int,
    val draws: Int,
    val ratio: Double
)
