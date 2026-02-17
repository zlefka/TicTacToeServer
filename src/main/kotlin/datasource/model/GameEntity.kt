package datasource.model

import domain.model.GameStatus
import java.util.UUID

class GameEntity(
    val id: UUID,
    val board: GameBoardEntity,
    val player1: UserEntity,
    val player2: UserEntity?,
    val currentTurn: UUID?,
    val isTwoPlayers: Boolean,
    val status: GameStatusEntity,
    val winnerIs: UUID?
) {
}