package datasource.repository

import datasource.model.GameEntity
import domain.repository.GameRepository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GameRepositoryImpl() : GameRepository {
    val saved: ConcurrentHashMap<UUID, GameEntity> = ConcurrentHashMap<UUID, GameEntity>()
    override fun save(game: GameEntity) {
        val gameId = game.id
        saved[gameId] = game
    }

    override fun get(id: UUID): GameEntity? {
        return saved[id]
    }


}