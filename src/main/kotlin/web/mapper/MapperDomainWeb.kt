package web.mapper

import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard.Companion.SIZE
import web.model.GameBoardDTO
import web.model.GameDTO
import java.util.UUID

class MapperDomainWeb {
    companion object {
        fun fromDomainToWeb(game: CurrentGame): GameDTO {
            val newBoard = List(SIZE) { i ->
                List(SIZE) { j ->
                    when (game.board.field[i][j]) {
                        Cell.COMPUTER -> 2
                        Cell.PLAYER   -> 1
                        else          -> 0
                    }
                }
            }
            return GameDTO(id = game.id.toString(), board = GameBoardDTO(cells = newBoard))
        }
    }
}
