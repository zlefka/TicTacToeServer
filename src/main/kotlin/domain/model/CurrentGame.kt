package domain.model

import java.util.UUID

data class CurrentGame(
    val id: UUID,
    val board: GameBoard,
    val player1: User,
    val player2: User?,
    val isBot: Boolean,
    val player1Symbol: Cell,
    val player2Symbol: Cell,
    val state: GameState,
    val isTwoPlayers: Boolean,
) {

    companion object {
        fun new(player: User, isTwoPlayers: Boolean, computer: User? = null): CurrentGame {
            return if (isTwoPlayers) {
                CurrentGame(
                    id = UUID.randomUUID(),
                    board = GameBoard.empty(),
                    player1 = player,
                    player2 = null,
                    isBot = false,
                    player1Symbol = Cell.X,
                    player2Symbol = Cell.O,
                    state = GameState.WaitingForPlayers,
                    isTwoPlayers = true
                )
            } else {
                CurrentGame(
                    id = UUID.randomUUID(),
                    board = GameBoard.empty(),
                    player1 = player,
                    player2 = computer,
                    isBot = true,
                    player1Symbol = Cell.X,
                    player2Symbol = Cell.O,
                    state = GameState.PlayerTurn(player.id),
                    isTwoPlayers = false,
                )
            }
        }
    }

    fun makeMove(coordinates: Pair<Int, Int>, playerID: UUID): CurrentGame {
        if (this.state is GameState.Winner || this.state is GameState.Draw) {
            throw IllegalStateException("Game is already finished")
        }

        val updatedBoard = this.board.copy()
        val (row, col) = coordinates
        val currentPlayerID = (state as? GameState.PlayerTurn)?.playerID
        if (playerID != currentPlayerID) {
            throw IllegalArgumentException("It is another player's turn")
        }

        val symbol = when (playerID) {
            player1.id -> player1Symbol
            player2?.id -> if (player1Symbol == Cell.X) Cell.O else Cell.X
            else -> throw IllegalArgumentException("Invalid player")
        }
        if (board.field[row][col] != Cell.EMPTY) {
            throw IllegalArgumentException("This cell is busy")
        } else {
            updatedBoard.field[row][col] = symbol
        }

        val newState = when {
            updatedBoard.checkWin(symbol)              -> GameState.Winner(playerID)
            updatedBoard.getAvailableMoves().isEmpty() -> GameState.Draw
            else                                       -> {
                val nextPlayerID = if (playerID == player1.id) player2?.id ?: player1.id else player1.id
                GameState.PlayerTurn(nextPlayerID)
            }
        }
        return copy(board = updatedBoard, state = newState)
    }

    fun join(player: User): CurrentGame? =
        if (!this.isBot && this.isTwoPlayers && this.player2 == null && this.state == GameState.WaitingForPlayers) {
            this.copy(player2 = player, state = GameState.PlayerTurn(player1.id))
        } else null
}