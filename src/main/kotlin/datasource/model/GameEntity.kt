package datasource.model

import domain.model.Cell
import domain.model.GameBoard
import domain.model.GameState
import domain.model.User
import java.util.UUID

class GameEntity(
    val id: UUID,
    val board: GameBoardEntity,
    val player1: UserEntity,
    val player2: UserEntity?,
    val isBot: Boolean,
    val player1Symbol: CellEntity,
    val player2Symbol: CellEntity?,
    val state: String,
    val isTwoPlayers: Boolean,
) {
}