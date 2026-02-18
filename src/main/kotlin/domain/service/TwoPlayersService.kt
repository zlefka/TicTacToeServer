package domain.service

import domain.repository.GameRepository
import domain.model.CurrentGame
import domain.model.GameStatus

class TwoPlayersService(private val repository: GameRepository) : GameService {
    override fun makeMove(game: CurrentGame): CurrentGame {
        TODO("Not yet implemented")
    }

    override fun validatePlayerMove(
        previousGame: CurrentGame,
        currentGame: CurrentGame
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun checkGameStatus(currentGame: CurrentGame): GameStatus {
        TODO("Not yet implemented")
    }
}