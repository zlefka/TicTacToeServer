package datasource.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanParam

object Users  : Table("users") {
    val id = uuid("id").autoGenerate()
    val login = varchar("login", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 256)

    override val primaryKey = PrimaryKey(id)
}