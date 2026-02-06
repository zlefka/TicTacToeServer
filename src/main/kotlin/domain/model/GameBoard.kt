package domain.model

class GameBoard(val field: Array<Array<Cell>>) {

    init {
        require(field.size == SIZE)
        require(field.all { it.size == SIZE })
    }

    companion object {
        const val SIZE = 3

        fun empty(): GameBoard = GameBoard(Array(SIZE) { Array(SIZE) { Cell.EMPTY } })
    }

    fun copy(): GameBoard = GameBoard(Array(SIZE) { row ->
        field[row].clone()
    })

    fun checkWin(symbol: Cell): Boolean {
        for (i in 0 until SIZE) {
            //строки
            if (field[i][0] == symbol && field[i][1] == symbol && field[i][2] == symbol) return true
        }
        for (i in 0 until SIZE) {
            //строки
            if (field[0][i] == symbol && field[1][i] == symbol && field[2][i] == symbol) return true
        }
        //диагонали
        if (field[0][0] == symbol && field[1][1] == symbol && field[2][2] == symbol) return true
        if (field[0][2] == symbol && field[1][1] == symbol && field[2][0] == symbol) return true
        return false
    }

    fun getAvailableMoves(): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until SIZE) {
            for (j in 0 until SIZE) {
                if (field[i][j] == Cell.EMPTY) {
                    moves.add(Pair(i, j))
                }
            }
        }
        return moves
    }

    fun isGameOver(): Boolean {
        return checkWin(Cell.COMPUTER) || checkWin(Cell.PLAYER) || getAvailableMoves().isEmpty()
    }

    fun makeMove(move: Pair<Int, Int>, symbol: Cell) {
        val (row, col) = move
        if (field[row][col] != Cell.EMPTY) {
            throw IllegalArgumentException("Cell [$row, $col] is already occupied") // подумать что можно чтобы не крашилось
        }
        field[row][col] = symbol
    }

}
