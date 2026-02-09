package web.mapper

import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameBoard.Companion.SIZE
import web.model.GameDTO
import java.util.UUID

class MapperWebDomain() {
    companion object {
        fun fromWebToDomain(dto: GameDTO): CurrentGame {
            val newBoard: Array<Array<Cell>> = Array(SIZE) { Array(SIZE) { Cell.EMPTY } }
            for (i in 0 until SIZE) {
                for (j in 0 until SIZE) {
                    when (dto.board.cells[i][j]) {
                        0    -> newBoard[i][j] = Cell.EMPTY
                        1    -> newBoard[i][j] = Cell.PLAYER
                        else -> newBoard[i][j] = Cell.COMPUTER
                    }
                }
            }
            return CurrentGame(id = UUID.fromString(dto.id), board = GameBoard(newBoard))
        }
    }
}