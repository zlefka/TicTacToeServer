package domain.model

import java.util.UUID
import kotlin.random.Random

data class CurrentGame(
    val id: UUID,
    val board: GameBoard,
    val player1: User,
    val player2: User?,
    val currentTurn: UUID?,
    val isTwoPlayers: Boolean,
    val status: GameStatus,
    val winnerIs: UUID?
) {

    companion object {
        fun new(player: User, isTwoPlayers: Boolean): CurrentGame =
            if(isTwoPlayers) createGameForTwoPlayers(player)
            else createGameWithComputer(player)

        private fun createGameForTwoPlayers(player: User): CurrentGame = CurrentGame(
            id = UUID.randomUUID(),
            board = GameBoard.empty(),
            player1 = player,
            player2 = null,
            currentTurn = player.id,
            isTwoPlayers = true,
            status = GameStatus.WAITING,
            winnerIs = null

        )

        private fun createGameWithComputer(player: User): CurrentGame = CurrentGame(
            id = UUID.randomUUID(),
            board = GameBoard.empty(),
            player1 = player,
            player2 = null,
            currentTurn = player.id,
            isTwoPlayers = false,
            status = GameStatus.IN_PROGRESS,
            winnerIs = null

        )
    }
}