package domain.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID
@Serializable
sealed class GameState {
    @Serializable
    object WaitingForPlayers : GameState()

    @Serializable
    data class PlayerTurn(@Contextual val playerID: UUID) : GameState()

    @Serializable
    object Draw : GameState()

    @Serializable
    data class Winner(@Contextual val winnerID: UUID) : GameState()
}