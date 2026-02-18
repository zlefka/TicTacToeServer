package web.model


import domain.model.GameStatus
import domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class GameDTO(val id: String,
                   val board: GameBoardDTO,
                   val player1: String,
                   val player2: String?,
                   val currentTurn: String?,
                   val isTwoPlayers: Boolean,
                   val status: String,
                   val winnerIs: String?)