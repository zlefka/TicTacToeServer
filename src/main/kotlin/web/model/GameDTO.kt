package web.model



import kotlinx.serialization.Serializable


@Serializable
data class GameDTO(val id: String,
                   val board: GameBoardDTO,
                   val player1: String,
                   val player2: String?,
                   val currentTurn: String?,
                   val isTwoPlayers: Boolean,
                   val status: String,
                   val winnerIs: String?)