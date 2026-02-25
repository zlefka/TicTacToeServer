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
        return transaction {
            Users.select (Users.id, Users.login, Users.passwordHash).where(Users.id eq uuid)
                .map { row ->
                    User(
                        id = row[Users.id],
                        login = row[Users.login],
                        passwordHash = row[Users.passwordHash],
                    )
                }
                .singleOrNull()
        }
    }

    override fun getUserByLogin(login: String): User? {
        return transaction {
            Users.select( Users.id, Users.login, Users.passwordHash ).where(Users.login eq login)
                .map { row ->
                    User(
                        id = row[Users.id],
                        login = row[Users.login],
                        passwordHash = row[Users.passwordHash],
                    )
                }
                .singleOrNull()
        }
    }

    override fun isUserExistsByLogin(login: String): Boolean {
        return transaction {
            !Users.select( Users.login ).where(Users.login eq login).empty()
        }
    }

    override fun getComputer(): UUID? {
        return transaction {
            Users.select(Users.id).where(Users.login eq "computer").map { it[Users.id] }.singleOrNull()
        }
    }

}