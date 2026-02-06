package domain.service

import datasource.mapper.MapperDomainData
import datasource.repository.GameRepository
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameStatus

class MinimaxService(private val repository: GameRepository) : GameService {
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

            val newGame = CurrentGame(game.id, newBoard)
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
        computer: Cell = Cell.COMPUTER,
        human: Cell = Cell.PLAYER
    ): Pair<Int, Int> {
        val availableMoves = game.board.getAvailableMoves()
        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Int, Int> = availableMoves.first()

        for (move in availableMoves) {
            val newBoard = game.board.copy()
            newBoard.makeMove(move, computer)

            val newGame = CurrentGame(game.id, newBoard)
            val moveScore = minimax(newGame, depth = 0, isMaximizing = false, computer, human)

            if (moveScore > bestScore) {
                bestScore = moveScore
                bestMove = move
            }
        }

        return bestMove
    }

    override fun makeComputerMove(game: CurrentGame): CurrentGame {
        val bestMove = findBestMove(game)
        val newBoard = game.board.copy()
        newBoard.makeMove(bestMove, Cell.COMPUTER)
        val updatedGame = CurrentGame(game.id, newBoard)
        val gameEntity = MapperDomainData.fromDomainToData(updatedGame)
        repository.save(gameEntity)
        return updatedGame
    }

    override fun validatePlayerMove(
        previousGame: CurrentGame,
        currentGame: CurrentGame
    ): Boolean {
        val prevCells = previousGame.board.field
        val currCells = currentGame.board.field
        var diffCount = 0

        for (i in 0 until GameBoard.SIZE) {
            for (j in 0 until GameBoard.SIZE) {
                if (prevCells[i][j] != currCells[i][j]) {
                    if (prevCells[i][j] != Cell.EMPTY || currCells[i][j] != Cell.PLAYER) {
                        return false // изменён чужой ход или неправильный символ
                    }
                    diffCount++
                }
            }
        }

        return diffCount == 1
    }

    override fun checkGameStatus(currentGame: CurrentGame): GameStatus {
        return when {
            currentGame.board.checkWin(Cell.COMPUTER)       -> GameStatus.COMPUTER_WON
            currentGame.board.checkWin(Cell.PLAYER)         -> GameStatus.PLAYER_WON
            currentGame.board.getAvailableMoves().isEmpty() -> GameStatus.DRAW
            else                                            -> GameStatus.IN_PROGRESS

        }
    }
}