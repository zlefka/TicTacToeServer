package domain.model

enum class Cell {
    EMPTY,
    PLAYER,
    COMPUTER
}

enum class GameStatus {
    IN_PROGRESS,
    DRAW,
    PLAYER_WON,
    COMPUTER_WON
}