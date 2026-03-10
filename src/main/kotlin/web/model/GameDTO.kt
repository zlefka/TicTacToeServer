package web.model


import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class GameDTO(
    val id: String,
    val board: GameBoardDTO,
    val player1: String,
    val player2: String?,
    val currentTurn: String?,
    val isTwoPlayers: Boolean,
    val status: String,
    val winnerIs: String?,
    val createdAt: String
)