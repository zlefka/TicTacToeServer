package domain.model

import java.util.UUID

class CurrentGame(
    val id: UUID,
    val board: GameBoard,
) {
    lateinit var gameStatus: GameStatus

    companion object {
        fun new(): CurrentGame {
            val new = CurrentGame(id = UUID.randomUUID(), board = GameBoard.empty())
            new.gameStatus = GameStatus.IN_PROGRESS
            return new
        }
    }
}