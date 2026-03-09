package web.route

import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import web.model.UserDTO
import java.util.UUID

fun Route.aboutMe(userService: UserService) {
    get("/user/me") {
        val principal = call.principal<UserIdPrincipal>()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val userId = UUID.fromString(principal.name)

        val user = userService.getUserById(userId)
            ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

        call.respond(UserDTO(user.id.toString(), user.login))
    }
}