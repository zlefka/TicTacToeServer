package datasource.mapper

import datasource.model.GameBoardEntity
import datasource.model.GameEntity
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard.Companion.SIZE

class MapperDomainData() {
    companion object {
        fun fromDomainToData(game: CurrentGame): GameEntity {
            val newBoard = Array(SIZE) { IntArray(SIZE) { 0 } }
            for (i in 0 until SIZE) {
                for (j in 0 until SIZE) {
                    when (game.board.field[i][j]) {
                        Cell.COMPUTER -> newBoard[i][j] = 2
                        Cell.PLAYER   -> newBoard[i][j] = 1
                        else          -> newBoard[i][j] = 0
                    }
                }
            }

            return GameEntity(id = game.id, board = GameBoardEntity(cells = newBoard))
        }
    }
}