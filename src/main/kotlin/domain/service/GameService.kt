package domain.service

import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameState
import java.util.UUID


interface GameService {
    fun makeMove(game: CurrentGame, playerId: UUID, move: Pair<Int, Int>): CurrentGame
    fun calculateState(game: CurrentGame): GameState {
        val board = game.board

        if(board.checkWin(game.player1Symbol)) {
            return GameState.Winner(game.player1.id)
        }

        if(game.player2 != null && game.player2Symbol != null && board.checkWin(game.player2Symbol)) {
            return GameState.Winner(game.player2.id)
        }

        if(board.getAvailableMoves().isEmpty()) {
            return GameState.Draw
        }

        return when(game.state) {
            is GameState.PlayerTurn -> {
                val nextPlayer = if(game.state.playerID == game.player1.id) game.player2!!.id
                else game.player1.id
                GameState.PlayerTurn(nextPlayer)
            }
            else -> game.state
        }
    }
}