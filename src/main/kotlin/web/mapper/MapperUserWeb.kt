package web.mapper

import domain.model.User
import web.model.UserDTO

class MapperUserWeb {
    companion object {
        fun fromDomainToWeb(user: User): UserDTO {
            return UserDTO(
                id = user.id.toString(),
                login = user.login
            )
        }

        fun fromDomainToWeb(users: List<User>): List<UserDTO> {
            return users.map { fromDomainToWeb(it) }
        }
    }
}