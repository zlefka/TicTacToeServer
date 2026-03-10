package domain.repository


import domain.model.CurrentGame
import domain.model.Statistic
import java.util.UUID

interface GameRepository {
    fun save(game: CurrentGame)
    fun get(id: UUID): CurrentGame?
    fun getAvailableGames(): List<CurrentGame>

    fun getCompletedGames(userId: UUID): List<CurrentGame>

    fun getStatistic(limit: Int): List<Statistic>
}