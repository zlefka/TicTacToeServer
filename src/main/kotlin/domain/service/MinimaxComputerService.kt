package domain.service

import domain.repository.GameRepository
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameState
import java.util.UUID

class MinimaxComputerService(private val repository: GameRepository) : GameService {
    private fun score(currentGame: CurrentGame, depth: Int, player: Cell, computer: Cell): Int {
        return when {
            currentGame.board.checkWin(computer) -> 10 - depth // компьютер выйграл
            currentGame.board.checkWin(player)   -> depth - 10 // игрок выйграл
            else                                 -> 0 // ничья
        }
    }

    private fun minimax(game: CurrentGame, depth: Int, isMaximizing: Boolean, player: Cell, computer: Cell): Int {
        if (game.board.isGameOver()) {
            return score(game, depth, player, computer)
        }
        val currentDepth = depth + 1
        val availableMoves = game.board.getAvailableMoves()
        val scores = mutableListOf<Int>()

        for (move in availableMoves) {
            val newBoard = game.board.copy()
            if (isMaximizing) {
                newBoard.makeMove(move, computer)
            } else {
                newBoard.makeMove(move, player)
            }

            val newGame = game.copy(board = newBoard)
            val moveScores = minimax(newGame, currentDepth, !isMaximizing, computer, player)
            scores.add(moveScores)
        }
        return if (isMaximizing) {
            scores.maxOrNull() ?: 0
        } else {
            scores.minOrNull() ?: 0
        }
    }

    private fun findBestMove(
        game: CurrentGame,
        o: Cell = Cell.O,
        human: Cell = Cell.X
    ): Pair<Int, Int> {
        val availableMoves = game.board.getAvailableMoves()
        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Int, Int> = availableMoves.first()

        for (move in availableMoves) {
            val newBoard = game.board.copy()
            newBoard.makeMove(move, o)

            val newGame = game.copy(board = newBoard)
            val moveScore = minimax(newGame, depth = 0, isMaximizing = false, o, human)

            if (moveScore > bestScore) {
                bestScore = moveScore
                bestMove = move
            }
        }

        return bestMove
    }

    override fun makeMove(game: CurrentGame, playerId: UUID, move: Pair<Int, Int>): CurrentGame {
        val afterMove = game.makeMove(move, playerId)

        if (afterMove.state !is GameState.PlayerTurn) {
            repository.save(afterMove)
            return afterMove
        }

        if (afterMove.isBot) {

            val botId = afterMove.player2?.id
                ?: error("Bot user missing")

            val currentTurn = afterMove.state.playerID

            if (currentTurn == botId) {

                val botMove = findBestMove(afterMove)

                val afterBotMove = afterMove.makeMove(botMove, botId)

                repository.save(afterBotMove)
                return afterBotMove
            }
        }

        repository.save(afterMove)
        return afterMove
    }
}