package web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class GameDTO(val id: String, val board: GameBoardDTO)  {
}