package datasource.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object GameTable : Table("game") {
    val id = uuid("id").autoGenerate()
    val board = text("board")
    val user1 = reference("user1", Users.id, onDelete = ReferenceOption.CASCADE)
    val user2 = optReference("user2", Users.id, onDelete = ReferenceOption.CASCADE)
    val isBot = bool("is_bot")
    val player1Symbol = text("player1_symbol")
    val player2Symbol = text("player2_symbol")
    val state = text("state_json")
    val isTwoPlayers = bool("is_two_players")

    override val primaryKey = PrimaryKey(id)
}