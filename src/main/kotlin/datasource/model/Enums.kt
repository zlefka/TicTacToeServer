package datasource.model

import kotlinx.serialization.Serializable

@Serializable
enum class CellEntity {
    EMPTY,
    X,
    O
}

enum class GameStatusEntity {
    IN_PROGRESS,
    DRAW,
    PLAYER_WON,
    COMPUTER_WON,
    WAITING,
    FINISHED
}