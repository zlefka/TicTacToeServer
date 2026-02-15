package domain.repository

import datasource.model.GameEntity
import java.util.UUID

interface GameRepository {
    fun save(game: GameEntity)
    fun get(id: UUID): GameEntity?
}