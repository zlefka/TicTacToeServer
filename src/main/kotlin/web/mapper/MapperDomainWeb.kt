package web.mapper

import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard.Companion.SIZE
import web.model.GameBoardDTO
import web.model.GameDTO

class MapperDomainWeb {
    companion object {
        fun fromDomainToWeb(game: CurrentGame): GameDTO {
            val newBoard = List(SIZE) { i ->
                List(SIZE) { j ->
                    when (game.board.field[i][j]) {
                        Cell.O -> 2
                        Cell.X -> 1
                        else          -> 0
                    }
                }
            }
            return GameDTO(id = game.id.toString(),
                board = GameBoardDTO(cells = newBoard),
                player1 = game.player1.id.toString(),
                player2 = game.player2?.id?.toString(),
                currentTurn = game.currentTurn?.toString(),
                isTwoPlayers = game.isTwoPlayers,
                status = game.status.toString(),
                winnerIs = game.winnerIs?.toString())
        }
    }
}
