package datasource.repository

import datasource.database.GameTable
import datasource.database.Users
import datasource.model.CellEntity
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameStatus
import domain.model.User
import domain.repository.GameRepository
import io.ktor.client.plugins.UserAgent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update

class GameRepositoryImpl() : GameRepository {

    override fun save(game: CurrentGame) {
        transaction {
            val boardJson = Json.encodeToString(
                game.board.field.map { row ->
                    row.map { cell ->
                        when (cell) {
                            domain.model.Cell.EMPTY -> CellEntity.EMPTY
                            domain.model.Cell.X     -> CellEntity.X
                            domain.model.Cell.O     -> CellEntity.O
                        }
                    }
                }
            )

            val idExists = GameTable.select(listOf(GameTable.id eq game.id)).singleOrNull()

            if (idExists == null) {
                GameTable.insert {
                    it[id] = game.id
                    it[board] = boardJson
                    it[user1] = game.player1.id
                    it[user2] = game.player2?.id
                    it[currentTurn] = game.currentTurn
                    it[state] = game.status.toEntity()
                    it[winner] = game.winnerIs
                }
            } else {
                GameTable.update {
                    it[board] = boardJson
                    it[currentTurn] = game.currentTurn
                    it[state] = game.status.toEntity()
                    it[winner] = game.winnerIs
                    it[user2] = game.player2?.id
                }
            }
        }

    }

    override fun get(id: UUID): CurrentGame? {
        val gameDB = transaction {
            GameTable.select(listOf(GameTable.id eq id)).singleOrNull()
        } ?: return null
        val boardStr = gameDB[GameTable.board]
        val board = Json.decodeFromString<List<List<CellEntity>>>(boardStr)

        val mapBoard = Array(board.size) { i ->
            Array(board[i].size) { j ->
                when (board[i][j]) {
                    CellEntity.EMPTY -> domain.model.Cell.EMPTY
                    CellEntity.X     -> domain.model.Cell.X
                    CellEntity.O     -> domain.model.Cell.O
                }
            }
        }

        val player1 = transaction {
            val user1 = Users.select(listOf(Users.id eq gameDB[GameTable.user1])).single()
            User(user1[Users.id], user1[Users.login], user1[Users.passwordHash])
        }

        val player2 = gameDB[GameTable.user2]?.let {
            transaction {
                val user2 = Users.select(listOf(Users.id eq gameDB[GameTable.user1])).single()
                User(user2[Users.id], user2[Users.login], user2[Users.passwordHash])
            }
        }

        return CurrentGame(
            id = gameDB[GameTable.id],
            board = GameBoard(mapBoard),
            player1 = player1,
            player2 = player2,
            currentTurn = gameDB[GameTable.currentTurn],
            isTwoPlayers = player2 != null,
            status = gameDB[GameTable.state].toDomain(),
            winnerIs = gameDB[GameTable.winner]
        )
    }

    private fun GameStatus.toEntity(): datasource.model.GameStatusEntity = when (this) {
        GameStatus.WAITING      -> datasource.model.GameStatusEntity.WAITING
        GameStatus.IN_PROGRESS  -> datasource.model.GameStatusEntity.IN_PROGRESS
        GameStatus.PLAYER_WON   -> datasource.model.GameStatusEntity.PLAYER_WON
        GameStatus.COMPUTER_WON -> datasource.model.GameStatusEntity.COMPUTER_WON
        GameStatus.DRAW         -> datasource.model.GameStatusEntity.DRAW
        GameStatus.FINISHED     -> datasource.model.GameStatusEntity.FINISHED
    }

    fun datasource.model.GameStatusEntity.toDomain(): GameStatus = when (this) {
        datasource.model.GameStatusEntity.WAITING      -> GameStatus.WAITING
        datasource.model.GameStatusEntity.IN_PROGRESS  -> GameStatus.IN_PROGRESS
        datasource.model.GameStatusEntity.PLAYER_WON   -> GameStatus.PLAYER_WON
        datasource.model.GameStatusEntity.COMPUTER_WON -> GameStatus.COMPUTER_WON
        datasource.model.GameStatusEntity.DRAW         -> GameStatus.DRAW
        datasource.model.GameStatusEntity.FINISHED     -> GameStatus.FINISHED
    }
}