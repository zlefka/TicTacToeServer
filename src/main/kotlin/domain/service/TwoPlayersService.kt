package domain.service

import domain.repository.GameRepository
import domain.model.CurrentGame
import java.util.UUID

class TwoPlayersService(private val repository: GameRepository) : GameService {

    override fun makeMove(
        game: CurrentGame,
        playerId: UUID,
        move: Pair<Int, Int>
    ): CurrentGame {
        val updatedGame = game.makeMove(move, playerId)
        repository.save(updatedGame)
        return updatedGame
    }
}