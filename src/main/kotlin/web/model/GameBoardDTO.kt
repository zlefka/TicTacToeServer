package web.model

import kotlinx.serialization.Serializable

@Serializable
class GameBoardDTO(val cells: List<List<Int>>) {
}