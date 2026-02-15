package domain.model

enum class Cell {
    EMPTY,
    X,
    O
}

enum class GameStatus {
    IN_PROGRESS,
    DRAW,
    PLAYER_WON,
    COMPUTER_WON,
    WAITING,
    FINISHED
}