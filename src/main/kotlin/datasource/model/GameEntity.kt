package datasource.model

import java.util.UUID
import java.time.Instant

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
    val createdAt: Instant = Instant.now()
) {
}