package datasource.repository

import datasource.database.Users
import domain.model.User
import domain.repository.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserRepositoryImpl() : UserRepository {
    override fun saveUser(user: User) {
        transaction {
            Users.insert {
                it[id] = user.id
                it[login] = user.login
                it[passwordHash] = user.passwordHash
            }
        }

    }

    override fun getUserById(uuid: UUID): User? {
        val userId = transaction {
            Users.select(listOf(Users.id eq uuid)).singleOrNull()
        } ?: return null
        return User(userId[Users.id], userId[Users.login], userId[Users.passwordHash])
    }

    override fun getUserByLogin(login: String): User? {
        val userLogin = transaction {
            Users.select(listOf(Users.login eq login)).singleOrNull()
        } ?: return null
        return User(userLogin[Users.id], userLogin[Users.login], userLogin[Users.passwordHash])
    }

    override fun isUserExistsByLogin(login: String): Boolean {
        return transaction {
            !Users.select(Users.login eq login).empty()
        }
    }

}