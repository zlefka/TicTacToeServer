package datasource.repository

import datasource.database.GameTable
import datasource.database.Users
import datasource.model.CellEntity
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameState
import domain.model.User
import domain.repository.GameRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
import web.serialization.UUIDSerializer

class GameRepositoryImpl() : GameRepository {

    override fun save(game: CurrentGame) {
        transaction {
            val boardJson = Json.encodeToString(
                game.board.field.map { row ->
                    row.map { cell ->
                        when (cell) {
                            Cell.EMPTY -> CellEntity.EMPTY
                            Cell.X     -> CellEntity.X
                            Cell.O     -> CellEntity.O
                        }
                    }
                }
            )

            val idExists = GameTable.select( GameTable.id eq game.id ).singleOrNull()
            println("Serializing state: ${game.state}")
            val jsonWithUUID = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    contextual(UUID::class, UUIDSerializer)
                }
            }

            val stateJson = jsonWithUUID.encodeToString(game.state)
            println("encoded")

            if (idExists == null) {
                GameTable.insert {
                    it[id] = game.id
                    it[board] = boardJson
                    it[user1] = game.player1.id
                    it[user2] = game.player2?.id
                    it[isBot] = game.isBot
                    it[player1Symbol] = game.player1Symbol.toString()
                    it[player2Symbol] = game.player2Symbol.toString()
                    it[state] = stateJson
                    it[isTwoPlayers] = game.isTwoPlayers
                }
            } else {
                GameTable.update({ GameTable.id eq game.id }) {
                    it[board] = boardJson
                    it[isBot] = game.isBot
                    it[state] = stateJson
                    it[user2] = game.player2?.id
                }
            }
        }

    }

    override fun get(id: UUID): CurrentGame? = transaction {
        val gameDB = GameTable.select(GameTable.id eq id).singleOrNull() ?: return@transaction null
        val boardStr = gameDB[GameTable.board]
        val board = Json.decodeFromString<List<List<CellEntity>>>(boardStr)


        val player1DB = Users.select(Users.id eq gameDB[GameTable.user1]).single()
        val player1 = User(player1DB[Users.id], player1DB[Users.login], player1DB[Users.passwordHash])

        val player2 = gameDB[GameTable.user2]?.let { user2 ->
            val user2 = Users.select(listOf(Users.id eq user2)).single()
            User(user2[Users.id], user2[Users.login], user2[Users.passwordHash])
        }

        val gameState = Json.decodeFromString<GameState>(
            gameDB[GameTable.state]
        )


        val mapBoard = Array(board.size) { i ->
            Array(board[i].size) { j ->
                when (board[i][j]) {
                    CellEntity.EMPTY -> Cell.EMPTY
                    CellEntity.X     -> Cell.X
                    CellEntity.O     -> Cell.O
                }
            }
        }

        val player1Sym = Cell.valueOf(gameDB[GameTable.player1Symbol])
        val player2Sym = gameDB[GameTable.player2Symbol].let { Cell.valueOf(it) }

        CurrentGame(
            id = gameDB[GameTable.id],
            board = GameBoard(mapBoard),
            player1 = player1,
            player2 = player2,
            isBot = gameDB[GameTable.isBot],
            player1Symbol = player1Sym,
            player2Symbol = player2Sym,
            state = gameState,
            isTwoPlayers =gameDB[GameTable.isTwoPlayers]
        )
    }
}