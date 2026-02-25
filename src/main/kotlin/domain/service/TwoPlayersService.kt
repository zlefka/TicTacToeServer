package domain.service

import domain.model.Cell
import domain.repository.GameRepository
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameState
import org.koin.core.context.unloadKoinModules
import java.util.UUID

class TwoPlayersService(private val repository: GameRepository) : GameService {

    override fun makeMove(
        game: CurrentGame,
        playerId: UUID?,
        move: Pair<Int, Int>?
    ): CurrentGame {
        val symbol = when (playerId) {
            game.player1.id  -> game.player1Symbol
            game.player2?.id -> game.player2Symbol
            else             -> throw IllegalArgumentException("Invalid player")
        }

        val newBoard = game.board.copy()
        newBoard.makeMove(move!!, symbol!!)

        val updatedGame = game.copy(board = newBoard)

        if(validatePlayerMove(game, updatedGame, playerId)) {

            val newState = calculateState(updatedGame)

            val finalGame = updatedGame.copy(state = newState)

            repository.save(finalGame)

            return finalGame
        } else return game
    }
}