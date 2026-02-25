package domain.repository


import domain.model.CurrentGame
import domain.model.User
import java.util.UUID

interface GameRepository {
    fun save(game: CurrentGame)
    fun get(id: UUID): CurrentGame?
}