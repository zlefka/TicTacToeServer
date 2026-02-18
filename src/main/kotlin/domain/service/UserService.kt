package domain.service

import domain.model.User
import domain.repository.UserRepository
import java.security.MessageDigest
import java.util.UUID

class UserService(private val userRepository: UserRepository) {
    fun register(login: String, password: String): Boolean {
        if(userRepository.isUserExistsByLogin(login)) {
            return false
        } else {
            val id = UUID.randomUUID()
            val hashPass = hashPassword(password)
            val newUser = User(id, login, hashPass)
            userRepository.saveUser(newUser)
            return true
        }
    }

    fun authenticate(login: String, password: String): UUID? {
        val user = userRepository.getUserByLogin(login) ?: return null
        val hashPass = hashPassword(password)
        return if(user.passwordHash == hashPass) user.id else null
    }

    fun getUserById(id: UUID): User? {
        return userRepository.getUserById(id)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") {"%02x".format(it)}
    }
}