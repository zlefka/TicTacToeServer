package datasource.repository

import datasource.database.GameTable
import datasource.database.Users
import datasource.model.CellEntity
import domain.model.Cell
import domain.model.CurrentGame
import domain.model.GameBoard
import domain.model.GameState
import domain.model.PlayerStats
import domain.model.Statistic
import domain.model.User
import domain.repository.GameRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import web.serialization.UUIDSerializer
import org.jetbrains.exposed.sql.and
import java.time.Instant

class GameRepositoryImpl(
    private val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(UUID::class, UUIDSerializer)
        }
    }
) : GameRepository {

    override fun save(game: CurrentGame) {
        transaction {
            val boardJson = json.encodeToString(
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

            val idExists = GameTable.selectAll().where { GameTable.id eq game.id }.singleOrNull()
            println("Serializing state: ${game.state}")

            val stateJson = json.encodeToString(game.state)

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
                    it[createdAt] = Instant.now()
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
        val gameDB = GameTable.selectAll().where { GameTable.id eq id }.singleOrNull() ?: return@transaction null

        val boardStr = gameDB[GameTable.board]
        val board = json.decodeFromString<List<List<CellEntity>>>(boardStr)

        val player1Row = Users.selectAll().where { Users.id eq gameDB[GameTable.user1] }.single()
        val player1 = User(
            player1Row[Users.id],
            player1Row[Users.login],
            player1Row[Users.passwordHash]
        )

        val player2 = gameDB[GameTable.user2]?.let { user2Id ->
            Users.selectAll()
                .where { Users.id eq user2Id }
                .singleOrNull()
                ?.let { row ->
                    User(row[Users.id], row[Users.login], row[Users.passwordHash])
                }
        }


        val gameState = json.decodeFromString<GameState>(gameDB[GameTable.state])

        val mapBoard = Array(board.size) { i ->
            Array(board[i].size) { j ->
                when (board[i][j]) {
                    CellEntity.EMPTY -> Cell.EMPTY
                    CellEntity.X     -> Cell.X
                    CellEntity.O     -> Cell.O
                }
            }
        }

        CurrentGame(
            id = gameDB[GameTable.id],
            board = GameBoard(mapBoard),
            player1 = player1,
            player2 = player2,
            isBot = gameDB[GameTable.isBot],
            player1Symbol = Cell.valueOf(gameDB[GameTable.player1Symbol]),
            player2Symbol = Cell.valueOf(gameDB[GameTable.player2Symbol]),
            state = gameState,
            isTwoPlayers = gameDB[GameTable.isTwoPlayers]
        )
    }

    override fun getAvailableGames(): List<CurrentGame> = transaction {
        GameTable.selectAll().where { (GameTable.isTwoPlayers eq true) and
                (GameTable.user2.isNull()) and
                (GameTable.isBot eq false) }
            .mapNotNull { gameDB ->
                val board = json.decodeFromString<List<List<CellEntity>>>(gameDB[GameTable.board])
                val player1Row = Users.selectAll().where { Users.id eq gameDB[GameTable.user1] }.single()
                val player1 = User(
                    player1Row[Users.id],
                    player1Row[Users.login],
                    player1Row[Users.passwordHash]
                )

                val player2 = gameDB[GameTable.user2]?.let { user2Id ->
                    Users.selectAll()
                        .where { Users.id eq user2Id }
                        .singleOrNull()
                        ?.let { row ->
                            User(row[Users.id], row[Users.login], row[Users.passwordHash])
                        }
                }
                CurrentGame(
                    id = gameDB[GameTable.id],
                    board = GameBoard(Array(board.size) { i ->
                        Array(board[i].size) { j ->
                            when (board[i][j]) {
                                CellEntity.EMPTY -> Cell.EMPTY
                                CellEntity.X     -> Cell.X
                                CellEntity.O     -> Cell.O
                            }
                        }
                    }),
                    player1 = player1,
                    player2 = player2,
                    isBot = gameDB[GameTable.isBot],
                    player1Symbol = Cell.valueOf(gameDB[GameTable.player1Symbol]),
                    player2Symbol = Cell.valueOf(gameDB[GameTable.player2Symbol]),
                    state = json.decodeFromString(gameDB[GameTable.state]),
                    isTwoPlayers = gameDB[GameTable.isTwoPlayers]
                )
            }
    }

    override fun getCompletedGames(userId: UUID): List<CurrentGame> = transaction {
        GameTable.selectAll()
            .mapNotNull { row ->
                if (row[GameTable.user1] != userId && row[GameTable.user2] != userId) return@mapNotNull null

                val state = json.decodeFromString<GameState>(row[GameTable.state])

                if (state !is GameState.Draw && state !is GameState.Winner) return@mapNotNull null

                val boardJson = row[GameTable.board]
                val boardList = json.decodeFromString<List<List<CellEntity>>>(boardJson)
                val mapBoard = Array(boardList.size) { i ->
                    Array(boardList[i].size) { j ->
                        when (boardList[i][j]) {
                            CellEntity.EMPTY -> Cell.EMPTY
                            CellEntity.X     -> Cell.X
                            CellEntity.O     -> Cell.O
                        }
                    }
                }

                val player1Row = Users.selectAll().where { Users.id eq row[GameTable.user1] }.single()
                val player1 = User(
                    id = player1Row[Users.id],
                    login = player1Row[Users.login],
                    passwordHash = player1Row[Users.passwordHash]
                )

                val player2 = row[GameTable.user2]?.let { user2Id ->
                    Users.selectAll().where { Users.id eq user2Id }
                        .singleOrNull()
                        ?.let { r -> User(r[Users.id], r[Users.login], r[Users.passwordHash]) }
                }

                CurrentGame(
                    id = row[GameTable.id],
                    board = GameBoard(mapBoard),
                    player1 = player1,
                    player2 = player2,
                    isBot = row[GameTable.isBot],
                    player1Symbol = Cell.valueOf(row[GameTable.player1Symbol]),
                    player2Symbol = Cell.valueOf(row[GameTable.player2Symbol]),
                    state = state,
                    isTwoPlayers = row[GameTable.isTwoPlayers]
                )
            }
    }

    override fun getStatistic(limit: Int): List<Statistic> = transaction {

        val stats = mutableMapOf<UUID, PlayerStats>()

        GameTable.selectAll().forEach { row ->

            val state = json.decodeFromString<GameState>(row[GameTable.state])

            val player1 = row[GameTable.user1]
            val player2 = row[GameTable.user2]

            stats.putIfAbsent(player1, PlayerStats())
            player2?.let { stats.putIfAbsent(it, PlayerStats()) }

            when (state) {

                is GameState.Winner -> {

                    val winner = state.winnerID

                    if (winner == player1) {

                        stats[player1]!!.wins++

                        player2?.let {
                            stats[it]!!.loses++
                        }

                    } else {

                        player2?.let {
                            stats[it]!!.wins++
                        }

                        stats[player1]!!.loses++
                    }
                }

                is GameState.Draw -> {

                    stats[player1]!!.draws++

                    player2?.let {
                        stats[it]!!.draws++
                    }
                }

                else -> {}
            }
        }

        stats.map { (userId, stat) ->

            val total = stat.wins + stat.loses + stat.draws

            val ratio =
                if (total == 0) 0.0
                else stat.wins.toDouble() / total

            Statistic(
                userId = userId,
                wins = stat.wins,
                loses = stat.loses,
                draws = stat.draws,
                ratio = ratio
            )

        }
            .sortedByDescending { it.ratio }
            .take(limit)
    }


}