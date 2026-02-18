package web.model

import kotlinx.serialization.Serializable

@Serializable
data class GameBoardDTO(val cells: List<List<Int>>)