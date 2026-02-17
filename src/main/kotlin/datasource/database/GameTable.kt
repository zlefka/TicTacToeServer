package datasource.database

import datasource.model.CellEntity
import datasource.model.GameStatusEntity
import domain.model.GameStatus
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object GameTable: Table("game") {
    val id =  uuid("id").autoGenerate()
    val board = text("board")
    val user1 = reference("user1", Users.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val user2 = optReference("user2", Users.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val currentTurn = optReference("current_turn", Users.id)
    val state = enumeration("state", GameStatusEntity::class)
    val winner = optReference("winner", Users.id)

    override val primaryKey = PrimaryKey(GameTable.id, name = "GameBoardID")
}