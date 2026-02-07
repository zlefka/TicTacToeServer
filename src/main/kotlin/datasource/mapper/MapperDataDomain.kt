package datasource.mapper

import datasource.model.GameEntity
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameBoard.Companion.SIZE

class MapperDataDomain() {
    companion object {
        fun fromDataToDomain(entity: GameEntity): CurrentGame {
            val newBoard: Array<Array<Cell>> = Array(SIZE) { Array(SIZE) { Cell.EMPTY } }
            for (i in 0 until SIZE) {
                for (j in 0 until SIZE) {
                    when (entity.board.cells[i][j]) {
                        0    -> newBoard[i][j] = Cell.EMPTY
                        1    -> newBoard[i][j] = Cell.PLAYER
                        else -> newBoard[i][j] = Cell.COMPUTER
                    }
                }
            }
            return CurrentGame(id = entity.id, board = GameBoard(newBoard))
        }
    }
}