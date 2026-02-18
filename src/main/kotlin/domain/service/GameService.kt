package domain.service

import domain.model.CurrentGame
import domain.model.GameStatus

interface GameService {
    fun makeMove(game: CurrentGame): CurrentGame
    fun validatePlayerMove(previousGame: CurrentGame, currentGame: CurrentGame): Boolean
    fun checkGameStatus(currentGame: CurrentGame): GameStatus
}