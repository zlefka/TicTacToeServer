package domain.repository

import domain.model.User
import java.util.UUID
import kotlin.uuid.Uuid

interface UserRepository {
    fun saveUser(user: User)
    fun getUserById(uuid: UUID): User?
    fun getUserByLogin(login: String): User?
    fun isUserExistsByLogin(login: String): Boolean
}