package domain.service

import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameStatus

interface GameService {
    fun makeComputerMove(game: CurrentGame): CurrentGame
    fun validatePlayerMove(previousGame: CurrentGame, currentGame: CurrentGame): Boolean
    fun checkGameStatus(currentGame: CurrentGame): GameStatus
}