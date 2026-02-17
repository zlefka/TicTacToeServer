package datasource.mapper

import datasource.model.CellEntity
import datasource.model.GameBoardEntity
import datasource.model.GameEntity
import datasource.model.GameStatusEntity
import datasource.model.UserEntity
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard.Companion.SIZE
import domain.model.GameStatus
import domain.model.User

class MapperDomainData() {
    companion object {
        fun fromDomainToData(game: CurrentGame): GameEntity {
            val newBoard: Array<Array<CellEntity>> = Array(SIZE) { Array(SIZE) { CellEntity.O } }
            for (i in 0 until SIZE) {
                for (j in 0 until SIZE) {
                    when (game.board.field[i][j]) {
                        Cell.O -> newBoard[i][j] = CellEntity.O
                        Cell.X -> newBoard[i][j] = CellEntity.X
                        else   -> newBoard[i][j] = CellEntity.EMPTY
                    }
                }
            }

            val player1Entity = UserEntity(
                id = game.player1.id,
                login = game.player1.login,
                passwordHash = game.player1.passwordHash
            )

            val player2Entity = game.player2?.let {
                UserEntity(id = it.id, login = it.login, passwordHash = it.passwordHash)
            }

            val statusEntity = when (game.status) {
                GameStatus.WAITING      -> GameStatusEntity.WAITING
                GameStatus.IN_PROGRESS  -> GameStatusEntity.IN_PROGRESS
                GameStatus.DRAW         -> GameStatusEntity.DRAW
                GameStatus.PLAYER_WON   -> GameStatusEntity.PLAYER_WON
                GameStatus.COMPUTER_WON -> GameStatusEntity.COMPUTER_WON
                GameStatus.FINISHED     -> GameStatusEntity.FINISHED
            }

            return GameEntity(
                game.id,
                GameBoardEntity(newBoard),
                player1Entity,
                player2Entity,
                game.currentTurn,
                game.isTwoPlayers,
                statusEntity,
                game.winnerIs
            )
        }
    }
}