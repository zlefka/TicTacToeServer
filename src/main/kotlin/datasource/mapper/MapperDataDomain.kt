package datasource.mapper

import datasource.model.CellEntity
import datasource.model.GameEntity
import datasource.model.GameStatusEntity
import datasource.model.UserEntity
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameBoard.Companion.SIZE
import domain.model.GameStatus
import domain.model.User

class MapperDataDomain() {
    companion object {
        fun fromDataToDomain(entity: GameEntity): CurrentGame {
            val newBoard: Array<Array<Cell>> = Array(SIZE) { Array(SIZE) { Cell.EMPTY } }
            for (i in 0 until SIZE) {
                for (j in 0 until SIZE) {
                    when (entity.board.cells[i][j]) {
                        CellEntity.EMPTY -> newBoard[i][j] = Cell.EMPTY
                        CellEntity.X     -> newBoard[i][j] = Cell.X
                        CellEntity.O     -> newBoard[i][j] = Cell.O
                    }
                }
            }

            val player1 = User(
                id = entity.player1.id,
                login = entity.player1.login,
                passwordHash = entity.player1.passwordHash
            )

            val player2 = entity.player2?.let {
                User(id = it.id, login = it.login, passwordHash = it.passwordHash)
            }

            val statusEntity = when (entity.status) {
                GameStatusEntity.WAITING      -> GameStatus.WAITING
                GameStatusEntity.IN_PROGRESS  -> GameStatus.IN_PROGRESS
                GameStatusEntity.DRAW         -> GameStatus.DRAW
                GameStatusEntity.PLAYER_WON   -> GameStatus.PLAYER_WON
                GameStatusEntity.COMPUTER_WON -> GameStatus.COMPUTER_WON
                GameStatusEntity.FINISHED     -> GameStatus.FINISHED
            }

            return CurrentGame(
                entity.id,
                GameBoard(newBoard),
                player1, player2,
                entity.currentTurn,
                entity.isTwoPlayers,
                statusEntity,
                entity.winnerIs
            )
        }
    }
}