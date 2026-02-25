package web.mapper

import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard.Companion.SIZE
import domain.model.GameState
import web.model.GameBoardDTO
import web.model.GameDTO

class MapperDomainWeb {
    companion object {
        fun fromDomainToWeb(game: CurrentGame): GameDTO {
            val newBoard = List(SIZE) { i ->
                List(SIZE) { j ->
                    when (game.board.field[i][j]) {
                        Cell.X -> 1
                        Cell.O -> 2
                        Cell.EMPTY -> 0
                    }
                }
            }

            val statusString = when (val s = game.state) {
                is GameState.WaitingForPlayers -> "WAITING"
                is GameState.PlayerTurn        -> "TURN"
                is GameState.Draw -> "DRAW"
                is GameState.Winner -> "VICTORY"
            }

            return GameDTO(
                id = game.id.toString(),
                board = GameBoardDTO(cells = newBoard),
                player1 = game.player1.id.toString(),
                player2 = game.player2?.id?.toString(),
                currentTurn = (game.state as? GameState.PlayerTurn)?.playerID?.toString(),
                isTwoPlayers = game.isTwoPlayers,
                status = statusString,
                winnerIs = (game.state as? GameState.Winner)?.winnerID?.toString()
            )
        }
    }
}
