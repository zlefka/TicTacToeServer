package domain.repository

import datasource.model.GameEntity
import domain.model.CurrentGame
import java.util.UUID

interface GameRepository {
    fun save(game: CurrentGame)
    fun get(id: UUID): CurrentGame?
}